package it.unibo.almamensa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.unibo.almamensa.data.model.Theme
import it.unibo.almamensa.ui.screens.base.BaseScreen
import it.unibo.almamensa.ui.screens.settings.SettingsViewModel
import it.unibo.almamensa.ui.theme.AlmaMensaTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel = koinViewModel<SettingsViewModel>()
            val themeState by themeViewModel.state.collectAsStateWithLifecycle()

            AlmaMensaTheme (
                darkTheme = when (themeState.theme) {
                    Theme.Chiaro -> false
                    Theme.Scuro -> true
                    Theme.Sistema -> isSystemInDarkTheme()
                },
                dynamicColor = themeState.dynamicColor
            ){
                BaseScreen()
            }
        }
    }
}
