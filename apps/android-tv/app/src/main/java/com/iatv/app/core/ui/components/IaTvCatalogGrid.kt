package com.iatv.app.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.iatv.app.core.model.*
import com.iatv.app.core.network.TvApi

/** Rows are virtualized by the screen's LazyColumn; focus uses content IDs, not positions. */
fun LazyListScope.iaTvGrid(section: HomeSection, restoreKey: String?, onRestored: () -> Unit,
    liveApi: TvApi?, onOpen: (ContentCard, String) -> Unit) {
    if(section.items.isEmpty()) return
    item(key="${section.id}/heading") { IaTvSectionTitle(section.title,section.items.size) }
    itemsIndexed(section.items.chunked(3), key={ _, row -> "${section.id}/row/${row.first().id}" }) { _, row ->
        Row(Modifier.fillMaxWidth().padding(8.dp),horizontalArrangement=Arrangement.spacedBy(16.dp)) {
            row.forEach { card ->
                key(card.id) {
                    val target="${section.id}/${card.id}"
                    val requester=remember { FocusRequester() }
                    LaunchedEffect(restoreKey) {
                        if(restoreKey==target) { withFrameNanos {}; requester.requestFocus(); onRestored() }
                    }
                    val modifier=Modifier.weight(1f).focusRequester(requester)
                    if(card.kind=="channel") IaTvLiveCard(card,modifier,api=liveApi) { onOpen(card,target) }
                    else IaTvLandscapeCard(card,modifier) { onOpen(card,target) }
                }
            }
            repeat(3-row.size) { Spacer(Modifier.weight(1f)) }
        }
    }
}
