package com.iatv.app.core.player

import android.content.Context
import android.os.SystemClock
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.*
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.upstream.DefaultLoadErrorHandlingPolicy
import com.iatv.app.core.model.ContentCard
import com.iatv.app.core.model.WatchProgress
import com.iatv.app.core.network.TvStore
import com.iatv.app.core.diagnostics.DebugTelemetry
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException
import java.net.SocketTimeoutException
import retrofit2.HttpException

/** Main-thread confined. Owns at most one player while its host is STARTED. */
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class PlaybackController(private val context: Context, private val item: ContentCard, private val repository: TvStore, private val scope: CoroutineScope) {
    private val mutable = MutableStateFlow<PlayerState>(PlayerState.Idle)
    val state = mutable.asStateFlow()
    var player: ExoPlayer? by mutableStateOf(null)
        private set
    private var active = false
    private var load: Job? = null
    private var ticker: Job? = null
    private var watchdog: Job? = null
    private val recovery = RecoveryPolicy()
    private var position = repository.position(item.id)
    private var shouldPlay = true
    private var requestedAt = 0L
    private var firstFrame = false
    private var bufferingAt: Long? = null
    private var bufferingCount = 0

    fun start() {
        if(active) return
        active = true
        if(state.value is PlayerState.Error || state.value == PlayerState.Ended) return
        prepare()
    }
    fun retry() {
        if(!active) return
        val ended = state.value == PlayerState.Ended
        release()
        if(ended) position = 0
        recovery.reset(); shouldPlay = true; prepare()
    }
    fun networkRestored() {
        if(active && state.value is PlayerState.Recovering) { load?.cancel(); prepare() }
    }
    fun save() { player?.let { if(item.kind != "channel") repository.savePosition(item.id, it.currentPosition, it.duration, state.value == PlayerState.Ended) } }
    fun stop() {
        active = false
        load?.cancel(); ticker?.cancel(); watchdog?.cancel()
        release()
    }
    private fun release() {
        player?.let {
            position = it.currentPosition.coerceAtLeast(0)
            shouldPlay = it.playWhenReady
            save(); endBuffering(); it.removeListener(listener); it.release()
            DebugTelemetry.event("player_released")
        }
        player = null
    }
    private fun prepare() {
        load?.cancel(); ticker?.cancel(); watchdog?.cancel(); release()
        if(!active) return
        mutable.value = PlayerState.Preparing
        requestedAt = SystemClock.elapsedRealtime(); firstFrame = false
        DebugTelemetry.event("play_requested"); DebugTelemetry.event("playback_start_requested")
        load = scope.launch {
            try {
                val source = repository.api.playback(item.id)
                ensureActive()
                val factory = DefaultMediaSourceFactory(context)
                    .setDataSourceFactory(DefaultHttpDataSource.Factory().setConnectTimeoutMs(10_000).setReadTimeoutMs(15_000))
                    .setLoadErrorHandlingPolicy(DefaultLoadErrorHandlingPolicy(0))
                val created = ExoPlayer.Builder(context).setMediaSourceFactory(factory).build()
                player = created
                DebugTelemetry.event("player_created")
                created.addListener(listener)
                created.setAudioAttributes(AudioAttributes.DEFAULT, true)
                created.setHandleAudioBecomingNoisy(true)
                created.setMediaItem(MediaItem.Builder().setUri(source.url).setMimeType(source.mimeType)
                    .setMediaMetadata(MediaMetadata.Builder().setTitle(item.title).build()).build())
                // Only the legacy MP4 demonstration loops. HLS/DASH live follows its manifest.
                created.repeatMode = if(source.isLive && source.demo && source.mimeType == "video/mp4") Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
                if(!source.isLive) created.seekTo(position)
                created.playWhenReady = shouldPlay
                created.prepare()
                ticker = scope.launch { while(isActive) { delay(WatchProgress.SAVE_INTERVAL_MS); save() } }
            } catch(cancelled: CancellationException) { throw cancelled }
            catch(error: Exception) { fail(classify(error)) }
        }
    }
    private fun fail(code: PlaybackError) {
        if(!active) return
        watchdog?.cancel(); ticker?.cancel(); endBuffering()
        DebugTelemetry.error("play_error", code.name)
        val wait = recovery.nextDelay(code)
        release()
        if(wait == null) { mutable.value = PlayerState.Error(code); return }
        mutable.value = PlayerState.Recovering(recovery.attempts, wait)
        load = scope.launch { delay(wait); prepare() }
    }
    private fun endBuffering() {
        bufferingAt?.let { DebugTelemetry.timing("buffering_duration_ms", SystemClock.elapsedRealtime() - it); DebugTelemetry.event("buffering_ended") }
        bufferingAt = null
    }
    private val listener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            watchdog?.cancel()
            if(playbackState != Player.STATE_BUFFERING) endBuffering()
            when(playbackState) {
                Player.STATE_BUFFERING -> {
                    if(bufferingAt == null) {
                        bufferingAt = SystemClock.elapsedRealtime(); bufferingCount++
                        DebugTelemetry.event("buffering_started"); DebugTelemetry.timing("buffering_count", bufferingCount.toLong())
                    }
                    mutable.value = PlayerState.Buffering
                    watchdog = scope.launch { delay(30_000); fail(PlaybackError.TIMEOUT) }
                }
                Player.STATE_READY -> mutable.value = if(player?.isPlaying == true) PlayerState.Playing else PlayerState.Paused
                Player.STATE_ENDED -> { mutable.value = PlayerState.Ended; save(); DebugTelemetry.event("play_completed") }
            }
        }
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if(player?.playbackState == Player.STATE_READY) mutable.value = if(isPlaying) PlayerState.Playing else PlayerState.Paused
        }
        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) { shouldPlay = playWhenReady; if(!playWhenReady) save() }
        override fun onPositionDiscontinuity(oldPosition: Player.PositionInfo, newPosition: Player.PositionInfo, reason: Int) { if(reason == Player.DISCONTINUITY_REASON_SEEK) save() }
        override fun onRenderedFirstFrame() {
            if(!firstFrame) {
                firstFrame = true; DebugTelemetry.event("first_frame_rendered"); DebugTelemetry.event("play_started")
                DebugTelemetry.timing("time_to_first_frame_ms", SystemClock.elapsedRealtime() - requestedAt)
            }
        }
        override fun onPlayerError(error: PlaybackException) { DebugTelemetry.error("media3_error", error.errorCodeName); fail(classify(error)) }
    }
    private fun classify(error: Exception): PlaybackError = when(error) {
        is SocketTimeoutException -> PlaybackError.TIMEOUT
        is HttpException -> if(error.code() >= 500 || error.code() == 429) PlaybackError.NETWORK_ERROR else PlaybackError.SOURCE_ERROR
        is PlaybackException -> when(error.errorCode) {
            PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW -> PlaybackError.NETWORK_ERROR
            PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS -> {
                val status = (error.cause as? HttpDataSource.InvalidResponseCodeException)?.responseCode
                if(status == 429 || (status != null && status >= 500) || (item.kind == "channel" && status == 404)) PlaybackError.NETWORK_ERROR else PlaybackError.SOURCE_ERROR
            }
            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> PlaybackError.TIMEOUT
            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED -> PlaybackError.NETWORK_ERROR
            PlaybackException.ERROR_CODE_DECODER_INIT_FAILED, PlaybackException.ERROR_CODE_DECODING_FAILED -> PlaybackError.DECODER_ERROR
            PlaybackException.ERROR_CODE_DECODING_FORMAT_UNSUPPORTED, PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED -> PlaybackError.UNSUPPORTED_FORMAT
            in 2000..3999 -> PlaybackError.SOURCE_ERROR
            else -> PlaybackError.UNKNOWN
        }
        is IOException -> PlaybackError.NETWORK_ERROR
        else -> PlaybackError.UNKNOWN
    }
}

