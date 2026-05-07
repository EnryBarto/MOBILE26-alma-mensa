package it.unibo.almamensa.ui.composables

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import io.github.jan.supabase.auth.status.SessionStatus
import it.unibo.almamensa.ui.AlmaMensaRoute
import it.unibo.almamensa.ui.screens.auth.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppMenu(
    currentDestination: NavDestination?,
    navController: NavHostController,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    ModalDrawerSheet(
        // Navbar width
        modifier = Modifier.width(280.dp)
    ) {
        val authVm = koinViewModel<AuthViewModel>(
            viewModelStoreOwner = LocalActivity.current as ComponentActivity // Get the AuthState from the activity: it need to be shared between composables
        )
        val authState by authVm.state.collectAsStateWithLifecycle()

        Text(
            "AlmaMensa",
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
                navController.navigate(AlmaMensaRoute.Home) {
                    // Pop up to the starting destination of the graph to avoid building up
                    // a large stack of destinations on the back stack as users select items
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true // Save the state of the popped destination so it can be restored later
                    }
                    launchSingleTop = true // Avoid multiple copies of the same destination when reselecting the same item
                    restoreState = true // Restore state when reselecting a previously selected item (e.g., scroll position)
                }
            }
        )

        NavigationDrawerItem(
            label = { Text("Esplora") },
            selected = currentDestination?.hierarchy?.any { it.hasRoute<AlmaMensaRoute.Explore>() } == true,
            icon = { Icon(Icons.Default.Restaurant, contentDescription = null) },
            onClick = {
                scope.launch { drawerState.close() }
                navController.navigate(AlmaMensaRoute.Explore) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        if (authState.sessionStatus is SessionStatus.Authenticated) {
            NavigationDrawerItem(
                label = { Text("Profilo") },
                selected = currentDestination?.hierarchy?.any { it.hasRoute<AlmaMensaRoute.Profile>() } == true,
                icon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                onClick = {
                    scope.launch {
                        drawerState.close()
                        navController.navigate(AlmaMensaRoute.Profile) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )

            NavigationDrawerItem(
                label = { Text("Logout") },
                selected = false,
                icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                onClick = {
                    scope.launch {
                        drawerState.close()
                        authVm.logout()
                        navController.navigate(AlmaMensaRoute.Home) {
                            // Clean the navigation stack
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
                    scope.launch {
                        drawerState.close()
                        navController.navigate(AlmaMensaRoute.Auth) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
