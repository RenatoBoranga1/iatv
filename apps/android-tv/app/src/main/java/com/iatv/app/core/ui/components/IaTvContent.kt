package com.iatv.app.core.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.iatv.app.core.designsystem.*
import com.iatv.app.core.model.*
import com.iatv.app.core.network.TvApi
import com.iatv.app.core.network.TvStore
import com.iatv.app.feature.live.LiveGuide
import kotlinx.coroutines.CancellationException

@Composable fun IaTvPosterCard(item: ContentCard, modifier: Modifier = Modifier, onFocus: () -> Unit = {}, onClick: () -> Unit) = ContentTile(item,true,modifier,onFocus,onClick)
@Composable fun IaTvLandscapeCard(item: ContentCard, modifier: Modifier = Modifier, onFocus: () -> Unit = {}, onClick: () -> Unit) = ContentTile(item,false,modifier,onFocus,onClick)
@Composable fun IaTvContinueCard(item: ContentCard, progress: WatchProgress?, modifier: Modifier = Modifier, onClick: () -> Unit) {
    ContentTile(item,false,modifier,{},onClick) {
        if(progress!=null) Column(Modifier.padding(horizontal=12.dp).padding(bottom=12.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
            Text("${((progress.durationMs-progress.positionMs).coerceAtLeast(0)+59_999)/60_000} min restantes",style=MaterialTheme.typography.labelMedium,color=IaTvColors.Muted)
            LinearProgressIndicator(progress={ (progress.percentage/100).toFloat().coerceIn(0f,1f) },modifier=Modifier.fillMaxWidth(),color=IaTvColors.Cyan,trackColor=IaTvColors.Raised)
        }
    }
}
@Composable fun IaTvLiveCard(item: ContentCard, modifier: Modifier = Modifier, api: TvApi? = null, onClick: () -> Unit) {
    var programs by remember(item.id) { mutableStateOf<List<EpgProgram>>(emptyList()) }
    var loading by remember(item.id) { mutableStateOf(api != null) }
    LaunchedEffect(item.id, api) {
        if (api != null) {
            try { programs = api.epg(item.id) }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { programs = emptyList() }
            finally { loading = false }
        }
    }
    ContentTile(item, false, modifier, {}, onClick) {
        if (api != null) Column(Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)) {
            if (loading) Text("Carregando programação…", color = IaTvColors.Muted, style = MaterialTheme.typography.bodyMedium)
            else LiveGuide(programs, compact = true)
        }
    }
}

@Composable private fun ContentTile(item: ContentCard, poster: Boolean, modifier: Modifier, onFocus: () -> Unit, onClick: () -> Unit, footer: @Composable () -> Unit = {}) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if(focused) IaTvMotion.FocusScale else 1f,tween(IaTvMotion.FocusMillis),label="cardFocus")
    Column(modifier.width(if(poster) IaTvSpacing.PosterWidth else IaTvSpacing.LandscapeWidth)
        .graphicsLayer { scaleX=scale; scaleY=scale; shadowElevation=if(focused) 12.dp.toPx() else 0f; shape=IaTvShapes.Card }
        .onFocusChanged { focused=it.isFocused; if(it.isFocused) onFocus() }
        .border(if(focused) 2.dp else 1.dp,if(focused) IaTvColors.Cyan else IaTvColors.Border,IaTvShapes.Card)
        .background(IaTvColors.Surface,IaTvShapes.Card).clip(IaTvShapes.Card)
        .clickable(role=Role.Button,onClick=onClick)
        .semantics(mergeDescendants=true) { contentDescription="${item.title}, ${item.category}" }) {
        Box(Modifier.fillMaxWidth().height(if(poster) 190.dp else 112.dp)) {
            IaTvBackdrop(Modifier.fillMaxSize(),id=item.id,thumbnail=true)
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(IaTvColors.Transparent,IaTvColors.Background.copy(alpha=.25f),IaTvColors.Background.copy(alpha=.98f)))))
            Text(if(item.kind=="channel") "AO VIVO • DEMO" else item.badge,
                Modifier.align(Alignment.TopStart).padding(10.dp).background(if(item.kind=="channel") IaTvColors.Live else IaTvColors.Surface, IaTvShapes.Pill).padding(horizontal=6.dp,vertical=2.dp),style=MaterialTheme.typography.labelMedium,color=IaTvColors.Text,maxLines=1)
            Column(Modifier.align(Alignment.BottomStart).padding(10.dp),verticalArrangement=Arrangement.spacedBy(2.dp)) {
                Text(item.title,style=MaterialTheme.typography.titleMedium,maxLines=1,overflow=TextOverflow.Ellipsis)
                Text(item.category,style=MaterialTheme.typography.labelMedium,color=IaTvColors.Muted,maxLines=1,overflow=TextOverflow.Ellipsis)
            }
        }
        footer()
    }
}

