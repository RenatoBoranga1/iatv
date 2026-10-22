package com.iatv.app.feature.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.iatv.app.core.designsystem.*
import com.iatv.app.core.ui.components.IaTvActionButton

@Composable fun SearchHeader(query: String, onQuery: (String) -> Unit, onSubmit: () -> Unit, assistant: Boolean = false) {
    var focused by remember { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current
    fun submit() { keyboard?.hide(); onSubmit() }
    Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(value=query,onValueChange=onQuery,singleLine=true,
            modifier=Modifier.weight(1f).onFocusChanged { focused=it.hasFocus },
            placeholder={ Text(if(assistant) "Conte o que você quer assistir…" else "Títulos, gêneros e canais…") },
            label={ Text(if(assistant) "Pergunte à IA TV" else "Buscar no catálogo") },
            keyboardOptions=KeyboardOptions(imeAction=if(assistant) ImeAction.Send else ImeAction.Search),
            keyboardActions=KeyboardActions(onSearch={submit()},onSend={submit()}),
            textStyle=MaterialTheme.typography.bodyLarge,shape=IaTvShapes.Button,
            colors=OutlinedTextFieldDefaults.colors(focusedBorderColor=if(assistant) IaTvColors.Purple else IaTvColors.Cyan,
                unfocusedBorderColor=IaTvColors.Border,focusedContainerColor=IaTvColors.Raised,unfocusedContainerColor=IaTvColors.Surface),
            supportingText={ Text(if(focused) "Escreva e confirme no teclado ou no botão ao lado." else if(assistant) "Sugestões a partir do catálogo disponível." else "Explore por título, gênero ou canal.",style=MaterialTheme.typography.labelMedium,color=IaTvColors.Muted) })
        IaTvActionButton(if(assistant) "Enviar" else "Buscar",::submit,Modifier.padding(top=8.dp),primary=true)
    }
}
