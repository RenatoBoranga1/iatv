package com.iatv.app

import com.iatv.app.core.model.WatchProgress
import com.iatv.app.core.player.*
import org.junit.Assert.*
import org.junit.Test

class HardeningTest {
    @Test fun completionBoundaryAndResume() {
        assertEquals(949L, WatchProgress.create(949, 1000, 1)!!.resumePositionMs)
        assertEquals(0L, WatchProgress.create(950, 1000, 2)!!.resumePositionMs)
        assertEquals(95.0, WatchProgress.create(950, 1000, 2)!!.percentage, 0.001)
        assertTrue(WatchProgress.create(500, 1000, 3, ended = true)!!.completed)
    }
    @Test fun invalidAndUnboundedProgress() {
        assertNull(WatchProgress.create(20, -1, 1))
        assertNull(WatchProgress.create(20, 0, 1))
        assertEquals(0L, WatchProgress.create(-10, 100, 1)!!.positionMs)
        assertEquals(100L, WatchProgress.create(Long.MAX_VALUE, 100, 1)!!.positionMs)
    }
    @Test fun retryBudgetDoesNotResetAfterSuccessOrNetworkChanges() {
        val policy = RecoveryPolicy()
        assertEquals(2000L, policy.nextDelay(PlaybackError.NETWORK_ERROR))
        assertEquals(4000L, policy.nextDelay(PlaybackError.TIMEOUT))
        assertEquals(8000L, policy.nextDelay(PlaybackError.NETWORK_ERROR))
        repeat(10) { assertNull(policy.nextDelay(PlaybackError.NETWORK_ERROR)) }
        policy.reset()
        assertEquals(2000L, policy.nextDelay(PlaybackError.TIMEOUT))
    }
    @Test fun permanentFailuresDoNotRetry() {
        val policy = RecoveryPolicy()
        listOf(PlaybackError.DECODER_ERROR, PlaybackError.SOURCE_ERROR, PlaybackError.UNSUPPORTED_FORMAT, PlaybackError.UNKNOWN).forEach { assertNull(policy.nextDelay(it)) }
        assertEquals(0, policy.attempts)
    }
}
