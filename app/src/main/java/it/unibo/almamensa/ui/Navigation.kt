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
import androidx.navigation.toRoute
import io.github.jan.supabase.auth.status.SessionStatus
import it.unibo.almamensa.ui.screens.auth.AuthScreen
import it.unibo.almamensa.ui.screens.auth.AuthViewModel
import it.unibo.almamensa.ui.screens.canteen.CanteenScreen
import it.unibo.almamensa.ui.screens.canteen.CanteenViewModel
import it.unibo.almamensa.ui.screens.explore.ExploreScreen
import it.unibo.almamensa.ui.screens.explore.ExploreViewModel
import it.unibo.almamensa.ui.screens.home.HomeScreen
import it.unibo.almamensa.ui.screens.home.HomeViewModel
import it.unibo.almamensa.ui.screens.map.MapScreen
import it.unibo.almamensa.ui.screens.map.MapViewModel
import it.unibo.almamensa.ui.screens.profile.ProfileScreen
import it.unibo.almamensa.ui.screens.profile.ProfileViewModel
import it.unibo.almamensa.ui.screens.review.ReviewScreen
import it.unibo.almamensa.ui.screens.review.ReviewViewModel
import it.unibo.almamensa.ui.screens.settings.SettingsScreen
import it.unibo.almamensa.ui.screens.settings.SettingsViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

sealed interface AlmaMensaRoute {
    @Serializable data object Home : AlmaMensaRoute
    @Serializable data object Auth : AlmaMensaRoute
    @Serializable data object Explore : AlmaMensaRoute
    @Serializable data object Profile: AlmaMensaRoute
    @Serializable data object Map: AlmaMensaRoute
    @Serializable data class CanteenDetails(val canteenId: Long) : AlmaMensaRoute
    @Serializable data class AddReview(val canteenId: Long) : AlmaMensaRoute
    @Serializable data object Settings: AlmaMensaRoute
}

// Used to know when to show the menu bar icon instead of the back arrow
val topLevelRoutes = listOf(
    AlmaMensaRoute.Home::class,
    AlmaMensaRoute.Explore::class,
    AlmaMensaRoute.Profile::class,
    AlmaMensaRoute.Auth::class,
    AlmaMensaRoute.Map::class,
    AlmaMensaRoute.Settings::class
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

        composable<AlmaMensaRoute.Map> {
            val mapVm = koinViewModel<MapViewModel>()
            MapScreen(mapVm)
        }

        composable<AlmaMensaRoute.CanteenDetails> { backStackEntry ->
            val route = backStackEntry.toRoute<AlmaMensaRoute.CanteenDetails>()
            val canteenId = route.canteenId
            val canteenDetailsVm = koinViewModel<CanteenViewModel> { parametersOf(canteenId) }
            val authVm = koinViewModel<AuthViewModel>(
                viewModelStoreOwner = LocalActivity.current as ComponentActivity // Get the AuthState from the activity: it needs to be shared between composables
            )
            val authState by authVm.state.collectAsStateWithLifecycle()
            val loggedIn = authState.sessionStatus is SessionStatus.Authenticated
            CanteenScreen(
                loggedIn = loggedIn,
                viewModel = canteenDetailsVm,
                onReview = {
                    navController.navigate(AlmaMensaRoute.AddReview(canteenId))
                },
                onBook = { /* TODO */ }
            )
        }

        composable<AlmaMensaRoute.AddReview> { backStackEntry ->
            val route = backStackEntry.toRoute<AlmaMensaRoute.AddReview>()
            val canteenId = route.canteenId
            val reviewVm = koinViewModel<ReviewViewModel> { parametersOf(canteenId) }
            val state by reviewVm.state.collectAsStateWithLifecycle()
            
            ReviewScreen(
                state = state,
                onTitleChange = reviewVm::onTitleChange,
                onScoreChange = reviewVm::onScoreChange,
                onDescriptionChange = reviewVm::onDescriptionChange,
                onSubmit = reviewVm::submitReview,
                onBack = { navController.popBackStack() }
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

        composable<AlmaMensaRoute.Settings> {
            val settingsVm = koinViewModel<SettingsViewModel>()
            val state by settingsVm.state.collectAsStateWithLifecycle()
            SettingsScreen(state, settingsVm.actions)
        }
    }
}
