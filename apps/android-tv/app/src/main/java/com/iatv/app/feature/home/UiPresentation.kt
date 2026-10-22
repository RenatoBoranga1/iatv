package com.iatv.app.feature.home

import com.iatv.app.core.model.ContentCard
import com.iatv.app.core.model.HomeSection

fun featuredContent(sections: List<HomeSection>): ContentCard? {
    val content = sections.asSequence().filter { it.id != "continue" && it.id != "favorites" }.flatMap { it.items.asSequence() }
    return content.firstOrNull { it.kind == "movie" && it.playable }
        ?: sections.asSequence().flatMap { it.items.asSequence() }.firstOrNull { it.playable }
}

fun hasFocusTarget(key: String, sections: List<HomeSection>): Boolean =
    if(key == "hero") featuredContent(sections) != null
    else sections.any { section -> section.items.any { "${section.id}/${it.id}" == key } }

fun emptyMessage(page: String): Pair<String,String> = when(page) {
    "Minha Lista" -> "Uma lista com a sua cara" to "Adicione títulos à Minha Lista para encontrá-los aqui."
    "Busca" -> "Encontre seu próximo favorito" to "Busque por um título, gênero ou canal."
    "Jogos do Dia" -> "Nenhum jogo nesta data" to "Escolha outro dia para consultar a programação."
    else -> "Novas descobertas vêm aí" to "Nenhum título disponível nesta seção agora."
}
