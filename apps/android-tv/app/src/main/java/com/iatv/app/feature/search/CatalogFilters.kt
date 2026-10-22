package com.iatv.app.feature.search

import com.iatv.app.core.model.ContentCard
import java.text.Normalizer

val liveFilters = listOf("Todos", "Notícias", "Esportes", "Filmes", "Séries", "Infantil", "Variedades", "Documentários")
val searchFilters = listOf("Todos", "Filmes", "Séries", "Canais ao vivo", "Infantil", "Ação", "Drama", "Comédia", "Documentário")
private fun normalized(value: String) = Normalizer.normalize(value, Normalizer.Form.NFD).replace("\\p{M}+".toRegex(), "").lowercase(java.util.Locale.ROOT)

fun filterCatalog(items: List<ContentCard>, filter: String, live: Boolean = false): List<ContentCard> {
    if (filter == "Todos") return items
    val kind = if (live) null else mapOf("Filmes" to "movie", "Séries" to "series", "Canais ao vivo" to "channel")[filter]
    return items.filter { item ->
        if (kind != null) item.kind == kind
        else normalized(item.category).contains(normalized(filter).removeSuffix("s"))
    }
}
