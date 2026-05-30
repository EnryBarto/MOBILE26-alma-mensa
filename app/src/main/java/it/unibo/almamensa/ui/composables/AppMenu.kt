package it.unibo.almamensa.ui.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import it.unibo.almamensa.R
import it.unibo.almamensa.ui.AlmaMensaRoute
import it.unibo.almamensa.utils.Dimensions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppMenu(
    currentDestination: NavDestination?,
    navController: NavHostController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    isAuthenticated: Boolean,
    onLogout: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(Dimensions.drawerMenuWidth)
    ) {
        Text(
            stringResource(R.string.app_name),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleLarge
        )
        HorizontalDivider()

        Spacer(modifier = Modifier.height(10.dp))

        NavigationDrawerItem(
            label = { Text("Home") },
            selected = currentDestination?.hierarchy?.any { it.hasRoute<AlmaMensaRoute.Home>() } == true,
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            onClick = {
                scope.launch { drawerState.close() }
                navController.navigateTopLevel(AlmaMensaRoute.Home)
            }
        )

        NavigationDrawerItem(
            label = { Text("Esplora") },
            selected = currentDestination?.hierarchy?.any { it.hasRoute<AlmaMensaRoute.Explore>() } == true,
            icon = { Icon(Icons.Default.Restaurant, contentDescription = null) },
            onClick = {
                scope.launch { drawerState.close() }
                navController.navigateTopLevel(AlmaMensaRoute.Explore)
            }
        )

        NavigationDrawerItem(
            label = { Text("Mappa") },
            selected = currentDestination?.hierarchy?.any { it.hasRoute<AlmaMensaRoute.Map>() } == true,
            icon = { Icon(Icons.Default.Map, contentDescription = null) },
            onClick = {
                scope.launch { drawerState.close() }
                navController.navigateTopLevel(AlmaMensaRoute.Map)
            }
        )

        HorizontalDivider(
            modifier = Modifier.padding(ButtonDefaults.ButtonWithIconContentPadding),
            thickness = 0.75.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        if (isAuthenticated) {
            NavigationDrawerItem(
                label = { Text("Profilo") },
                selected = currentDestination?.hierarchy?.any { it.hasRoute<AlmaMensaRoute.Profile>() } == true,
                icon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                onClick = {
                    scope.launch { drawerState.close() }
                    navController.navigateTopLevel(AlmaMensaRoute.Profile)
                }
            )

            NavigationDrawerItem(
                label = { Text("Logout") },
                selected = false,
                icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                onClick = {
                    scope.launch {
                        drawerState.close()
                        onLogout()
                        navController.navigate(AlmaMensaRoute.Home) {
                            // Clean navigation stack
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        } else {
            NavigationDrawerItem(
                label = { Text("Accedi") },
                selected = currentDestination?.hierarchy?.any { it.hasRoute<AlmaMensaRoute.Auth>() } == true,
                icon = { Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null) },
                onClick = {
                    scope.launch { drawerState.close() }
                    navController.navigateTopLevel(AlmaMensaRoute.Auth)
                }
            )
        }

        NavigationDrawerItem(
            label = { Text("Impostazioni") },
            selected = currentDestination?.hierarchy?.any { it.hasRoute<AlmaMensaRoute.Settings>() } == true,
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            onClick = {
                scope.launch { drawerState.close() }
                navController.navigateTopLevel(AlmaMensaRoute.Settings)
            }
        )
    }
}

// Function to navigato to a TopLevel destination, avoiding multiple copies of the same destination
private fun NavHostController.navigateTopLevel(route: Any) {
    navigate(route) {
        // Pop up to the starting destination of the graph to avoid building up
        // a large stack of destinations on the back stack as users select items
        popUpTo(graph.findStartDestination().id) {
            saveState = true // Save the state of the popped destination so it can be restored later
        }
        launchSingleTop = true // Avoid multiple copies of the same destination when reselecting the same item
        restoreState = true // Restore state when reselecting a previously selected item (e.g., scroll position)
    }
}