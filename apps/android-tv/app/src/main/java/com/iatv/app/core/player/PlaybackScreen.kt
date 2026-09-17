package com.iatv.app.core.player

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.focusable
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.ui.PlayerView
import androidx.media3.common.C
import androidx.media3.ui.TrackSelectionDialogBuilder
import com.iatv.app.core.ui.TvAction as Button
import com.iatv.app.core.model.ContentCard
import com.iatv.app.core.network.TvStore
import com.iatv.app.core.network.NetworkState

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable fun PlaybackScreen(item: ContentCard, repository: TvStore, network: NetworkState, onBack: () -> Unit) {
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val controller = remember(item.id) { PlaybackController(context.applicationContext, item, repository, scope) }
    val state by controller.state.collectAsStateWithLifecycle()
    var overlay by remember { mutableStateOf(true) }
    val hostFocus = remember { FocusRequester() }
    var playerView by remember { mutableStateOf<PlayerView?>(null) }
    var toolbarFocused by remember { mutableStateOf(false) }
    val retryFocus = remember { FocusRequester() }
    LaunchedEffect(state) { if(state is PlayerState.Error || state == PlayerState.Ended) retryFocus.requestFocus() }
    var epg by remember { mutableStateOf("") }
    var trackDialog by remember { mutableStateOf<android.app.Dialog?>(null) }
    DisposableEffect(controller.player) { onDispose { trackDialog?.dismiss(); trackDialog = null } }
    BackHandler(onBack = onBack)
    DisposableEffect(controller, owner) {
        val observer = LifecycleEventObserver { _, event -> when(event) {
            Lifecycle.Event.ON_START -> controller.start()
            Lifecycle.Event.ON_PAUSE -> controller.save()
            Lifecycle.Event.ON_STOP, Lifecycle.Event.ON_DESTROY -> controller.stop()
            else -> Unit
        } }
        owner.lifecycle.addObserver(observer)
        if(owner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) controller.start()
        onDispose { owner.lifecycle.removeObserver(observer); controller.stop() }
    }
    LaunchedEffect(network) { if(network == NetworkState.Connected || network == NetworkState.Limited) controller.networkRestored() }
    LaunchedEffect(item.id) {
        if(item.kind == "channel") try {
            val guide = repository.api.epg(item.id)
            val now = java.time.Instant.now()
            val current = guide.firstOrNull { now >= java.time.Instant.parse(it.startAt) && now < java.time.Instant.parse(it.endAt) }
            current?.let { program ->
                val zone = java.time.ZoneId.of("America/Sao_Paulo")
                epg = "${program.title} • ${java.time.Instant.parse(program.startAt).atZone(zone).toLocalTime()} — ${java.time.Instant.parse(program.endAt).atZone(zone).toLocalTime()}\nA seguir: ${guide.getOrNull(guide.indexOf(program) + 1)?.title ?: "Sem programação"}"
            }
        } catch(cancelled: kotlinx.coroutines.CancellationException) { throw cancelled } catch(_: Exception) { epg = "Guia temporariamente indisponível" }
    }
    Column(Modifier.fillMaxSize().padding(24.dp).focusRequester(hostFocus).onPreviewKeyEvent { event ->
        val wakeKey = event.key in listOf(Key.DirectionUp, Key.DirectionDown, Key.DirectionLeft, Key.DirectionRight, Key.DirectionCenter, Key.Enter)
        if(event.type == KeyEventType.KeyDown && wakeKey && !overlay) {
            overlay = true
            playerView?.let { view -> view.showController(); view.post { view.requestFocus() } }
            true
        } else false
    }.focusable()) {
        if(overlay || toolbarFocused || state is PlayerState.Error || state is PlayerState.Recovering || state == PlayerState.Ended) {
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) { Button(onClick = onBack) { Text("Voltar") }; Text(item.title) }
            if(epg.isNotEmpty()) Text(epg)
        }
        when(val value = state) {
            PlayerState.Preparing, PlayerState.Buffering -> Text("Carregando reprodução…")
            is PlayerState.Recovering -> Text("Reconectando… tentativa ${value.attempt}/3")
            is PlayerState.Error -> {
                Text("Não foi possível continuar a reprodução. Verifique a conexão ou tente outro conteúdo.")
                Button(onClick = { controller.retry() }, modifier = Modifier.focusRequester(retryFocus)) { Text("Tentar novamente") }
            }
            PlayerState.Ended -> { Text("Reprodução concluída."); Button(onClick = { controller.retry() }, modifier = Modifier.focusRequester(retryFocus)) { Text("Assistir novamente") } }
            else -> Unit
        }
        if(network == NetworkState.Disconnected) Text("Sem conexão com a internet.")
        val player = controller.player
        if(player != null) {
            if(overlay || toolbarFocused) Row(modifier = Modifier.onFocusChanged { toolbarFocused = it.hasFocus }, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("Áudio" to C.TRACK_TYPE_AUDIO, "Legenda" to C.TRACK_TYPE_TEXT, "Qualidade" to C.TRACK_TYPE_VIDEO).forEach { (label, type) ->
                    Button(onClick = {
                        trackDialog?.dismiss()
                        trackDialog = TrackSelectionDialogBuilder(context, label, player, type).build().also { it.show() }
                    }) { Text(label) }
                }
            }
            AndroidView(factory = { PlayerView(it).apply {
                playerView = this
                useController = true; controllerShowTimeoutMs = 5000; isFocusable = true
                setShowSubtitleButton(true)
                setControllerVisibilityListener(PlayerView.ControllerVisibilityListener { visibility ->
                    overlay = visibility == android.view.View.VISIBLE
                    if(!overlay) post { if(isAttachedToWindow && !toolbarFocused) hostFocus.requestFocus() }
                })
                requestFocus()
            } }, update = { view ->
                if(view.player !== player) {
                    view.player = player
                    view.post { if(view.player === player) { view.requestFocus(); view.showController() } }
                }
            }, onRelease = { it.setControllerVisibilityListener(null as PlayerView.ControllerVisibilityListener?); it.player = null; playerView = null }, modifier = Modifier.fillMaxWidth().weight(1f))
        }
    }
}

