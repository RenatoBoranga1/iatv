package com.iatv.app.core.network
import com.iatv.app.core.model.*
import retrofit2.http.*
interface TvApi {
    @GET("v1/home") suspend fun home(): HomeResponse
    @GET("v1/catalog") suspend fun catalog(): List<ContentCard>
    @GET("v1/search") suspend fun search(@Query("q") query: String): List<ContentCard>
    @GET("v1/sports") suspend fun sports(@Query("date") date: String): List<SportsEvent>
    @GET("v1/live/{id}/epg") suspend fun epg(@Path("id") id: String): List<EpgProgram>
    @POST("v1/ai/chat") suspend fun chat(@Body request: ChatRequest): AssistantResponse
    @GET("v1/playback/{id}") suspend fun playback(@Path("id") id: String): PlaybackSource
}
