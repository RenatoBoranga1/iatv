package com.iatv.app.core.ui.components

import android.graphics.BitmapFactory
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.iatv.app.R
import com.iatv.app.core.designsystem.*

@Composable fun IaTvLogo(modifier: Modifier = Modifier) {
    val resources = LocalContext.current.resources
    // Decode once at 157px, rather than keeping the 1254px source in the rail.
    val bitmap = remember(resources) { BitmapFactory.decodeResource(resources, R.drawable.iatv_logo,
        BitmapFactory.Options().apply { inSampleSize = 8 }).asImageBitmap() }
    Image(bitmap, "IA TV", modifier)
}

@Composable fun IaTvActionButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier,
    primary: Boolean = false, enabled: Boolean = true, selected: Boolean = false) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if(focused) 1.04f else 1f, tween(IaTvMotion.FocusMillis), label = "actionFocus")
    val highlighted = focused || primary
    Box(modifier.heightIn(min = 42.dp).graphicsLayer { scaleX = scale; scaleY = scale }
        .onFocusChanged { focused = it.isFocused }
        .border(if(focused) 2.dp else 1.dp, if(focused) IaTvColors.Text else if(selected) IaTvColors.Cyan else IaTvColors.Border, IaTvShapes.Button)
        .background(if(highlighted && enabled) IaTvColors.Cyan else IaTvColors.Surface, IaTvShapes.Button)
        .clip(IaTvShapes.Button).clickable(enabled = enabled, role = Role.Button, onClick = onClick)
        .semantics { this.selected = selected }
        .padding(horizontal = IaTvSpacing.Medium, vertical = 10.dp), contentAlignment = Alignment.Center) {
        Text(label, color = if(!enabled) IaTvColors.Muted else if(highlighted) IaTvColors.Background else IaTvColors.Text,
            style = MaterialTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable fun IaTvBadge(text: String, ai: Boolean = false) {
    Text(text, color = if(ai) IaTvColors.Purple else IaTvColors.Cyan,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.background(IaTvColors.Surface.copy(alpha = .85f), IaTvShapes.Pill).padding(horizontal = 10.dp, vertical = 4.dp),
        maxLines = 1, overflow = TextOverflow.Ellipsis)
}

/** Original procedural scenery: no remote images, bitmap allocations or third-party art. */
@Composable fun IaTvArtwork(seed: String, modifier: Modifier = Modifier) {
    val accent = IaTvColors.Art[(seed.hashCode() and Int.MAX_VALUE) % IaTvColors.Art.size]
    Canvas(modifier.clipToBounds()) {
        drawRect(Brush.linearGradient(listOf(IaTvColors.Raised, accent, IaTvColors.Background)))
        val center = Offset(size.width * .72f, size.height * .35f)
        drawCircle(Brush.radialGradient(listOf(IaTvColors.Cyan.copy(alpha = .25f), IaTvColors.Transparent), center, size.width * .55f), size.width * .55f, center)
        drawCircle(IaTvColors.Text.copy(alpha = .8f), size.minDimension * .12f, center)
        for(layer in 0..2) {
            val ridge = Path().apply {
                moveTo(0f, size.height)
                lineTo(0f, size.height * (.68f + layer * .08f))
                lineTo(size.width * .25f, size.height * (.38f + layer * .16f))
                lineTo(size.width * .48f, size.height * (.7f + layer * .06f))
                lineTo(size.width * .78f, size.height * (.42f + layer * .15f))
                lineTo(size.width, size.height * (.65f + layer * .09f))
                lineTo(size.width, size.height); close()
            }
            drawPath(ridge, IaTvColors.Background.copy(alpha = .3f + layer * .22f))
        }
    }
}

@Composable fun IaTvEmptyState(title: String, message: String, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().background(IaTvColors.Surface, IaTvShapes.Card).padding(IaTvSpacing.Large),
        verticalArrangement = Arrangement.spacedBy(IaTvSpacing.Small)) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Text(message, color = IaTvColors.Muted, style = MaterialTheme.typography.bodyMedium)
    }
}
@Composable fun IaTvErrorState(offline: Boolean, onRetry: () -> Unit, compact: Boolean = false) {
    if (compact) {
        Row(Modifier.fillMaxWidth().background(IaTvColors.Surface, IaTvShapes.Card)
            .border(1.dp, IaTvColors.Border, IaTvShapes.Card).padding(IaTvSpacing.Medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(IaTvSpacing.Medium)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(if(offline) "Sem conexão" else "Não foi possível atualizar", style = MaterialTheme.typography.titleMedium, color = IaTvColors.Warning)
                Text("Exibindo o conteúdo carregado anteriormente.", style = MaterialTheme.typography.bodyMedium, color = IaTvColors.Muted)
            }
            IaTvActionButton("Tentar novamente", onRetry)
        }
        return
    }
    Column(Modifier.fillMaxWidth().background(IaTvColors.Surface, IaTvShapes.Card).padding(IaTvSpacing.Large),
        verticalArrangement = Arrangement.spacedBy(IaTvSpacing.Medium)) {
        Text(if(offline) "Sem conexão" else "Vamos tentar de novo?", style = MaterialTheme.typography.titleLarge, color = IaTvColors.Warning)
        Text(if(offline) "Verifique sua rede. Seu conteúdo salvo continua por aqui." else "Não conseguimos carregar o conteúdo agora.", color = IaTvColors.Muted)
        IaTvActionButton("Tentar novamente", onRetry)
    }
}
@Composable fun IaTvLoadingCard() {
    // Static skeleton avoids a continuous animation on modest TV hardware.
    Row(horizontalArrangement = Arrangement.spacedBy(IaTvSpacing.Medium), modifier = Modifier.semantics { contentDescription = "Carregando conteúdo" }) {
        repeat(3) { Column(Modifier.width(180.dp).height(146.dp).background(IaTvColors.Surface, IaTvShapes.Card).padding(16.dp)) {
            Spacer(Modifier.weight(1f)); Box(Modifier.fillMaxWidth(.75f).height(12.dp).background(IaTvColors.Raised, IaTvShapes.Pill))
            Spacer(Modifier.height(8.dp)); Box(Modifier.fillMaxWidth(.45f).height(8.dp).background(IaTvColors.Raised, IaTvShapes.Pill))
        } }
    }
}
