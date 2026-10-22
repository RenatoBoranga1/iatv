package com.iatv.app

import com.google.gson.Gson
import com.iatv.app.core.model.*
import com.iatv.app.feature.home.*
import org.junit.Assert.*
import org.junit.Test

class UiPresentationTest {
    private fun card(id: String, kind: String = "movie", playable: Boolean = true) =
        ContentCard(id,kind,"Título","Descrição","Categoria",90,"DEMO",playable)

    @Test fun heroPrefersPlayableMovieOverLiveAndUnavailableTitles() {
        val sections=listOf(HomeSection("live","Live",listOf(card("live","channel"))),HomeSection("movies","Filmes",listOf(card("locked",playable=false),card("movie"))))
        assertEquals("movie",featuredContent(sections)?.id)
    }
    @Test fun heroFallsBackToPlayableLiveWithoutInventingContent() {
        assertEquals("live",featuredContent(listOf(HomeSection("live","Live",listOf(card("live","channel")))))?.id)
        assertNull(featuredContent(listOf(HomeSection("locked","Em breve",listOf(card("locked",playable=false))))))
        assertNull(featuredContent(emptyList()))
    }
    @Test fun focusRestoresExactSectionWhenCardAppearsTwice() {
        val sections=listOf(HomeSection("favorites","Favoritos",listOf(card("one"))),HomeSection("movies","Filmes",listOf(card("one"))))
        assertTrue(hasFocusTarget("favorites/one",sections))
        assertTrue(hasFocusTarget("movies/one",sections))
        assertFalse(hasFocusTarget("removed/one",sections))
    }
    @Test fun removedFavoriteCannotTrapFocusOnMissingCard() {
        assertFalse(hasFocusTarget("favorites/one",listOf(HomeSection("favorites","Favoritos",emptyList()))))
        assertFalse(hasFocusTarget("hero",emptyList()))
    }
    @Test fun legacyCardsDoNotRequireArtwork() {
        val item=Gson().fromJson("""{"id":"one","kind":"movie","title":"Título","overview":"Demo","category":"Filme","durationMinutes":90,"badge":"DEMO","playable":true}""",ContentCard::class.java)
        assertNull(item.posterUrl); assertNull(item.backdropUrl); assertNull(item.logoUrl)
        assertTrue(item.playable)
    }
    @Test fun emptyStatesOfferContextWithoutTechnicalErrors() {
        assertTrue(emptyMessage("Minha Lista").second.contains("Adicione"))
        assertTrue(emptyMessage("Busca").second.contains("Busque"))
        assertTrue(emptyMessage("Jogos do Dia").second.contains("outro dia"))
    }
}
