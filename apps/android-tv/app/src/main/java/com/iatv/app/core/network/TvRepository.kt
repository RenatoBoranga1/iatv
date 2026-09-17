package com.iatv.app.core.network
import android.content.Context
import com.google.gson.Gson
import com.iatv.app.core.model.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
@Singleton class TvRepository @Inject constructor(override val api: TvApi, @ApplicationContext context: Context): TvStore {
    private val prefs = context.getSharedPreferences("iatv-demo", Context.MODE_PRIVATE)
    private val gson = Gson()
    override suspend fun home(): HomeResponse = api.home().also { home -> kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) { prefs.edit().putString("home", gson.toJson(home)).apply() } }
    override fun cachedHome(): HomeResponse? = runCatching { gson.fromJson(prefs.getString("home", null), HomeResponse::class.java) }.getOrNull()
    override fun favorites(): Set<String> = prefs.getStringSet("favorites", emptySet())!!.toSet()
    override fun toggleFavorite(id: String) { val values = favorites().toMutableSet(); if (!values.add(id)) values.remove(id); prefs.edit().putStringSet("favorites", values).apply() }
    override fun progress(id: String): WatchProgress? = runCatching { gson.fromJson(prefs.getString("progress-$id", null), WatchProgress::class.java) }.getOrNull()
    override fun position(id: String): Long = progress(id)?.resumePositionMs ?: prefs.getLong("position-$id", 0).coerceAtLeast(0)
    override fun savePosition(id: String, position: Long, duration: Long, ended: Boolean) {
        val value = WatchProgress.create(position, duration, System.currentTimeMillis(), ended) ?: return
        val previous = progress(id)
        if (previous?.positionMs == value.positionMs && previous.durationMs == value.durationMs && previous.completed == value.completed) return
        prefs.edit().putString("progress-$id", gson.toJson(value)).remove("position-$id").apply()
    }
    override fun personalize(sections: List<HomeSection>): List<HomeSection> {
        val catalog = sections.flatMap { it.items }.distinctBy { it.id }
        return sections.map { section -> when(section.id) {
            "favorites" -> section.copy(items = catalog.filter { it.id in favorites() })
            "continue" -> section.copy(items = catalog.filter { it.kind != "channel" && position(it.id) > 0 }.sortedByDescending { progress(it.id)?.updatedAt ?: 0 })
            else -> section
        } }
    }
}

