package it.unibo.almamensa.ui

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
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
import it.unibo.almamensa.ui.screens.nearme.NearMeScreen
import it.unibo.almamensa.ui.screens.nearme.NearMeViewModel
import it.unibo.almamensa.ui.screens.profile.changepassword.UpdatePasswordScreen
import it.unibo.almamensa.ui.screens.profile.edit.EditProfileScreen
import it.unibo.almamensa.ui.screens.profile.edit.EditProfileViewModel
import it.unibo.almamensa.ui.screens.profile.reviews.PersonalReviewScreen
import it.unibo.almamensa.ui.screens.profile.reviews.PersonalReviewViewModel
import it.unibo.almamensa.ui.screens.profile.view.ProfileScreen
import it.unibo.almamensa.ui.screens.profile.view.ProfileViewModel
import it.unibo.almamensa.ui.screens.review.ReviewScreen
import it.unibo.almamensa.ui.screens.review.ReviewViewModel
import it.unibo.almamensa.ui.screens.settings.SettingsScreen
import it.unibo.almamensa.ui.screens.settings.SettingsViewModel
import it.unibo.almamensa.utils.showBiometricPrompt
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

sealed interface AlmaMensaRoute {
    @Serializable data object Home : AlmaMensaRoute
    @Serializable data object Auth: AlmaMensaRoute
    @Serializable data object Explore : AlmaMensaRoute
    @Serializable data object Profile: AlmaMensaRoute
    @Serializable data object Map: AlmaMensaRoute
    @Serializable data class CanteenDetails(val canteenId: Long) : AlmaMensaRoute
    @Serializable data class WriteReview(val canteenId: Long? = null, val reviewId: Long? = null) : AlmaMensaRoute
    @Serializable data object Settings: AlmaMensaRoute
    @Serializable data object EditProfile : AlmaMensaRoute
    @Serializable data object UpdatePassword : AlmaMensaRoute
    @Serializable data object ShowReviews: AlmaMensaRoute
    @Serializable data object NearMe : AlmaMensaRoute
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
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = false
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
                },
                onSearchQueryChange = canteenVm::onSearchQueryChange,
                onLoadCanteens = canteenVm::loadCanteens,
                onToggleFavorites = canteenVm::toggleFavorites,
                showOnlyFavorites = state.showOnlyFavorites
            )
        }

        composable<AlmaMensaRoute.Map> {
            val mapVm = koinViewModel<MapViewModel>()
            val state by mapVm.state.collectAsStateWithLifecycle()

            MapScreen(
                state = state,
                onCanteenClick = { canteen ->
                    navController.navigate(AlmaMensaRoute.CanteenDetails(canteen.id))
                },
                onOpenNearMeClick = {
                    navController.navigate(AlmaMensaRoute.NearMe)
                },
                isDarkTheme = isDarkTheme
            )
        }

        composable<AlmaMensaRoute.CanteenDetails>(
            deepLinks = listOf(
                navDeepLink { uriPattern = "https://almamensa-e4631.web.app/canteen/{canteenId}" }
            )
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<AlmaMensaRoute.CanteenDetails>()

            val canteenDetailsVm = koinViewModel<CanteenViewModel> { parametersOf(route.canteenId) }
            val state by canteenDetailsVm.state.collectAsStateWithLifecycle()

            val authVm = authViewModel()
            val authState by authVm.state.collectAsStateWithLifecycle()
            val currentUserId = (authState.sessionStatus as? SessionStatus.Authenticated)?.session?.user?.id

            // Refresh reviews when the screen is resumed (e.g., returning from adding a review)
            LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                canteenDetailsVm.refresh()
            }

            CanteenScreen(
                state = state,
                onReview = {
                    navController.navigate(AlmaMensaRoute.WriteReview(canteenId = route.canteenId))
                },
                onClearError = canteenDetailsVm::clearError,
                onToggleFavorite = { id -> canteenDetailsVm.toggleFavorite(id) },
                currentUserId = currentUserId,
                onEditReviewClick = { reviewId ->
                    navController.navigate(AlmaMensaRoute.WriteReview(reviewId = reviewId))
                },
                onDeleteReviewClick = canteenDetailsVm::deleteReview
            )
        }

        composable<AlmaMensaRoute.WriteReview> { backStackEntry ->
            val route = backStackEntry.toRoute<AlmaMensaRoute.WriteReview>()

            val reviewVm = koinViewModel<ReviewViewModel> { parametersOf(route.canteenId, route.reviewId) }
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
            val authVm = authViewModel()
            val state by authVm.state.collectAsStateWithLifecycle()
            AuthScreen(
                state = state,
                onEmailChange = authVm::onEmailChange,
                onSignIn = authVm::signIn,
                onSignUp = authVm::signUp,
                onGitHubSignIn = authVm::signInWithGitHub,
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

            val activity = LocalActivity.current as? FragmentActivity
            val authVm = authViewModel()
            val authState by authVm.state.collectAsStateWithLifecycle()

            ProfileScreen(
                profileState = state,
                onShowReviewClick = {
                    navController.navigate(AlmaMensaRoute.ShowReviews)
                },
                authState = authState,
                onLogoutSuccess = {
                    navController.navigate(AlmaMensaRoute.Home) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onEditClick = {
                    navController.navigate(AlmaMensaRoute.EditProfile)
                },
                onClearSnackbar = profileVm::clearSnackbar,
                onModifyPassword = {
                    if (activity != null) {
                        showBiometricPrompt(
                            activity = activity,
                            onSuccess = {
                                navController.navigate(AlmaMensaRoute.UpdatePassword)
                            },
                            onError = {
                                profileVm.onBiometricNotAvailable()
                            }
                        )
                    } else {
                        navController.navigate(AlmaMensaRoute.UpdatePassword)
                    }
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

        composable<AlmaMensaRoute.UpdatePassword> {
            val authVm = authViewModel()
            val state by authVm.state.collectAsStateWithLifecycle()
            UpdatePasswordScreen(
                state = state,
                onUpdatePassword = authVm::updatePassword,
                onUpdateSuccess = {
                    authVm.resetUpdateSuccess()
                    navController.popBackStack()
                }
            )
        }

        composable<AlmaMensaRoute.NearMe> {
            val nearMeVm = koinViewModel<NearMeViewModel>()
            val state by nearMeVm.state.collectAsStateWithLifecycle()

            NearMeScreen(
                state = state,
                onLoad = nearMeVm::loadNearbyCanteens,
                onRefresh = { nearMeVm.loadNearbyCanteens(isRefresh = true) },
                onCanteenClick = { canteen ->
                    navController.navigate(AlmaMensaRoute.CanteenDetails(canteen.id))
                },
                onDismissLocationAlert = nearMeVm::dismissLocationDisabledAlert,
                onDismissPermissionAlert = nearMeVm::dismissPermissionDeniedAlert,
                onMaxDistanceChange = nearMeVm::setMaxDistance
            )
        }

        composable<AlmaMensaRoute.ShowReviews> {
            val personalReviewVm = koinViewModel<PersonalReviewViewModel>()
            val state by personalReviewVm.state.collectAsStateWithLifecycle()

            PersonalReviewScreen(
                state = state,
                onRefresh = personalReviewVm::loadPersonalReviews,
                onReviewClick = { reviewId ->
                    navController.navigate(AlmaMensaRoute.WriteReview(reviewId = reviewId))
                },
                onDeleteClick = personalReviewVm::deleteReview
            )
        }
    }
}

@Composable
private fun authViewModel() = koinViewModel<AuthViewModel>(
    viewModelStoreOwner = LocalActivity.current as ComponentActivity
)