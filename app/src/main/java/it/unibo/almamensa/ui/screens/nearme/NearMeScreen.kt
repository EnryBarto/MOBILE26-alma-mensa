package it.unibo.almamensa.ui.screens.nearme

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import it.unibo.almamensa.data.model.Canteen
import it.unibo.almamensa.ui.composables.RefreshableCanteenList
import it.unibo.almamensa.utils.Dimensions
import it.unibo.almamensa.utils.Dimensions.verticalItemsSpacing
import it.unibo.almamensa.utils.PermissionStatus
import it.unibo.almamensa.utils.openAppSettings
import it.unibo.almamensa.utils.openLocationSettings
import it.unibo.almamensa.utils.rememberMultiplePermissions

@Composable
fun NearMeScreen(
    state: NearMeState,
    onLoad: () -> Unit,
    onRefresh: () -> Unit,
    onCanteenClick: (Canteen) -> Unit,
    onDismissLocationAlert: () -> Unit,
    onMaxDistanceChange: (Float) -> Unit,
    onDismissPermissionAlert: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val locationPermission = rememberMultiplePermissions(
        listOf(Manifest.permission.ACCESS_FINE_LOCATION)
    ) { statuses ->
        when {
            statuses.values.any { it == PermissionStatus.Granted } -> onLoad()
            statuses.values.all { it == PermissionStatus.PermanentlyDenied } -> openAppSettings(context)
            else -> {}
        }
    }

    LaunchedEffect(Unit) {
        if (locationPermission.statuses.values.any { it == PermissionStatus.Granted }) {
            onLoad()
        } else {
            locationPermission.launchPermissionRequest()
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    if (state.showLocationDisabledAlert) {
        AlertDialog(
            onDismissRequest = onDismissLocationAlert,
            title = { Text("GPS disabilitato") },
            text = { Text("Abilita il GPS per vedere le mense più vicine.") },
            confirmButton = {
                TextButton(onClick = {
                    onDismissLocationAlert()
                    openLocationSettings(context)
                }) { Text("Impostazioni") }
            },
            dismissButton = {
                TextButton(onClick = onDismissLocationAlert) { Text("Annulla") }
            }
        )
    }

    if (state.showPermissionDeniedAlert) {
        AlertDialog(
            onDismissRequest = onDismissPermissionAlert,
            title = { Text("Permesso posizione") },
            text = { Text("Concedi il permesso di posizione per vedere le mense più vicine.") },
            confirmButton = {
                TextButton(onClick = {
                    onDismissPermissionAlert()
                    locationPermission.launchPermissionRequest()
                }) { Text("Concedi") }
            },
            dismissButton = {
                TextButton(onClick = onDismissPermissionAlert) { Text("Annulla") }
            }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            else -> {
                NearMeContent(
                    modifier = modifier,
                    state = state,
                    onCanteenClick = onCanteenClick,
                    onMaxDistanceChange = onMaxDistanceChange,
                    onRefresh = onRefresh
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NearMeContent(
    modifier: Modifier = Modifier,
    state: NearMeState,
    onCanteenClick: (Canteen) -> Unit = {},
    onMaxDistanceChange: (Float) -> Unit = {},
    onRefresh: () -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(verticalItemsSpacing)
    ) {
        Text(
            text = "Mense più vicine a te",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(horizontal = Dimensions.screenHorizontalPadding)
                .padding(vertical = 8.dp)
        )

        Row(
            modifier = Modifier.padding(horizontal = Dimensions.screenHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Entro:")
            Slider(
                value = state.maxDistanceKm,
                onValueChange = onMaxDistanceChange,
                valueRange = 0.5f..15f,
                modifier = Modifier.weight(1f)
            )
            Text("%.1f km".format(state.maxDistanceKm))
        }

        RefreshableCanteenList(
            items = state.canteenListItems,
            onCanteenClick = onCanteenClick,
            isRefreshing = state.isRefreshing,
            onRefresh = onRefresh,
            emptyMessage = "Nessuna mensa trovata",
            contentPadding = PaddingValues(
                horizontal = Dimensions.screenHorizontalPadding,
                vertical = 8.dp
            )
        )
    }
}