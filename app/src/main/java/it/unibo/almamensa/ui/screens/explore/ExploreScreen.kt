package it.unibo.almamensa.ui.screens.explore

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import it.unibo.almamensa.data.model.Canteen
import it.unibo.almamensa.ui.composables.RefreshableCanteenList
import it.unibo.almamensa.ui.model.CanteenListItem
import it.unibo.almamensa.utils.Dimensions

@Composable
fun ExploreScreen(
    state: ExploreState,
    onCanteenClick: (Canteen) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onLoadCanteens: (showFavorites: Boolean, isRefresh: Boolean) -> Unit,
    onToggleFavorites: () -> Unit,
    showOnlyFavorites: Boolean,
    modifier: Modifier = Modifier,
) {

    // Everytime the parameter changes we reload the data
    LaunchedEffect(showOnlyFavorites) {
        onLoadCanteens(showOnlyFavorites, false)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Dimensions.screenHorizontalPadding)
    ) {
        ExploreNavBar(
            searchQuery = state.searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            showOnlyFavorites = showOnlyFavorites,
            onToggleFavorites = onToggleFavorites
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    RefreshableCanteenList(
                        items = state.canteens.map { CanteenListItem(it) },
                        onCanteenClick = onCanteenClick,
                        isRefreshing = state.isRefreshing,
                        onRefresh = { onLoadCanteens(showOnlyFavorites, true) },
                        emptyMessage =
                            if (showOnlyFavorites)
                                "Non hai ancora aggiunto nessuna mensa ai preferiti"
                            else
                                "Nessuna mensa trovata"
                    )
                }
            }
        }
    }
}

@Composable
private fun ExploreNavBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    showOnlyFavorites: Boolean,
    onToggleFavorites: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Cerca mensa...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
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

        Spacer(modifier = Modifier.width(8.dp))

        FilledTonalIconButton(
            onClick = onToggleFavorites,
            modifier = Modifier
                .padding(bottom = Dimensions.verticalItemsSpacing)
                .fillMaxHeight()
                .aspectRatio(1f),
            shape = CircleShape,
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = if (showOnlyFavorites) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.secondaryContainer,
                contentColor = if (showOnlyFavorites) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Icon(
                imageVector = if (showOnlyFavorites) Icons.Default.Star else Icons.Default.StarOutline,
                contentDescription = "Preferiti",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}