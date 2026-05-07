package it.unibo.almamensa.ui.screens.base

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import it.unibo.almamensa.ui.AlmaMensaNavGraph
import it.unibo.almamensa.ui.composables.AppBar
import it.unibo.almamensa.ui.composables.AppMenu
import it.unibo.almamensa.ui.topLevelRoutes
import kotlinx.coroutines.launch

@Composable
fun BaseScreen() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Get current user position
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
        gesturesEnabled = currentDestination?.hierarchy?.any { dest ->
            topLevelRoutes.any { route -> dest.hasRoute(route) }
        } == true, // Enable swipe to open menu bar only in home screen
        drawerContent = { AppMenu(currentDestination, navController, drawerState, scope) }
    ) {
        Scaffold(
            topBar = { AppBar(currentDestination, navController, scope, drawerState) }
        ) { innerPadding ->
            // Invoke the NavGraph to manage the screen that need to be visualized
            AlmaMensaNavGraph(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}