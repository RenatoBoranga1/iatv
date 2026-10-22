package com.iatv.app.core.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.iatv.app.core.designsystem.*

val iaTvDestinations = listOf("Início", "TV ao Vivo", "Filmes", "Séries", "YouTube", "Jogos do Dia", "Minha Lista", "Busca", "Assistente IA", "Perfil", "Configurações")

@Composable private fun NavigationIcon(index: Int, active: Boolean) {
    val color = if(active) IaTvColors.Cyan else IaTvColors.Muted
    Canvas(Modifier.size(21.dp)) {
        val u = size.width / 24f
        fun line(x1: Float, y1: Float, x2: Float, y2: Float) = drawLine(color, Offset(x1*u,y1*u),Offset(x2*u,y2*u),1.7f*u)
        when(index) {
            0 -> { val path = Path().apply { moveTo(3*u,11*u); lineTo(12*u,3*u); lineTo(21*u,11*u); lineTo(21*u,21*u); lineTo(3*u,21*u); close() }; drawPath(path,color,style=Stroke(1.7f*u)); line(10f,21f,10f,14f); line(10f,14f,14f,14f) }
            7 -> { drawCircle(color,7*u,Offset(10*u,10*u),style=Stroke(1.7f*u)); line(15f,15f,22f,22f) }
            9 -> { drawCircle(color,4*u,Offset(12*u,6*u),style=Stroke(1.7f*u)); drawArc(color,180f,180f,false,Offset(3*u,12*u),androidx.compose.ui.geometry.Size(18*u,16*u),style=Stroke(1.7f*u)) }
            8 -> { val path=Path().apply { moveTo(12*u,1*u); lineTo(15*u,9*u); lineTo(23*u,12*u); lineTo(15*u,15*u); lineTo(12*u,23*u); lineTo(9*u,15*u); lineTo(1*u,12*u); lineTo(9*u,9*u); close() }; drawPath(path,color,style=Stroke(1.7f*u)) }
            6 -> { line(5f,3f,19f,3f); line(5f,3f,5f,22f); line(19f,3f,19f,22f); line(5f,22f,12f,16f); line(12f,16f,19f,22f) }
            10 -> { drawCircle(color,8*u,center,style=Stroke(1.7f*u)); drawCircle(color,3*u,center,style=Stroke(1.7f*u)); line(12f,0f,12f,4f); line(12f,20f,12f,24f); line(0f,12f,4f,12f); line(20f,12f,24f,12f) }
            else -> { drawRoundRect(color,Offset(2*u,4*u),androidx.compose.ui.geometry.Size(20*u,15*u),androidx.compose.ui.geometry.CornerRadius(2*u),style=Stroke(1.7f*u))
                when(index) { 1 -> { line(8f,23f,16f,23f); line(12f,19f,12f,23f) }; 2 -> { line(2f,8f,22f,8f); line(7f,4f,10f,8f); line(14f,4f,17f,8f) }; 3 -> { line(6f,22f,18f,22f) }; 5 -> { line(12f,4f,12f,19f); drawCircle(color,3*u,center,style=Stroke(u)) }; else -> { val p=Path().apply{moveTo(10*u,8*u);lineTo(16*u,12*u);lineTo(10*u,16*u);close()};drawPath(p,color) } }
            }
        }
    }
}

@Composable fun IaTvNavigationRail(page: String, first: FocusRequester, onNavigate: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val width by animateDpAsState(if(expanded) IaTvSpacing.RailExpanded else IaTvSpacing.Rail, tween(IaTvMotion.FocusMillis), label="rail")
    Column(Modifier.width(width).fillMaxHeight().background(IaTvColors.Background)
        .onFocusChanged { expanded = it.hasFocus }.focusGroup().padding(horizontal = 8.dp, vertical = 12.dp)) {
        Row(Modifier.align(Alignment.CenterHorizontally),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            IaTvLogo(Modifier.size(46.dp))
            if(expanded) Text("IA TV",style=MaterialTheme.typography.headlineLarge,color=IaTvColors.Text)
        }
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(3.dp), contentPadding = PaddingValues(vertical=4.dp)) {
            items(iaTvDestinations, key = { it }) { destination ->
                var focused by remember { mutableStateOf(false) }
                val selected = destination == page
                Row(Modifier.fillMaxWidth().height(37.dp)
                    .then(if(destination == "Início") Modifier.focusRequester(first) else Modifier)
                    .onFocusChanged { focused = it.isFocused }
                    .border(if(focused) 2.dp else 0.dp, if(focused) IaTvColors.Cyan else IaTvColors.Transparent, IaTvShapes.Button)
                    .background(if(focused) IaTvColors.Raised else if(selected) IaTvColors.Surface else IaTvColors.Transparent, IaTvShapes.Button)
                    .clip(IaTvShapes.Button).clickable(role = Role.Tab) { onNavigate(destination) }
                    .semantics { contentDescription = destination; this.selected = selected }
                    .padding(horizontal=12.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    NavigationIcon(iaTvDestinations.indexOf(destination), selected || focused)
                    if(expanded) Text(destination, style = MaterialTheme.typography.bodyMedium, maxLines=1)
                }
            }
        }
    }
}

@Composable fun IaTvTopBar(page: String, offline: Boolean, onSearch: () -> Unit, onProfile: () -> Unit) {
    Row(Modifier.fillMaxWidth().height(58.dp), verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.spacedBy(12.dp)) {
        Text(page, style=MaterialTheme.typography.titleMedium)
        IaTvBadge(if(offline) "SEM CONEXÃO" else "DEMONSTRAÇÃO")
        Spacer(Modifier.weight(1f))
        IaTvActionButton("Buscar",onSearch)
        IaTvActionButton("Perfil",onProfile)
    }
}
