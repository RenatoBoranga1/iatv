package com.iatv.app.feature.home
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iatv.app.core.model.*
import com.iatv.app.core.network.TvRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.iatv.app.core.network.NetworkMonitor
import com.iatv.app.core.network.NetworkState
import com.iatv.app.core.network.TvStore
import com.iatv.app.core.diagnostics.Telemetry
import com.iatv.app.core.ui.ScreenStatus
import com.iatv.app.core.diagnostics.DebugTelemetry
import com.iatv.app.core.diagnostics.DebugCrashReporter
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
data class TvState(val status: ScreenStatus = ScreenStatus.Loading, val sections: List<HomeSection> = emptyList(), val notice: String? = null) {
    val loading get() = status == ScreenStatus.Loading
}
@HiltViewModel class TvViewModel(
    val repository: TvStore,
    connectivity: kotlinx.coroutines.flow.Flow<NetworkState>,
    private val telemetry: Telemetry
): ViewModel() {
    @Inject constructor(repository: TvRepository, monitor: NetworkMonitor) : this(repository, monitor.state, DebugTelemetry)
    private val mutable = MutableStateFlow(TvState())
    val state = mutable.asStateFlow()
    private var request: Job? = null
    val network = connectivity.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NetworkState.Unknown)
    private var currentPage = "Início"
    private var currentQuery = ""
    private var currentDate = ""
    init {
        viewModelScope.launch {
            var previous = NetworkState.Unknown
            network.collect { value ->
                if (value == NetworkState.Disconnected) telemetry.event("network_lost")
                if (previous == NetworkState.Disconnected && (value == NetworkState.Connected || value == NetworkState.Limited)) {
                    telemetry.event("network_restored")
                    if (mutable.value.status == ScreenStatus.Offline && currentPage != "Assistente IA") load(currentPage, currentQuery, currentDate)
                }
                previous = value
            }
        }
    }
    fun refreshLocal() { mutable.value = mutable.value.copy(sections = repository.personalize(mutable.value.sections)) }
    fun load(page: String, query: String = "", date: String = java.time.LocalDate.now(java.time.ZoneId.of("America/Sao_Paulo")).toString()) {
        currentPage = page; currentQuery = query; currentDate = date
        request?.cancel()
        request = viewModelScope.launch {
            mutable.value = TvState()
            try {
                val sections = when(page) {
                    "Início" -> {
                        val home = repository.home()
                        repository.personalize(home.sections)
                    }
                    "Assistente IA" -> { val reply = repository.api.chat(ChatRequest(query.ifBlank { "Ajuda" })); listOf(HomeSection("ai", reply.message, reply.cards)) }
                    "Busca" -> listOf(HomeSection("search", "Resultados", repository.api.search(query)))
                    "Jogos do Dia" -> listOf(HomeSection("sports", "Jogos • $date", repository.api.sports(date).map { it.card() }))
                    "Minha Lista" -> listOf(HomeSection("favorites", "Minha Lista • neste dispositivo", repository.api.catalog().filter { it.id in repository.favorites() }))
                    else -> { val kind = mapOf("TV ao Vivo" to "channel", "Filmes" to "movie", "Séries" to "series", "YouTube" to "youtube")[page]; listOf(HomeSection("catalog", page, repository.api.catalog().filter { it.kind == kind })) }
                }
                mutable.value = TvState(status = if(sections.all { it.items.isEmpty() }) ScreenStatus.Empty else ScreenStatus.Content, sections = sections)
                when(page) { "Início" -> telemetry.event("home_loaded"); "Busca" -> telemetry.event("search"); "Assistente IA" -> telemetry.event("ai_query") }
            } catch(cancelled: CancellationException) { throw cancelled }
            catch(error: Exception) {
                telemetry.error("screen_error", error.javaClass.simpleName)
                val offline = error is java.io.IOException && error !is com.google.gson.stream.MalformedJsonException
                val cache = repository.cachedHome()?.sections.orEmpty()
                val available = when(page) {
                    "Início" -> repository.personalize(cache)
                    "Minha Lista" -> repository.personalize(cache).filter { it.id == "favorites" }
                    else -> emptyList()
                }
                mutable.value = TvState(status = if(offline) ScreenStatus.Offline else ScreenStatus.Error, sections = available,
                    notice = if(offline) "Sem conexão com a API. Verifique a rede e tente novamente." else "Não foi possível carregar esta tela. Tente novamente.")
            }
        }
    }
}