@Composable fun IaTvSectionTitle(title: String, count: Int, onSeeAll: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth().padding(horizontal=6.dp),verticalAlignment=Alignment.CenterVertically) {
        Text(title,style=MaterialTheme.typography.titleLarge,modifier=Modifier.weight(1f))
        if(onSeeAll!=null) IaTvActionButton("Ver todos ›",onSeeAll)
        else if(count>0) Text(if(count==1) "1 título" else "$count títulos",style=MaterialTheme.typography.labelMedium,color=IaTvColors.Muted)
    }
}

@Composable fun IaTvContentRow(section: HomeSection, state: LazyListState, restoreKey: String?, onRestored: () -> Unit, liveApi: TvApi? = null, landscape: Boolean = false, store: TvStore? = null, onSeeAll: (() -> Unit)? = null, onOpen: (ContentCard,String) -> Unit) {
    Column(verticalArrangement=Arrangement.spacedBy(4.dp)) {
        IaTvSectionTitle(section.title,section.items.size,onSeeAll)
        if(section.items.isEmpty()) {
            Text(if(section.id=="continue") "Seu próximo play começa aqui." else "Seus favoritos e descobertas aparecem aqui.",color=IaTvColors.Muted,
                style=MaterialTheme.typography.bodyMedium,modifier=Modifier.padding(horizontal=6.dp,vertical=10.dp))
        } else LazyRow(state=state,horizontalArrangement=Arrangement.spacedBy(16.dp),contentPadding=PaddingValues(10.dp)) {
            items(section.items,key={it.id}) { item ->
                val key="${section.id}/${item.id}"
                val requester=remember { FocusRequester() }
                LaunchedEffect(restoreKey) { if(restoreKey==key) { withFrameNanos {}; requester.requestFocus(); onRestored() } }
                val modifier=Modifier.focusRequester(requester)
                if(section.id=="continue") IaTvContinueCard(item,store?.progress(item.id),modifier) { onOpen(item,key) }
                else when(item.kind) {
                    "movie", "series" -> if(landscape) IaTvLandscapeCard(item,modifier,onClick={onOpen(item,key)}) else IaTvPosterCard(item,modifier,onClick={onOpen(item,key)})
                    "channel" -> IaTvLiveCard(item,modifier,api=liveApi,onClick={onOpen(item,key)})
                    else -> IaTvLandscapeCard(item,modifier,onClick={onOpen(item,key)})
                }
            }
        }
    }
}

@Composable fun IaTvHero(item: ContentCard, favorite: Boolean, onPlay: () -> Unit, onFavorite: () -> Unit, onDetails: () -> Unit,
    modifier: Modifier = Modifier, playModifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().height(IaTvSpacing.Hero).clip(IaTvShapes.Card)) {
        IaTvBackdrop(Modifier.fillMaxSize(),id=item.id)
        Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(IaTvColors.Background, IaTvColors.Background.copy(alpha=.92f), IaTvColors.Transparent))))
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(IaTvColors.Transparent,IaTvColors.Transparent,IaTvColors.Background))))
        Column(Modifier.fillMaxHeight().fillMaxWidth(.78f).padding(16.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
            IaTvBadge("EM DESTAQUE • ${item.badge}")
            Text(item.title,style=MaterialTheme.typography.displaySmall,maxLines=2,overflow=TextOverflow.Ellipsis)
            Text("${item.category}${if(item.durationMinutes>0) "  •  ${item.durationMinutes} min" else ""}",style=MaterialTheme.typography.labelMedium,color=IaTvColors.Cyan)
            Text(item.overview,style=MaterialTheme.typography.bodyMedium,color=IaTvColors.Muted,maxLines=2,overflow=TextOverflow.Ellipsis)
            Spacer(Modifier.weight(1f))
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                IaTvActionButton("Assistir agora",onPlay,playModifier,primary=true,enabled=item.playable)
                IaTvActionButton(if(favorite) "✓ Na lista" else "+ Minha Lista",onFavorite)
                IaTvActionButton("Mais informações",onDetails)
            }
        }
    }
}
