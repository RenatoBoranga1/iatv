package com.iatv.app

import com.iatv.app.core.model.ContentCard
import com.iatv.app.feature.search.filterCatalog
import org.junit.Assert.*
import org.junit.Test

class CatalogFiltersTest {
    private val movie = ContentCard("m", "movie", "Demo", "", "Ação", 90, "DEMO", true)
    private val channel = movie.copy(id = "c", kind = "channel", category = "Notícias")
    @Test fun typeFilterDoesNotMatchOnlyGenre() {
        assertEquals(listOf(movie), filterCatalog(listOf(movie, channel), "Filmes"))
        assertEquals(listOf(channel), filterCatalog(listOf(movie, channel), "Canais ao vivo"))
    }
    @Test fun genreMatchingIgnoresCaseAndAccents() {
        assertEquals(listOf(movie.copy(category="ACAO")), filterCatalog(listOf(movie.copy(category="ACAO")), "Ação"))
    }
    @Test fun liveFiltersUseCategoryAndPreserveEmptyState() {
        assertEquals(listOf(channel), filterCatalog(listOf(channel), "Notícias", live=true))
        assertTrue(filterCatalog(listOf(channel), "Esportes", live=true).isEmpty())
    }
    @Test fun allRetainsCatalogOrder() {
        assertEquals(listOf(channel,movie), filterCatalog(listOf(channel,movie), "Todos"))
    }
}
