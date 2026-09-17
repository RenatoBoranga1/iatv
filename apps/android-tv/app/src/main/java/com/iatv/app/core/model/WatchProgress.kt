package com.iatv.app.core.model

data class WatchProgress(
    val positionMs: Long,
    val durationMs: Long,
    val percentage: Double,
    val updatedAt: Long,
    val completed: Boolean
) {
    val resumePositionMs: Long get() = if (completed) 0 else positionMs
    companion object {
        const val COMPLETION_PERCENT = 95.0
        const val SAVE_INTERVAL_MS = 15_000L
        fun create(position: Long, duration: Long, now: Long, ended: Boolean = false): WatchProgress? {
            if (duration <= 0) return null
            val bounded = position.coerceIn(0, duration)
            val percentage = bounded.toDouble() / duration * 100
            return WatchProgress(bounded, duration, percentage, now, ended || percentage >= COMPLETION_PERCENT)
        }
    }
}
