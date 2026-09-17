package com.iatv.app.core.network

import com.iatv.app.core.model.*

/** Small boundary for deterministic ViewModel/repository tests and future storage replacement. */
interface TvStore {
    val api: TvApi
    suspend fun home(): HomeResponse
    fun cachedHome(): HomeResponse?
    fun favorites(): Set<String>
    fun toggleFavorite(id: String)
    fun progress(id: String): WatchProgress?
    fun position(id: String): Long
    fun savePosition(id: String, position: Long, duration: Long, ended: Boolean = false)
    fun personalize(sections: List<HomeSection>): List<HomeSection>
}
