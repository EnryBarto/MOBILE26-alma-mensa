package it.unibo.almamensa.ui.screens.explore

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import it.unibo.almamensa.data.model.Canteen
import it.unibo.almamensa.ui.composables.CanteenList
import it.unibo.almamensa.ui.composables.EmptyCanteenList
import it.unibo.almamensa.utils.Dimensions

@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel,
    onCanteenClick: (Canteen) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Dimensions.screenHorizontalPadding)
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            state.canteens.isEmpty() -> {
                EmptyCanteenList(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                CanteenList(
                    canteens = state.canteens,
                    onCanteenClick = onCanteenClick
                )
            }
        }
    }
}