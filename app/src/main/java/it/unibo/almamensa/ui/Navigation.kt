package it.unibo.almamensa.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import it.unibo.almamensa.ui.screens.home.HomeScreen
import it.unibo.almamensa.ui.screens.home.HomeViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

sealed interface AlmaMensaRoute {
    @Serializable data object Home : AlmaMensaRoute
}

@Composable
fun AlmaMensaNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AlmaMensaRoute.Home
    ) {
        composable<AlmaMensaRoute.Home> {
            val homeVm = koinViewModel<HomeViewModel>()
            val state by homeVm.state.collectAsStateWithLifecycle()
            HomeScreen(state, navController)
        }
    }
}
