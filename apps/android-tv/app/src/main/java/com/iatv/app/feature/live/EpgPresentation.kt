package com.iatv.app.feature.live

import com.iatv.app.core.model.EpgProgram
import java.time.Instant

data class EpgPresentation(val current: EpgProgram?, val next: EpgProgram?, val progress: Float)

fun presentEpg(programs: List<EpgProgram>, now: Instant): EpgPresentation {
    val valid = programs.mapNotNull { program ->
        runCatching {
            val start = Instant.parse(program.startAt)
            val end = Instant.parse(program.endAt)
            if (end > start) Triple(program, start, end) else null
        }.getOrNull()
    }.sortedBy { it.second }
    val current = valid.firstOrNull { now >= it.second && now < it.third }
    val next = valid.firstOrNull { it.second > now }
    val progress = current?.let {
        ((now.toEpochMilli() - it.second.toEpochMilli()).toDouble() /
            (it.third.toEpochMilli() - it.second.toEpochMilli())).toFloat().coerceIn(0f, 1f)
    } ?: 0f
    return EpgPresentation(current?.first, next?.first, progress)
}
