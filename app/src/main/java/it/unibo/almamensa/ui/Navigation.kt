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
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
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
import it.unibo.almamensa.ui.screens.profile.edit.EditProfileScreen
import it.unibo.almamensa.ui.screens.profile.edit.EditProfileViewModel
import it.unibo.almamensa.ui.screens.profile.view.ProfileScreen
import it.unibo.almamensa.ui.screens.profile.view.ProfileViewModel
import it.unibo.almamensa.ui.screens.review.ReviewScreen
import it.unibo.almamensa.ui.screens.review.ReviewViewModel
import it.unibo.almamensa.ui.screens.settings.SettingsScreen
import it.unibo.almamensa.ui.screens.settings.SettingsViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

sealed interface AlmaMensaRoute {
    @Serializable data object Home : AlmaMensaRoute
    // Had to become a data class so that i could pass parameters to the composable
    @Serializable data class Auth(val isModifyingPassword: Boolean = false) : AlmaMensaRoute
    @Serializable data object Explore : AlmaMensaRoute
    @Serializable data object Profile: AlmaMensaRoute
    @Serializable data object Map: AlmaMensaRoute
    @Serializable data class CanteenDetails(val canteenId: Long) : AlmaMensaRoute
    @Serializable data class AddReview(val canteenId: Long) : AlmaMensaRoute
    @Serializable data object Settings: AlmaMensaRoute
    @Serializable data object EditProfile : AlmaMensaRoute
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
            HomeScreen(state)
        }

        composable<AlmaMensaRoute.Explore> {
            val canteenVm = koinViewModel<ExploreViewModel>()
            val state by canteenVm.state.collectAsStateWithLifecycle()

            ExploreScreen(
                state = state,
                onCanteenClick = { canteen ->
                    navController.navigate(AlmaMensaRoute.CanteenDetails(canteen.id))
                }
            )
        }

        composable<AlmaMensaRoute.Map> {
            val mapVm = koinViewModel<MapViewModel>()
            val state by mapVm.state.collectAsStateWithLifecycle()

            MapScreen(state)
        }

        composable<AlmaMensaRoute.CanteenDetails>(
            deepLinks = listOf(
                navDeepLink { uriPattern = "https://almamensa-e4631.web.app/canteen/{canteenId}" }
            )
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<AlmaMensaRoute.CanteenDetails>()

            val canteenDetailsVm = koinViewModel<CanteenViewModel> { parametersOf(route.canteenId) }
            val state by canteenDetailsVm.state.collectAsStateWithLifecycle()

            CanteenScreen(
                state = state,
                onReview = { navController.navigate(AlmaMensaRoute.AddReview(route.canteenId)) },
                onClearError = canteenDetailsVm::clearError
            )
        }

        composable<AlmaMensaRoute.AddReview> { backStackEntry ->
            val route = backStackEntry.toRoute<AlmaMensaRoute.AddReview>()

            val reviewVm = koinViewModel<ReviewViewModel> { parametersOf(route.canteenId) }
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

        composable<AlmaMensaRoute.Auth> { backStackEntry ->
            val route = backStackEntry.toRoute<AlmaMensaRoute.Auth>()
            val authVm = koinViewModel<AuthViewModel>(
                viewModelStoreOwner = LocalActivity.current as ComponentActivity
            )
            val state by authVm.state.collectAsStateWithLifecycle()
            AuthScreen(
                state = state,
                isModifyingPassword = route.isModifyingPassword,
                onEmailChange = authVm::onEmailChange,
                onSignIn = authVm::signIn,
                onSignUp = authVm::signUp,
                onUpdatePassword = authVm::updatePassword,
                onAuthSuccess = {
                    navController.navigate(AlmaMensaRoute.Home) {
                        popUpTo(AlmaMensaRoute.Auth()) { inclusive = true }
                    }
                },
                onUpdateSuccess = {
                    authVm.resetUpdateSuccess()
                    navController.popBackStack()
                }
            )
        }

        composable<AlmaMensaRoute.Profile> {
            val profileVm = koinViewModel<ProfileViewModel>()
            val state by profileVm.state.collectAsStateWithLifecycle()

            val authVm = koinViewModel<AuthViewModel>(
                viewModelStoreOwner = LocalActivity.current as ComponentActivity
            )
            val authState by authVm.state.collectAsStateWithLifecycle()

            ProfileScreen(
                profileState = state,
                onLogout = authVm::logout,
                authState = authState,
                onLogoutSuccess = {
                    navController.navigate(AlmaMensaRoute.Home) {
                        // Clean the navigation stack
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onEditClick = {
                    navController.navigate(AlmaMensaRoute.EditProfile)
                },
                onModifyPassword = {
                    navController.navigate(AlmaMensaRoute.Auth(isModifyingPassword = true))
                }
            )
        }

        composable<AlmaMensaRoute.EditProfile> {
            val editProfileVm = koinViewModel<EditProfileViewModel>()
            val state by editProfileVm.state.collectAsStateWithLifecycle()

            EditProfileScreen(
                state = state,
                onNameChange = editProfileVm::onNameChange,
                onSurnameChange = editProfileVm::onSurnameChange,
                onSaveClick = editProfileVm::saveProfile,
                onAvatarPicked = editProfileVm::uploadProfilePhoto,
                onDeleteImageClick = editProfileVm::deleteProfilePhoto,
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable<AlmaMensaRoute.Settings> {
            val settingsVm = koinViewModel<SettingsViewModel>()
            val state by settingsVm.state.collectAsStateWithLifecycle()
            SettingsScreen(
                settingsState = state,
                onThemeChange = settingsVm::setTheme,
                onDynamicColorChange = settingsVm::setDynamicColor
            )
        }
    }
}
