package it.unibo.almamensa.ui.screens.explore

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import it.unibo.almamensa.data.model.Canteen
import it.unibo.almamensa.ui.composables.CanteenList
import it.unibo.almamensa.ui.composables.EmptyCanteenList

@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel,
    onCanteenClick: (Canteen) -> Unit
) {
    val state by viewModel.state.collectAsState()

    // Show error as snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
}