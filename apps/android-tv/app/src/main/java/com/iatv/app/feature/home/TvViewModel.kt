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
data class TvState(val loading: Boolean = true, val sections: List<HomeSection> = emptyList(), val notice: String? = null, val offline: Boolean = false)
@HiltViewModel class TvViewModel @Inject constructor(val repository: TvRepository): ViewModel() {
    private val mutable = MutableStateFlow(TvState())
    val state = mutable.asStateFlow()
    private var request: Job? = null
    init { load("Início") }
    fun load(page: String, query: String = "", date: String = java.time.LocalDate.now(java.time.ZoneId.of("America/Sao_Paulo")).toString()) {
        request?.cancel()
        request = viewModelScope.launch {
            mutable.value = TvState()
            try {
                val sections = when(page) {
                    "Início" -> {
                        val home = repository.home()
                        val catalog = home.sections.flatMap { it.items }.distinctBy { it.id }
                        home.sections.map { section -> when(section.id) {
                            "favorites" -> section.copy(items = catalog.filter { it.id in repository.favorites() })
                            "continue" -> section.copy(items = catalog.filter { repository.position(it.id) > 0 })
                            else -> section
                        } }
                    }
                    "Assistente IA" -> { val reply = repository.api.chat(ChatRequest(query.ifBlank { "Ajuda" })); listOf(HomeSection("ai", reply.message, reply.cards)) }
                    "Busca" -> listOf(HomeSection("search", "Resultados", repository.api.search(query)))
                    "Jogos do Dia" -> listOf(HomeSection("sports", "Jogos • $date", repository.api.sports(date).map { it.card() }))
                    "Minha Lista" -> listOf(HomeSection("favorites", "Minha Lista • neste dispositivo", repository.api.catalog().filter { it.id in repository.favorites() }))
                    else -> { val kind = mapOf("TV ao Vivo" to "channel", "Filmes" to "movie", "Séries" to "series", "YouTube" to "youtube")[page]; listOf(HomeSection("catalog", page, repository.api.catalog().filter { it.kind == kind })) }
                }
                mutable.value = TvState(loading = false, sections = sections)
            } catch(cancelled: CancellationException) { throw cancelled }
            catch(_: Exception) {
                mutable.value = TvState(loading = false, sections = if(page == "Início") repository.cachedHome()?.sections.orEmpty() else emptyList(), offline = true, notice = "Sem conexão com a API. Verifique a rede e tente novamente.")
            }
        }
    }
}
