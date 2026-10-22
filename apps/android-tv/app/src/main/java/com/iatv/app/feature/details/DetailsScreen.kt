package com.iatv.app.feature.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.iatv.app.core.designsystem.*
import com.iatv.app.core.model.*
import com.iatv.app.core.network.TvStore
import com.iatv.app.core.ui.components.*
import com.iatv.app.feature.live.LiveGuide
import kotlinx.coroutines.CancellationException

@Composable fun DetailsScreen(item: ContentCard, store: TvStore, onBack: () -> Unit, onPlay: () -> Unit, onAi: () -> Unit,
    related: List<ContentCard> = emptyList(), onRelated: (ContentCard) -> Unit = {}) {
    val actionFocus=remember { FocusRequester() }
    LaunchedEffect(item.id) { withFrameNanos {}; actionFocus.requestFocus() }
    var favorite by remember(item.id) { mutableStateOf(item.id in store.favorites()) }
    var epg by remember(item.id) { mutableStateOf<List<EpgProgram>>(emptyList()) }
    LaunchedEffect(item.id) { if(item.kind=="channel") try { epg=store.api.epg(item.id) } catch(cancelled: CancellationException){throw cancelled} catch(_:Exception){epg=emptyList()} }
    Box(Modifier.fillMaxSize()) {
        IaTvBackdrop(Modifier.fillMaxWidth().height(300.dp),id=item.id)
        Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(IaTvColors.Background,IaTvColors.Background.copy(alpha=.93f),IaTvColors.Background.copy(alpha=.3f)))))
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(IaTvColors.Transparent,IaTvColors.Transparent,IaTvColors.Background))))
        LazyColumn(contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
            item { IaTvBadge(item.badge) }
            item { Text(item.title,style=MaterialTheme.typography.displaySmall,modifier=Modifier.fillMaxWidth(.8f)) }
            item { Text("${item.category}${if(item.durationMinutes>0) "  •  ${item.durationMinutes} min" else ""}",color=IaTvColors.Cyan) }
            item { Text(item.overview,style=MaterialTheme.typography.bodyLarge,color=IaTvColors.Muted,modifier=Modifier.fillMaxWidth(.78f)) }
            item { Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                IaTvActionButton(if(item.playable) "Assistir" else "Indisponível",onPlay,if(item.playable) Modifier.focusRequester(actionFocus) else Modifier,primary=true,enabled=item.playable)
                IaTvActionButton(if(favorite) "✓ Na Minha Lista" else "+ Minha Lista",{ store.toggleFavorite(item.id);favorite=!favorite },if(!item.playable) Modifier.focusRequester(actionFocus) else Modifier)
                IaTvActionButton("Perguntar à IA",onAi)
            } }
            if(item.kind=="channel") item { LiveGuide(epg) }
            if(related.isNotEmpty()) item {
                IaTvContentRow(HomeSection("related","Conteúdos relacionados",related),rememberLazyListState(),null,{},landscape=true) { card,_ -> onRelated(card) }
            }
            item { IaTvActionButton("Voltar",onBack) }
        }
    }
}
