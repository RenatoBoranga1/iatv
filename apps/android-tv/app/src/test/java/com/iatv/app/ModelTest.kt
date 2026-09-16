package com.iatv.app
import com.iatv.app.core.model.SportsEvent
import org.junit.Assert.*
import org.junit.Test
class ModelTest {
    @Test fun sportsMetadataDoesNotGrantPlaybackRights() {
        val card = SportsEvent("1", "Aurora", "Estrela", "2026-09-16T22:00:00Z", "Copa fictícia").card()
        assertFalse(card.playable)
        assertEquals("sports", card.kind)
        assertTrue(card.overview.contains("19:00"))
    }
}
