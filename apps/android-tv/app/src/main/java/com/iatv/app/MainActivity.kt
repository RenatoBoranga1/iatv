package com.iatv.app
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Button as TvButton
import com.iatv.app.core.model.ContentCard
import com.iatv.app.core.model.EpgProgram
import com.iatv.app.core.player.PlaybackScreen
import com.iatv.app.core.ui.TvCard
import com.iatv.app.feature.home.TvViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint class MainActivity: ComponentActivity() {
    private val model: TvViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme(colorScheme = darkColorScheme(primary = Color(0xFF65E4C5), background = Color(0xFF0C101B))) { Surface(Modifier.fillMaxSize()) { TvApp(model) } } }
    }
}
private val menu = listOf("Início", "TV ao Vivo", "Filmes", "Séries", "YouTube", "Jogos do Dia", "Minha Lista", "Busca", "Perfil", "Configurações")
@Composable private fun TvApp(model: TvViewModel) {
    var page by rememberSaveable { mutableStateOf("Início") }
    var query by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf(LocalDate.now(java.time.ZoneId.of("America/Sao_Paulo")).toString()) }
    var detail by remember { mutableStateOf<ContentCard?>(null) }
    var playing by remember { mutableStateOf<ContentCard?>(null) }
    var lastCard by rememberSaveable { mutableStateOf("") }
    var restoreFocus by remember { mutableStateOf(false) }
    val verticalState = rememberLazyListState()
    val rowStates = remember { mutableMapOf<String, LazyListState>() }
    val initialFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { initialFocus.requestFocus() }
    val state by model.state.collectAsStateWithLifecycle()
    fun navigate(value: String) { page = value; query = ""; model.load(value, date = date) }
    BackHandler(enabled = playing == null && (detail != null || page != "Início")) { if(detail != null) { detail = null; restoreFocus = true } else navigate("Início") }
    if(playing != null) { PlaybackScreen(playing!!, model.repository) { playing = null }; return }
    Row(Modifier.fillMaxSize().background(Color(0xFF0C101B)).padding(24.dp)) {
        Column(Modifier.width(175.dp).fillMaxHeight()) {
            Text("IA TV", fontSize = 32.sp, color = Color(0xFF65E4C5))
            Text("SEU UNIVERSO DE PLAY", fontSize = 9.sp, color = Color(0xFF9EABBF))
            Spacer(Modifier.height(16.dp))
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(menu) { label -> TvButton(onClick = { detail = null; navigate(label) }, modifier = Modifier.fillMaxWidth().then(if(label == "Início") Modifier.focusRequester(initialFocus) else Modifier)) { Text(if(page == label) "• $label" else label, fontSize = 13.sp) } }
            }
            TvButton(onClick = { detail = null; navigate("Assistente IA") }, modifier = Modifier.fillMaxWidth()) { Text("✦ Assistente IA", fontSize = 13.sp) }
        }
        Spacer(Modifier.width(28.dp))
        Box(Modifier.weight(1f).fillMaxHeight()) {
            if(detail != null) {
                Detail(detail!!, model, onBack = { detail = null; restoreFocus = true }, onPlay = { playing = detail }, onAi = { val title = detail!!.title; detail = null; navigate("Assistente IA"); query = "Recomende um filme como $title"; model.load(page, query) })
            } else Column {
                Text(if(page == "Início") "Olá.\nO que você quer assistir?" else page, fontSize = 30.sp)
                Text("Tudo o que você quer assistir, em um só lugar. • DEMONSTRAÇÃO", fontSize = 11.sp, color = Color(0xFF9EABBF))
                Spacer(Modifier.height(12.dp))
                if(page == "Busca" || page == "Assistente IA") {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(value = query, onValueChange = { query = it.take(500) }, singleLine = true, label = { Text(if(page == "Busca") "Buscar no catálogo" else "Pergunte à IA TV") }, modifier = Modifier.weight(1f))
                        TvButton(onClick = { model.load(page, query) }, modifier = Modifier.padding(top = 8.dp)) { Text("Enviar") }
                    }
                    if(page == "Assistente IA") Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf("Quais jogos têm hoje?", "Quero uma comédia").forEach { suggestion -> TvButton(onClick = { query = suggestion; model.load(page, query) }) { Text(suggestion, fontSize = 12.sp) } } }
                }
                if(page == "Jogos do Dia") Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TvButton(onClick = { date = LocalDate.parse(date).minusDays(1).toString(); model.load(page, date = date) }) { Text("‹ Dia anterior") }
                    Text(date, Modifier.padding(10.dp))
                    TvButton(onClick = { date = LocalDate.parse(date).plusDays(1).toString(); model.load(page, date = date) }) { Text("Próximo dia ›") }
                }
                if(page == "Perfil" || page == "Configurações") {
                    Text(if(page == "Perfil") "Perfil local de demonstração\nFavoritos e progresso ficam neste aparelho. Contas e sincronização serão adicionadas em outro milestone." else "IA TV 0.1.0\nCatálogo fictício • Assistente mock\nUse as setas, OK e Voltar. Voz ainda não disponível.", Modifier.padding(top = 24.dp))
                } else {
                    if(state.loading) { CircularProgressIndicator(); Text("Carregando…") }
                    state.notice?.let { Text(it, color = Color(0xFFFFCA80)); TvButton(onClick = { model.load(page, query, date) }) { Text("Tentar novamente") } }
                    LazyColumn(state = verticalState, verticalArrangement = Arrangement.spacedBy(18.dp), contentPadding = PaddingValues(vertical = 12.dp)) {
                        items(state.sections, key = { it.id }) { section ->
                            Column {
                                Text(section.title, fontSize = 18.sp, modifier = Modifier.padding(bottom = 10.dp))
                                if(section.items.isEmpty()) Text(if(section.id == "continue") "Seu próximo play começa aqui." else "Nenhum item para exibir.", color = Color(0xFF9EABBF), fontSize = 13.sp)
                                LazyRow(state = rowStates.getOrPut("$page/${section.id}") { LazyListState() }, horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(4.dp)) {
                                    items(section.items, key = { it.id }) { item ->
                                        val focusId = "${section.id}/${item.id}"
                                        val requester = remember { FocusRequester() }
                                        LaunchedEffect(restoreFocus) { if(restoreFocus && focusId == lastCard) { requester.requestFocus(); restoreFocus = false } }
                                        TvCard(item, Modifier.focusRequester(requester), onFocus = { lastCard = focusId }) { detail = item }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable private fun Detail(item: ContentCard, model: TvViewModel, onBack: () -> Unit, onPlay: () -> Unit, onAi: () -> Unit) {
    val actionFocus = remember { FocusRequester() }
    LaunchedEffect(item.id) { actionFocus.requestFocus() }
    var favorite by remember(item.id) { mutableStateOf(item.id in model.repository.favorites()) }
    var epg by remember(item.id) { mutableStateOf<List<EpgProgram>>(emptyList()) }
    LaunchedEffect(item.id) { if(item.kind == "channel") epg = runCatching { model.repository.api.epg(item.id) }.getOrDefault(emptyList()) }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        item { Text(item.badge, color = Color(0xFF65E4C5)); Text(item.title, fontSize = 36.sp) }
        item { Text(item.overview, fontSize = 20.sp) }
        item { Text("${item.category}${if(item.durationMinutes > 0) " • ${item.durationMinutes} min" else ""}", color = Color(0xFF9EABBF)) }
        item { Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TvButton(onClick = onPlay, enabled = item.playable, modifier = if(item.playable) Modifier.focusRequester(actionFocus) else Modifier) { Text(if(item.playable) "Assistir demo" else "Sem transmissão") }
            TvButton(onClick = { model.repository.toggleFavorite(item.id); favorite = !favorite }, modifier = if(!item.playable) Modifier.focusRequester(actionFocus) else Modifier) { Text(if(favorite) "✓ Na minha lista" else "+ Minha lista") }
        } }
        item { Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { TvButton(onClick = onAi) { Text("Perguntar à IA") }; TvButton(onClick = onBack) { Text("Voltar") } } }
        if(item.kind == "channel") { item { Text("Guia de programação • fictício", fontSize = 22.sp) }; items(epg.take(6)) { program -> Text("${java.time.Instant.parse(program.startAt).atZone(java.time.ZoneId.of("America/Sao_Paulo")).toLocalTime()} — ${program.title}") } }
    }
}
