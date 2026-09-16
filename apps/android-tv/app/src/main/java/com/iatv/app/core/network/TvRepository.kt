package com.iatv.app.core.network
import android.content.Context
import com.google.gson.Gson
import com.iatv.app.core.model.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
@Singleton class TvRepository @Inject constructor(val api: TvApi, @ApplicationContext context: Context) {
    private val prefs = context.getSharedPreferences("iatv-demo", Context.MODE_PRIVATE)
    suspend fun home(): HomeResponse = api.home().also { prefs.edit().putString("home", Gson().toJson(it)).apply() }
    fun cachedHome(): HomeResponse? = runCatching { Gson().fromJson(prefs.getString("home", null), HomeResponse::class.java) }.getOrNull()
    fun favorites(): Set<String> = prefs.getStringSet("favorites", emptySet())!!.toSet()
    fun toggleFavorite(id: String) { val values = favorites().toMutableSet(); if (!values.add(id)) values.remove(id); prefs.edit().putStringSet("favorites", values).apply() }
    fun position(id: String): Long = prefs.getLong("position-$id", 0)
    fun savePosition(id: String, position: Long, duration: Long) { if(duration > 0) prefs.edit().putLong("position-$id", if(position.toDouble()/duration >= .95) 0 else position.coerceAtLeast(0)).apply() }
}
