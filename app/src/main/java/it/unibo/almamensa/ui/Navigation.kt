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
import io.github.jan.supabase.auth.status.SessionStatus
import it.unibo.almamensa.ui.screens.auth.AuthScreen
import it.unibo.almamensa.ui.screens.auth.AuthViewModel
import it.unibo.almamensa.ui.screens.canteen.CanteenScreen
import it.unibo.almamensa.ui.screens.canteen.CanteenViewModel
import it.unibo.almamensa.ui.screens.explore.ExploreScreen
import it.unibo.almamensa.ui.screens.explore.ExploreViewModel
import it.unibo.almamensa.ui.screens.home.HomeScreen
import it.unibo.almamensa.ui.screens.home.HomeViewModel
import it.unibo.almamensa.ui.screens.profile.ProfileScreen
import it.unibo.almamensa.ui.screens.profile.ProfileViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

sealed interface AlmaMensaRoute {
    @Serializable data object Home : AlmaMensaRoute
    @Serializable data object Auth : AlmaMensaRoute
    @Serializable data object Explore : AlmaMensaRoute
    @Serializable data object Profile: AlmaMensaRoute
    @Serializable data class CanteenDetails(val canteenId: Long) : AlmaMensaRoute

}

// Used to know when to show the menu bar icon instead of the back arrow
val topLevelRoutes = listOf(
    AlmaMensaRoute.Home::class,
    AlmaMensaRoute.Explore::class,
    AlmaMensaRoute.Profile::class,
    AlmaMensaRoute.Auth::class
)

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

        composable<AlmaMensaRoute.Explore> {
            val canteenVm = koinViewModel<ExploreViewModel>()
            ExploreScreen(
                viewModel = canteenVm,
                onCanteenClick = { canteen ->
                    navController.navigate(AlmaMensaRoute.CanteenDetails(canteen.id))
                }
            )
        }

        composable<AlmaMensaRoute.CanteenDetails> { backStackEntry ->
            val canteenId = backStackEntry.arguments?.getLong("canteenId") ?: 0L
            val canteenDetailsVm = koinViewModel<CanteenViewModel> { parametersOf(canteenId) }
            val authVm = koinViewModel<AuthViewModel>(
                viewModelStoreOwner = LocalActivity.current as ComponentActivity // Get the AuthState from the activity: it need to be shared between composables
            )
            val authState by authVm.state.collectAsStateWithLifecycle()
            val loggedIn = authState.sessionStatus is SessionStatus.Authenticated
            CanteenScreen(
                loggedIn = loggedIn,
                viewModel = canteenDetailsVm,
                onReview = { /* TODO */ },
                onBook = { /* TODO */ }
            )
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
