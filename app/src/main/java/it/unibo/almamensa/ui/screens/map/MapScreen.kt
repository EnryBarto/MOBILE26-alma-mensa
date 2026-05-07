package it.unibo.almamensa.ui.screens.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import it.unibo.almamensa.ui.composables.CanteensMapView

@Composable
fun MapScreen(
    viewModel: MapViewModel
) {
    val canteens by viewModel.canteens.collectAsState()
    CanteensMapView(canteens)
}