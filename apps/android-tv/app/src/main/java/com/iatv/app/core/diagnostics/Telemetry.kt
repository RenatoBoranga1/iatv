package com.iatv.app.core.diagnostics

import android.util.Log
import com.iatv.app.BuildConfig
import org.json.JSONObject

interface Telemetry {
    fun event(name: String)
    fun error(name: String, code: String)
    fun timing(name: String, milliseconds: Long)
}

/** Only developer-defined event names/codes and numeric measurements; never URLs or user text. */
object DebugTelemetry : Telemetry {
    private fun emit(name: String, value: Any? = null) {
        if (BuildConfig.DEBUG) Log.d("IaTvTelemetry", JSONObject().put("event", name).put("value", value).toString())
    }
    override fun event(name: String) = emit(name)
    override fun error(name: String, code: String) = emit(name, code)
    override fun timing(name: String, milliseconds: Long) = emit(name, milliseconds.coerceAtLeast(0))
}

interface CrashReporter { fun record(error: Throwable) }
object DebugCrashReporter : CrashReporter {
    override fun record(error: Throwable) { DebugTelemetry.error("handled_error", error.javaClass.simpleName) }
}
