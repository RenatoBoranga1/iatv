package com.iatv.app.feature.search

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import com.iatv.app.core.designsystem.*
import com.iatv.app.core.ui.components.IaTvActionButton

@Composable fun SearchHeader(query: String, onQuery: (String) -> Unit, onSubmit: () -> Unit, assistant: Boolean = false) {
    var focused by remember { mutableStateOf(false) }
    Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(value=query,onValueChange=onQuery,singleLine=true,
            modifier=Modifier.weight(1f).onFocusChanged { focused=it.hasFocus },
            placeholder={ Text(if(assistant) "Conte o que você quer assistir…" else "Títulos, gêneros e canais…") },
            label={ Text(if(assistant) "Pergunte à IA TV" else "Buscar no catálogo") },
            textStyle=MaterialTheme.typography.bodyLarge,shape=IaTvShapes.Button,
            colors=OutlinedTextFieldDefaults.colors(focusedBorderColor=if(assistant) IaTvColors.Purple else IaTvColors.Cyan,
                unfocusedBorderColor=IaTvColors.Border,focusedContainerColor=IaTvColors.Raised,unfocusedContainerColor=IaTvColors.Surface),
            supportingText=if(focused) { { Text("Use o teclado para escrever e selecione Buscar ou Enviar.",style=MaterialTheme.typography.labelMedium) } } else null)
        IaTvActionButton(if(assistant) "Enviar" else "Buscar",onSubmit,Modifier.padding(top=8.dp),primary=true)
    }
}
