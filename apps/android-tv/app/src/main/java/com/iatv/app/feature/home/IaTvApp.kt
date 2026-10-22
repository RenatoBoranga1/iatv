package com.iatv.app.feature.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iatv.app.core.designsystem.*
import com.iatv.app.core.model.ContentCard
import com.iatv.app.core.network.NetworkState
import com.iatv.app.core.player.PlaybackScreen
import com.iatv.app.core.ui.ScreenStatus
import com.iatv.app.core.ui.components.*
import com.iatv.app.feature.assistant.AssistantHeader
import com.iatv.app.feature.details.DetailsScreen
import com.iatv.app.feature.profile.ProfileScreen
import com.iatv.app.feature.search.SearchHeader
import com.iatv.app.feature.search.*
import com.iatv.app.feature.settings.SettingsScreen
import java.time.LocalDate

private val cardSaver=Saver<ContentCard?,String>(save={com.google.gson.Gson().toJson(it)},restore={com.google.gson.Gson().fromJson(it,ContentCard::class.java)})
private val scrollSaver=mapSaver(
    save={ states: MutableMap<String,LazyListState> -> states.mapValues { arrayListOf(it.value.firstVisibleItemIndex,it.value.firstVisibleItemScrollOffset) } },
    restore={ values -> values.mapValues { (_,value) -> val pair=value as List<*>; LazyListState(pair[0] as Int,pair[1] as Int) }.toMutableMap() }
)

