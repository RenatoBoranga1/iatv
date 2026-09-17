package com.iatv.app.core.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults

/** Bridges TV button focus colors to text used by the shared Material components. */
@Composable fun TvAction(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    content: @Composable RowScope.() -> Unit
) {
    Button(onClick = onClick, modifier = modifier, enabled = enabled && !loading,
        colors = ButtonDefaults.colors(
            containerColor = Color(0xFF1D2738),
            contentColor = Color(0xFFEAF2FA),
            focusedContainerColor = Color(0xFF65E4C5),
            focusedContentColor = Color(0xFF0C101B),
            disabledContainerColor = Color(0xFF182030),
            disabledContentColor = Color(0xFF77849A)
        )) {
        CompositionLocalProvider(androidx.compose.material3.LocalContentColor provides androidx.tv.material3.LocalContentColor.current) {
            if(loading) androidx.compose.material3.Text("Carregando…") else content()
        }
    }
}
