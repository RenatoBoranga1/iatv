package com.iatv.app.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.iatv.app.core.network.TvApi
import com.iatv.app.core.diagnostics.DiagnosticsScreen
import com.iatv.app.core.designsystem.*

@Composable fun SettingsScreen(api: TvApi) {
    Column(verticalArrangement=Arrangement.spacedBy(24.dp)) {
        Text("Configurações",style=MaterialTheme.typography.headlineLarge)
        Text("Informações do aplicativo e conexão, sempre à mão.",color=IaTvColors.Muted)
        DiagnosticsScreen(api)
    }
}
