package com.iatv.app.core.player

enum class PlaybackError { NETWORK_ERROR, SOURCE_ERROR, DECODER_ERROR, UNSUPPORTED_FORMAT, TIMEOUT, UNKNOWN }
sealed interface PlayerState {
    data object Idle : PlayerState
    data object Preparing : PlayerState
    data object Buffering : PlayerState
    data object Playing : PlayerState
    data object Paused : PlayerState
    data object Ended : PlayerState
    data class Recovering(val attempt: Int, val delayMs: Long) : PlayerState
    data class Error(val code: PlaybackError) : PlayerState
}
class RecoveryPolicy {
    var attempts: Int = 0
        private set
    fun nextDelay(code: PlaybackError): Long? {
        if (code !in setOf(PlaybackError.NETWORK_ERROR, PlaybackError.TIMEOUT) || attempts >= 3) return null
        return 2_000L shl attempts++
    }
    fun reset() { attempts = 0 }
}
