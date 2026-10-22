package com.iatv.app.feature.assistant

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.iatv.app.core.designsystem.*
import com.iatv.app.core.ui.components.*
import com.iatv.app.feature.search.SearchHeader

@Composable fun AssistantHeader(query: String, onQuery: (String) -> Unit, onSubmit: () -> Unit, onSuggestion: (String) -> Unit) {
    Column(verticalArrangement=Arrangement.spacedBy(16.dp)) {
        Row(Modifier.fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(IaTvColors.Surface,IaTvColors.Raised,IaTvColors.Surface)),IaTvShapes.Card)
            .border(1.dp,IaTvColors.Border,IaTvShapes.Card).padding(24.dp),
            verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(24.dp)) {
            Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(12.dp)) {
                IaTvBadge("✦ SEU ASSISTENTE IA TV",ai=true)
                Text("O que você quer\nassistir hoje?",style=MaterialTheme.typography.headlineLarge)
                Text("Conte o que está com vontade de ver. Encontre a próxima história no catálogo IA TV.",color=IaTvColors.Muted)
            }
            Canvas(Modifier.size(132.dp)) {
                drawCircle(Brush.radialGradient(listOf(IaTvColors.Purple.copy(alpha=.6f),IaTvColors.Blue.copy(alpha=.15f),IaTvColors.Transparent)))
                drawCircle(Brush.linearGradient(listOf(IaTvColors.Cyan,IaTvColors.Purple)),radius=size.minDimension*.29f,style=Stroke(3.dp.toPx()))
                drawCircle(IaTvColors.Cyan.copy(alpha=.5f),radius=size.minDimension*.18f,style=Stroke(1.dp.toPx()))
            }
        }
        SearchHeader(query,onQuery,onSubmit,assistant=true)
        Text("Experimente uma ideia",style=MaterialTheme.typography.titleMedium,color=IaTvColors.Muted)
        LazyRow(horizontalArrangement=Arrangement.spacedBy(10.dp),contentPadding=PaddingValues(4.dp)) {
            items(listOf("Comédia","Futebol hoje","Para a família","Suspense","Lançamentos","Algo leve")) { suggestion ->
                IaTvActionButton(suggestion,{onSuggestion(suggestion)})
            }
        }
    }
}
