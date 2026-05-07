package it.unibo.almamensa.ui

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import it.unibo.almamensa.ui.screens.auth.AuthScreen
import it.unibo.almamensa.ui.screens.auth.AuthViewModel
import it.unibo.almamensa.ui.screens.canteenDisplay.CanteenScreen
import it.unibo.almamensa.ui.screens.canteenDisplay.CanteenViewModel
import it.unibo.almamensa.ui.screens.home.HomeScreen
import it.unibo.almamensa.ui.screens.home.HomeViewModel
import it.unibo.almamensa.ui.screens.profile.ProfileScreen
import it.unibo.almamensa.ui.screens.profile.ProfileViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

sealed interface AlmaMensaRoute {
    @Serializable data object Home : AlmaMensaRoute
    @Serializable data object Auth : AlmaMensaRoute
    @Serializable data object Canteens : AlmaMensaRoute
    @Serializable data object Profile: AlmaMensaRoute
}

@Composable
fun AlmaMensaNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AlmaMensaRoute.Home,
        modifier = modifier
    ) {
        composable<AlmaMensaRoute.Home> {
            val homeVm = koinViewModel<HomeViewModel>()
            val state by homeVm.state.collectAsStateWithLifecycle()
            HomeScreen(state, navController)
        }

        composable<AlmaMensaRoute.Canteens> {
            val canteenVm = koinViewModel<CanteenViewModel>()
            CanteenScreen(canteenVm)
        }

        composable<AlmaMensaRoute.Auth> {
            val authVm = koinViewModel<AuthViewModel>(
                viewModelStoreOwner = LocalActivity.current as ComponentActivity
            )
            val state by authVm.state.collectAsStateWithLifecycle()
            AuthScreen(
                state = state,
                onEmailChange = authVm::onEmailChange,
                onSignIn = authVm::signIn,
                onSignUp = authVm::signUp,
                onAuthSuccess = {
                    navController.navigate(AlmaMensaRoute.Home) {
                        popUpTo(AlmaMensaRoute.Auth) { inclusive = true }
                    }
                }
            )
        }

        composable<AlmaMensaRoute.Profile> {
            val profileVm = koinViewModel<ProfileViewModel>()
            val state by profileVm.state.collectAsStateWithLifecycle()
            ProfileScreen(state)
        }
    }
}
