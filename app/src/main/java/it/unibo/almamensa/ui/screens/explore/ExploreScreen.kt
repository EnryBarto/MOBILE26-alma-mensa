package it.unibo.almamensa.ui.screens.explore

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import it.unibo.almamensa.data.model.Canteen
import it.unibo.almamensa.ui.composables.CanteenList
import it.unibo.almamensa.ui.composables.EmptyCanteenList
import it.unibo.almamensa.utils.Dimensions
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExploreScreen(
    state: ExploreState,
    onCanteenClick: (Canteen) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    initialFavoritesOnly: Boolean = false,
    viewModel: ExploreViewModel = koinViewModel()
) {
    var showOnlyFavorites by remember { mutableStateOf(initialFavoritesOnly) }

    // Everytime the parameter changes we reload the data
    LaunchedEffect(showOnlyFavorites) {
        viewModel.loadCanteens(showOnlyFavorites)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Dimensions.screenHorizontalPadding)
    ) {
        Row (
            modifier = androidx . compose . ui . Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically // Allinea TextField e Button
        ) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Cerca mensa...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (state.searchQuery.isNotBlank()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Cancella")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = Dimensions.verticalItemsSpacing)
            )

            Spacer(modifier = Modifier.width( 8.dp))

            FilledTonalIconButton(
                onClick = { showOnlyFavorites = !showOnlyFavorites },
                modifier = Modifier
                    .size(56.dp), // set the size of the button same as the height
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = if (showOnlyFavorites) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = if (showOnlyFavorites) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Preferiti",
                    modifier = Modifier.size(25.dp)
                )
            }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.canteens.isEmpty() -> {
                    if (showOnlyFavorites) {
                        Text(
                            text = "Non hai ancora aggiunto nessuna mensa ai preferiti.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        EmptyCanteenList(modifier = Modifier.align(Alignment.Center))
                    }
                }
                else -> {
                    CanteenList(canteens = state.canteens, onCanteenClick = onCanteenClick)
                }
            }
        }
    }
}