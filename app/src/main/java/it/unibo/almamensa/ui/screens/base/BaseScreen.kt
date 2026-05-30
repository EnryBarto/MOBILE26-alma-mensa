package it.unibo.almamensa.ui.screens.base

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import it.unibo.almamensa.ui.AlmaMensaNavGraph
import it.unibo.almamensa.ui.AlmaMensaRoute
import it.unibo.almamensa.ui.composables.AppBar
import it.unibo.almamensa.ui.composables.AppMenu
import it.unibo.almamensa.ui.composables.NoConnectivityScreen
import it.unibo.almamensa.ui.topLevelRoutes
import it.unibo.almamensa.utils.Dimensions
import it.unibo.almamensa.utils.observeConnectivity
import kotlinx.coroutines.launch

@Composable
fun BaseScreen(
    isDarkTheme: Boolean = false
) {
    val context = LocalContext.current

    // Observe the connectivity in realtime
    val isConnected by context.observeConnectivity().collectAsState(initial = true)

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Close the drawer if it's open when the back button is pressed instead of closing the application
    BackHandler(enabled = drawerState.isOpen) {
        scope.launch {
            drawerState.close()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        // Enable gestures is top routes, except for MapScreen
        gesturesEnabled = currentDestination?.hierarchy?.any { dest ->
            topLevelRoutes.any { route ->
                dest.hasRoute(route) && !dest.hasRoute(AlmaMensaRoute.Map::class)
            }
        } == true,
        drawerContent = { AppMenu(currentDestination, navController, drawerState, scope) }
    ) {
        Scaffold(
            topBar = { AppBar(currentDestination, navController, scope, drawerState) }
        ) { innerPadding ->

            AlmaMensaNavGraph(
                navController = navController,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(top = Dimensions.topAppBarBottomMargin),
                isDarkTheme = isDarkTheme
            )
        }
    }

    if (!isConnected) {
        NoConnectivityScreen()
    }
}