package it.unibo.almamensa.ui.composables

import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import it.unibo.almamensa.ui.topLevelRoutes
import it.unibo.almamensa.utils.Dimensions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    currentDestination: NavDestination?,
    navController: NavHostController,
    scope: CoroutineScope,
    drawerState: DrawerState
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    CenterAlignedTopAppBar(
        title = { Text("AlmaMensa") },
        navigationIcon = {
            val isTopLevelDestination = currentDestination?.hierarchy?.any { dest ->
                topLevelRoutes.any { route -> dest.hasRoute(route) }
            } == true

            if (isTopLevelDestination) {
                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                }
            } else {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Indietro"
                    )
                }
            }
        },
        expandedHeight = if (isLandscape) Dimensions.topAppBarLandscapeHeight else Dimensions.topAppBarPortraitHeight
    )
}