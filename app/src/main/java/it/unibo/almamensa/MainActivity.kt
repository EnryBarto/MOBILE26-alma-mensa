package it.unibo.almamensa

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.handleDeeplinks
import it.unibo.almamensa.data.model.Theme
import it.unibo.almamensa.ui.screens.base.BaseScreen
import it.unibo.almamensa.ui.screens.settings.SettingsViewModel
import it.unibo.almamensa.ui.theme.AlmaMensaTheme
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    private val supabase: SupabaseClient by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Gestisce il deep link all'avvio dell'activity
        intent?.let { handleDeepLink(it) }

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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Gestisce il deep link se l'app è già in esecuzione
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent) {
        val data = intent.data
        if (data != null && data.scheme == "almamensa" && data.host == "auth-callback") {
            supabase.handleDeeplinks(intent)
        }
    }
}
