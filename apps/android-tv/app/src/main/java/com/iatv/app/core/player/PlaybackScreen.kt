package com.iatv.app.core.player
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.tv.material3.Button
import com.iatv.app.core.model.ContentCard
import com.iatv.app.core.network.TvRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.CancellationException

@Composable fun PlaybackScreen(item: ContentCard, repository: TvRepository, onBack: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val player = remember { ExoPlayer.Builder(context).build() }
    var error by remember { mutableStateOf<String?>(null) }
    var buffering by remember { mutableStateOf(true) }
    var attempt by remember { mutableIntStateOf(0) }
    BackHandler(onBack = onBack)
    LaunchedEffect(item.id, attempt) {
        error = null
        try {
            val source = repository.api.playback(item.id)
            player.setMediaItem(MediaItem.Builder().setUri(source.url).setMimeType(source.mimeType).build())
            player.repeatMode = if(source.isLive) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
            if(!source.isLive) player.seekTo(repository.position(item.id))
            player.prepare(); player.playWhenReady = true
        } catch(cancelled: CancellationException) { throw cancelled }
        catch(_: Exception) { error = "Sinal de teste indisponível. Verifique a API e a mídia de demonstração."; buffering = false }
    }
    LaunchedEffect(player) { while(true) { delay(15000); if(item.kind != "channel") repository.savePosition(item.id, player.currentPosition, player.duration) } }
    DisposableEffect(player, lifecycleOwner) {
        val listener = object: Player.Listener {
            override fun onPlaybackStateChanged(state: Int) { buffering = state == Player.STATE_BUFFERING }
            override fun onPlayerError(exception: PlaybackException) { error = "A reprodução foi interrompida. Verifique a conexão e tente novamente." }
        }
        val observer = LifecycleEventObserver { _, event -> if(event == Lifecycle.Event.ON_STOP) player.pause() }
        player.addListener(listener); lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { if(item.kind != "channel") repository.savePosition(item.id, player.currentPosition, player.duration); lifecycleOwner.lifecycle.removeObserver(observer); player.removeListener(listener); player.release() }
    }
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) { Button(onClick = onBack) { Text("Voltar") }; Text("${item.title} • Sinal de teste próprio") }
        if(buffering) Text("Carregando reprodução…")
        error?.let { Text(it); Button(onClick = { attempt++ }) { Text("Tentar novamente") } }
        AndroidView(factory = { PlayerView(it).apply { this.player = player; useController = true; isFocusable = true; requestFocus() } }, modifier = Modifier.fillMaxWidth().weight(1f))
    }
}
