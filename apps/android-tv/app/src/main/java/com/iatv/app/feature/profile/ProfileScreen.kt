package com.iatv.app.feature.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.iatv.app.core.ui.components.*
import com.iatv.app.core.designsystem.*

@Composable fun ProfileScreen(onFavorites: () -> Unit) {
    Column(verticalArrangement=Arrangement.spacedBy(24.dp)) {
        IaTvBadge("SEU ESPAÇO")
        Text("Feito para o seu momento.",style=MaterialTheme.typography.headlineLarge)
        IaTvEmptyState("Perfil deste dispositivo","Sua lista e seu progresso ficam salvos neste aparelho. Continue de onde parou e guarde suas próximas descobertas.")
        IaTvActionButton("Abrir Minha Lista",onFavorites,primary=true)
    }
}
