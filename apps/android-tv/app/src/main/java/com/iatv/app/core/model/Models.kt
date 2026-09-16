package com.iatv.app.core.model
data class ContentCard(val id: String, val kind: String, val title: String, val overview: String, val category: String, val durationMinutes: Int, val badge: String, val playable: Boolean)
data class HomeSection(val id: String, val title: String, val items: List<ContentCard>)
data class HomeResponse(val greeting: String, val sections: List<HomeSection>, val mock: Boolean)
data class AssistantResponse(val message: String, val intent: String, val cards: List<ContentCard>, val mock: Boolean)
data class ChatRequest(val message: String)
data class PlaybackSource(val contentId: String, val url: String, val mimeType: String, val isLive: Boolean, val demo: Boolean)
data class EpgProgram(val id: String, val title: String, val startAt: String, val endAt: String, val description: String)
data class SportsEvent(val id: String, val homeTeam: String, val awayTeam: String, val startAt: String, val competition: String) {
    fun card() = ContentCard(id, "sports", "$homeTeam × $awayTeam", "${java.time.Instant.parse(startAt).atZone(java.time.ZoneId.of("America/Sao_Paulo")).toLocalTime()} • $competition. Nenhuma transmissão cadastrada.", "Futebol", 0, "Agenda fictícia", false)
}

