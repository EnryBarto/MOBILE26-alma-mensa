package it.unibo.almamensa.ui.composables

import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import it.unibo.almamensa.ui.topLevelRoutes
import it.unibo.almamensa.utils.Dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    currentDestination: NavDestination?,
    onNavigateUp: () -> Unit,
    onMenuClick: () -> Unit
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    CenterAlignedTopAppBar(
        title = { Text("AlmaMensa") },
        navigationIcon = {
            val isTopLevelDestination = currentDestination?.hierarchy?.any { dest ->
                topLevelRoutes.any { route -> dest.hasRoute(route) }
            } == true

            if (isTopLevelDestination) {
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                }
            } else {
                IconButton(onClick = onNavigateUp) {
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