@Composable fun IaTvApp(model: TvViewModel) {
    var page by rememberSaveable { mutableStateOf("Início") }
    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf("Todos") }
    var date by rememberSaveable { mutableStateOf(LocalDate.now(java.time.ZoneId.of("America/Sao_Paulo")).toString()) }
    var detail by rememberSaveable(stateSaver=cardSaver) { mutableStateOf<ContentCard?>(null) }
    var playing by rememberSaveable(stateSaver=cardSaver) { mutableStateOf<ContentCard?>(null) }
    var lastCard by rememberSaveable { mutableStateOf("") }
    var restoreKey by rememberSaveable { mutableStateOf<String?>(null) }
    var favoritesVersion by remember { mutableIntStateOf(0) }
    val scrollStates=rememberSaveable(saver=scrollSaver) { mutableMapOf<String,LazyListState>() }
    val first=remember { FocusRequester() }
    val heroFocus=remember { FocusRequester() }
    val inputMode=LocalInputModeManager.current
    val state by model.state.collectAsStateWithLifecycle()
    val network by model.network.collectAsStateWithLifecycle()
    fun load() { model.load(page,query,date) }
    fun navigate(destination: String) { detail=null; restoreKey=null; page=destination; query=""; filter="Todos"; if(destination!in listOf("Perfil","Configurações")) load() }
    fun ask(item: ContentCard) { navigate("Assistente IA"); query="Recomende algo como ${item.title}"; load() }
    fun closeDetail() { detail=null; model.refreshLocal(); favoritesVersion++; restoreKey=lastCard }
    LaunchedEffect(Unit) { load(); inputMode.requestInputMode(InputMode.Keyboard); withFrameNanos {}; if(detail==null && playing==null) first.requestFocus() }
    LaunchedEffect(restoreKey,state.loading,detail,playing) {
        if(restoreKey!=null && !state.loading && detail==null && playing==null) {
            withFrameNanos {}
            if(restoreKey=="hero" && featuredContent(state.sections)!=null) { heroFocus.requestFocus();restoreKey=null }
            else if(!hasFocusTarget(restoreKey!!,state.sections)) { first.requestFocus();restoreKey=null }
        }
    }
    BackHandler(playing==null && (detail!=null || page!="Início")) { if(detail!=null) closeDetail() else navigate("Início") }
    if(playing!=null) {
        PlaybackScreen(playing!!,model.repository,network) { playing=null;model.refreshLocal();if(detail==null) restoreKey=lastCard }
        return
    }
    Row(Modifier.fillMaxSize().background(IaTvColors.Background)) {
        IaTvNavigationRail(page,first,::navigate)
        Column(Modifier.weight(1f).fillMaxHeight().padding(end=IaTvSpacing.Page,start=16.dp)) {
            IaTvTopBar(if(detail==null) page else "Detalhes",network==NetworkState.Disconnected,{navigate("Busca")},{navigate("Perfil")})
            if(detail!=null) {
                DetailsScreen(detail!!,model.repository,::closeDetail,{playing=detail},{ask(detail!!)},
                    related=state.sections.flatMap { it.items }.distinctBy { it.id }.filter { it.id!=detail!!.id && it.kind==detail!!.kind }.take(12),
                    onRelated={detail=it})
            } else if(page=="Perfil") ProfileScreen { navigate("Minha Lista") }
            else if(page=="Configurações") SettingsScreen(model.repository.api)
            else {
                val hero=if(page=="Início") featuredContent(state.sections) else null
                val sections=if(page=="Busca" || page=="TV ao Vivo") state.sections.map { it.copy(items=filterCatalog(it.items,filter,page=="TV ao Vivo")) }
                    else if(page=="Início") state.sections.sortedBy { listOf("live","movies","series","continue","recommended","trending","sports","youtube","favorites").indexOf(it.id).let { index -> if(index<0) Int.MAX_VALUE else index } }
                    else state.sections
                LazyColumn(state=scrollStates.getOrPut("vertical/$page") { LazyListState() },contentPadding=PaddingValues(bottom=32.dp,top=8.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
                    if(page=="Assistente IA") item(key="assistant") { AssistantHeader(query,{query=it.take(500)},::load,{query=it;load()}) }
                    else if(page=="Busca") item(key="search") {
                        Column(verticalArrangement=Arrangement.spacedBy(16.dp)) {
                            Text("Encontre seu próximo favorito",style=MaterialTheme.typography.headlineLarge)
                            SearchHeader(query,{query=it.take(120)},::load)
                        }
                    }
                    else if(page!="Início") item(key="title") {
                        Column(verticalArrangement=Arrangement.spacedBy(6.dp)) {
                            Text(page,style=MaterialTheme.typography.headlineLarge)
                            Text(when(page){"TV ao Vivo"->"Entre na programação. Descubra o que está no ar.";"Minha Lista"->"Tudo o que você guardou para depois.";"Jogos do Dia"->"Acompanhe a agenda dos seus próximos jogos.";else->"Histórias e descobertas para o seu momento."},color=IaTvColors.Muted)
                        }
                    }
                    if(page=="Busca" || page=="TV ao Vivo") item(key="filters") {
                        IaTvFilters(if(page=="Busca") searchFilters else liveFilters,filter) { filter=it }
                    }
                    if(page=="Jogos do Dia") item(key="date") { Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                        IaTvActionButton("Dia anterior",{date=LocalDate.parse(date).minusDays(1).toString();load()})
                        Text(date,Modifier.padding(12.dp),color=IaTvColors.Muted)
                        IaTvActionButton("Próximo dia",{date=LocalDate.parse(date).plusDays(1).toString();load()})
                    } }
                    if(network==NetworkState.Disconnected || state.status==ScreenStatus.Offline || state.status==ScreenStatus.Error) item(key="error") {
                        IaTvErrorState(network==NetworkState.Disconnected || state.status==ScreenStatus.Offline,::load,compact=sections.any { it.items.isNotEmpty() })
                    }
                    if(state.loading) item(key="loading") { IaTvLoadingCard() }
                    if(hero!=null) item(key="hero") {
                        val favorite=remember(hero.id,favoritesVersion) { hero.id in model.repository.favorites() }
                        IaTvHero(hero,favorite,{lastCard="hero";playing=hero},{model.repository.toggleFavorite(hero.id);favoritesVersion++;model.refreshLocal()},{lastCard="hero";detail=hero},playModifier=Modifier.focusRequester(heroFocus))
                    }
                    if(!state.loading && state.status==ScreenStatus.Empty && page!="Assistente IA") item(key="empty") {
                        val message=emptyMessage(page); IaTvEmptyState(message.first,if(page=="Busca" && query.isNotBlank()) "Nenhum resultado. Tente outro título ou gênero." else message.second)
                    }
                    if(!state.loading && state.status==ScreenStatus.Content && sections.all { it.items.isEmpty() }) item(key="filter-empty") {
                        IaTvEmptyState("Nenhum título nesta categoria", "Escolha outro filtro para explorar o catálogo disponível.")
                    }
                    if(page=="Busca" || page=="TV ao Vivo") {
                        sections.forEach { section -> iaTvGrid(section,restoreKey,{restoreKey=null},if(page=="TV ao Vivo") model.repository.api else null) { item,key -> lastCard=key; detail=item } }
                    } else items(sections,key={it.id}) { section ->
                        if(page=="Assistente IA") Column(verticalArrangement=Arrangement.spacedBy(16.dp)) {
                            IaTvEmptyState("Uma ideia para você",section.title)
                            if(section.items.isNotEmpty()) IaTvContentRow(section.copy(title="Para o seu momento"),scrollStates.getOrPut("$page/${section.id}"){LazyListState()},restoreKey,{restoreKey=null}) { item,key->lastCard=key;detail=item }
                        } else if(section.items.isNotEmpty()) {
                            val destination=if(page=="Início") mapOf("live" to "TV ao Vivo","movies" to "Filmes","series" to "Séries","favorites" to "Minha Lista","youtube" to "YouTube")[section.id] else null
                            IaTvContentRow(section,scrollStates.getOrPut("$page/${section.id}/$filter"){LazyListState()},restoreKey,{restoreKey=null},liveApi=if(page=="TV ao Vivo") model.repository.api else null,landscape=page=="Início",store=model.repository,onSeeAll=destination?.let { {navigate(it)} }) { item,key->lastCard=key;detail=item }
                        }
                    }
                }
            }
        }
    }
}
