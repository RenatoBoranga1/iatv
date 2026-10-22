package com.iatv.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import com.iatv.app.core.designsystem.IaTvTheme
import com.iatv.app.core.diagnostics.DebugTelemetry
import com.iatv.app.feature.home.IaTvApp
import com.iatv.app.feature.home.TvViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint class MainActivity : ComponentActivity() {
    private val model: TvViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DebugTelemetry.event("app_open")
        setContent { IaTvTheme { Surface { IaTvApp(model) } } }
    }
}
