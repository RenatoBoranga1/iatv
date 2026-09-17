package com.iatv.app.core.diagnostics

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import com.iatv.app.BuildConfig
import com.iatv.app.core.network.TvApi
import com.iatv.app.core.ui.TvAction
import kotlinx.coroutines.CancellationException

data class DeviceInfo(val manufacturer: String, val model: String, val androidVersion: String, val sdkVersion: Int, val appVersion: String) {
    companion object { fun current() = DeviceInfo(Build.MANUFACTURER, Build.MODEL, Build.VERSION.RELEASE, Build.VERSION.SDK_INT, BuildConfig.VERSION_NAME) }
}

@Composable fun DiagnosticsScreen(api: TvApi) {
    var page by rememberSaveable { mutableIntStateOf(0) }
    var attempt by remember { mutableIntStateOf(0) }
    var apiStatus by remember { mutableStateOf("Verificando…") }
    val info = remember { DeviceInfo.current() }
    val clipboard = LocalClipboardManager.current
    val first = remember { FocusRequester() }
    LaunchedEffect(page) { first.requestFocus() }
    androidx.activity.compose.BackHandler(page > 0) { page-- }
    LaunchedEffect(page, attempt) {
        if(page == 2) {
            apiStatus = "Verificando…"
            apiStatus = try { api.health(); "Conectado" } catch(cancelled: CancellationException) { throw cancelled } catch(_: Exception) { "Indisponível" }
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        when(page) {
            0 -> TvAction(onClick = { page = 1 }, modifier = Modifier.focusRequester(first)) { Text("Sobre") }
            1 -> { Text("IA TV • catálogo de demonstração"); TvAction(onClick = { page = 2 }, modifier = Modifier.focusRequester(first)) { Text("Diagnóstico") } }
            else -> {
                val report = "IA TV\nVersão: ${info.appVersion} (${BuildConfig.VERSION_CODE})\nBuild: ${BuildConfig.GIT_COMMIT} / ${BuildConfig.BUILD_TYPE}\nAndroid: ${info.androidVersion} / SDK ${info.sdkVersion}\nDispositivo: ${info.manufacturer} ${info.model}\nAPI: $apiStatus\nPlayer: Media3"
                Text(report)
                TvAction(onClick = { clipboard.setText(AnnotatedString(report)) }, modifier = Modifier.focusRequester(first)) { Text("Copiar informações") }
                TvAction(onClick = { attempt++ }) { Text("Verificar API novamente") }
            }
        }
        if(page > 0) TvAction(onClick = { page-- }) { Text("Voltar") }
    }
}
