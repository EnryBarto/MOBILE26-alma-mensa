package it.unibo.almamensa.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import it.unibo.almamensa.data.model.Canteen
import it.unibo.almamensa.ui.model.CanteenListItem
import it.unibo.almamensa.utils.Dimensions.verticalItemsSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefreshableCanteenList(
    items: List<CanteenListItem>,
    onCanteenClick: (Canteen) -> Unit,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    emptyMessage: String? = null,
    contentPadding: PaddingValues = PaddingValues(bottom = 8.dp)
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(verticalItemsSpacing),
            contentPadding = contentPadding,
            modifier = Modifier.fillMaxSize()
        ) {
            if (items.isEmpty() && emptyMessage != null) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize()) {
                        EmptyCanteenList(
                            modifier = Modifier.align(Alignment.Center),
                            message = emptyMessage
                        )
                    }
                }
            } else {
                items(items, key = { it.canteen.id }) { item ->
                    CanteenCard(
                        canteen = item.canteen,
                        onClick = { onCanteenClick(item.canteen) },
                        distanceInfo = item.distanceInfo
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyCanteenList(
    modifier: Modifier = Modifier,
    message: String = "Nessuna mensa disponibile"
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Place,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
