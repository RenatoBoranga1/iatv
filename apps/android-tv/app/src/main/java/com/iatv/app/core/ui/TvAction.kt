package com.iatv.app.core.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.iatv.app.core.designsystem.IaTvColors
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
            containerColor = IaTvColors.Surface,
            contentColor = IaTvColors.Text,
            focusedContainerColor = IaTvColors.Cyan,
            focusedContentColor = IaTvColors.Background,
            disabledContainerColor = IaTvColors.Surface,
            disabledContentColor = IaTvColors.Muted
        )) {
        CompositionLocalProvider(androidx.compose.material3.LocalContentColor provides androidx.tv.material3.LocalContentColor.current) {
            if(loading) androidx.compose.material3.Text("Carregando…") else content()
        }
    }
}
