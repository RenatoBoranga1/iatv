package com.iatv.app

import com.iatv.app.core.model.EpgProgram
import com.iatv.app.feature.live.presentEpg
import java.time.Instant
import org.junit.Assert.*
import org.junit.Test

class EpgPresentationTest {
    private fun program(id: String, start: String, end: String) = EpgProgram(id, id, start, end, "")
    private val first = program("first", "2026-10-21T20:00:00Z", "2026-10-21T21:00:00Z")
    private val second = program("second", "2026-10-21T21:00:00Z", "2026-10-21T22:00:00Z")
    @Test fun sortsScheduleAndComputesElapsedTime() {
        val value = presentEpg(listOf(second, first), Instant.parse("2026-10-21T20:30:00Z"))
        assertEquals(first, value.current)
        assertEquals(second, value.next)
        assertEquals(.5f, value.progress, .001f)
    }
    @Test fun boundarySwitchesToNextProgram() {
        val value = presentEpg(listOf(first, second), Instant.parse("2026-10-21T21:00:00Z"))
        assertEquals(second, value.current)
        assertEquals(0f, value.progress, 0f)
        assertNull(value.next)
    }
    @Test fun rejectsMalformedAndReversedIntervals() {
        val value = presentEpg(listOf(program("bad", "invalid", "invalid"), first.copy(endAt = first.startAt)), Instant.now())
        assertNull(value.current)
        assertNull(value.next)
        assertEquals(0f, value.progress, 0f)
    }
    @Test fun scheduleGapDoesNotInventCurrentProgram() {
        val value = presentEpg(listOf(second), Instant.parse("2026-10-21T20:30:00Z"))
        assertNull(value.current)
        assertEquals(second, value.next)
    }
}
