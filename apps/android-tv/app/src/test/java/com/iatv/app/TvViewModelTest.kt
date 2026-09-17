package com.iatv.app

import androidx.lifecycle.ViewModelStore
import com.iatv.app.core.diagnostics.Telemetry
import com.iatv.app.core.model.*
import com.iatv.app.core.network.*
import com.iatv.app.core.ui.ScreenStatus
import com.iatv.app.feature.home.TvViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TvViewModelTest {
    private val silent = object : Telemetry {
        override fun event(name: String) {}
        override fun error(name: String, code: String) {}
        override fun timing(name: String, milliseconds: Long) {}
    }
    @Test fun offlineRecoversOnNetworkReturnAndHttpErrorIsNotOffline() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val store = ViewModelStore()
        try {
            val fake = FakeStore()
            val network = MutableStateFlow(NetworkState.Disconnected)
            val model = TvViewModel(fake, network, silent)
            store.put("vm", model)
            fake.failure = java.io.IOException()
            model.load("Início"); runCurrent()
            assertEquals(ScreenStatus.Offline, model.state.value.status)
            fake.failure = null
            network.value = NetworkState.Connected; runCurrent()
            assertEquals(ScreenStatus.Content, model.state.value.status)
            assertEquals(2, fake.calls)
            fake.failure = IllegalStateException("invalid JSON")
            model.load("Início"); runCurrent()
            assertEquals(ScreenStatus.Error, model.state.value.status)
        } finally { store.clear(); runCurrent(); Dispatchers.resetMain() }
    }
    @Test fun latestRequestWinsAndCancelledRequestDoesNotBecomeError() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val store = ViewModelStore()
        try {
            val fake = FakeStore()
            val model = TvViewModel(fake, MutableStateFlow(NetworkState.Connected), silent)
            store.put("vm", model)
            fake.wait = 1000
            model.load("Início"); runCurrent()
            fake.wait = 0
            model.load("Início"); runCurrent()
            assertEquals(ScreenStatus.Content, model.state.value.status)
            advanceTimeBy(2000); runCurrent()
            assertEquals(ScreenStatus.Content, model.state.value.status)
        } finally { store.clear(); runCurrent(); Dispatchers.resetMain() }
    }
    private class FakeStore : TvStore {
        override val api: TvApi get() = error("Unexpected API operation")
        var calls = 0
        var failure: Exception? = null
        var wait = 0L
        private val card = ContentCard("movie", "movie", "Test", "", "", 1, "", true)
        override suspend fun home(): HomeResponse { calls++; delay(wait); failure?.let { throw it }; return HomeResponse("", listOf(HomeSection("movies", "", listOf(card))), true) }
        override fun cachedHome(): HomeResponse? = null
        override fun favorites(): Set<String> = emptySet()
        override fun toggleFavorite(id: String) {}
        override fun progress(id: String): WatchProgress? = null
        override fun position(id: String) = 0L
        override fun savePosition(id: String, position: Long, duration: Long, ended: Boolean) {}
        override fun personalize(sections: List<HomeSection>) = sections
    }
}

