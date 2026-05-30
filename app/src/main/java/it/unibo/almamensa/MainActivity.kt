package it.unibo.almamensa

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.handleDeeplinks
import it.unibo.almamensa.data.model.Theme
import it.unibo.almamensa.data.repositories.UserRepository
import it.unibo.almamensa.ui.screens.base.BaseScreen
import it.unibo.almamensa.ui.screens.settings.SettingsViewModel
import it.unibo.almamensa.ui.theme.AlmaMensaTheme
import it.unibo.almamensa.utils.observeConnectivity
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

// FragmentActivity required for BiometricPrompt
class MainActivity : FragmentActivity() {

    private val supabase: SupabaseClient by inject()
    private val userRepository: UserRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Handle deeplink when the app is not running
        intent?.let { handleDeepLink(it) }

        enableEdgeToEdge()
        setContent {
            val themeViewModel = koinViewModel<SettingsViewModel>()
            val themeState by themeViewModel.state.collectAsStateWithLifecycle()

            val isDark = when (themeState.theme) {
                Theme.LIGHT -> false
                Theme.DARK -> true
                Theme.SYSTEM -> isSystemInDarkTheme()
            }

            AlmaMensaTheme (
                darkTheme = isDark,
                dynamicColor = themeState.dynamicColor
            ){
                BaseScreen(isDarkTheme = isDark)
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                // Reload profile when the app is reopened
                userRepository.getMyProfile()
                // Reload the profile when connection is restored
                applicationContext.observeConnectivity()
                    .filter { it }
                    .collect { userRepository.getMyProfile() }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle deep link when the app is already running
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent) {
        val data = intent.data
        if (data != null && data.scheme == "almamensa" && data.host == "auth-callback") {
            supabase.handleDeeplinks(intent)
        }
    }
}
