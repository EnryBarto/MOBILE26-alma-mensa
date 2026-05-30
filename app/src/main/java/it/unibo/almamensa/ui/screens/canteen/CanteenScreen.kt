package it.unibo.almamensa.ui.screens.canteen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import it.unibo.almamensa.data.model.Canteen
import it.unibo.almamensa.data.model.dto.ReviewWithUserDto
import it.unibo.almamensa.ui.composables.CanteenReviews
import it.unibo.almamensa.ui.composables.DoubleButtonBar
import it.unibo.almamensa.ui.composables.InfoItem
import it.unibo.almamensa.ui.composables.SingleButtonBar
import it.unibo.almamensa.utils.Dimensions
import it.unibo.almamensa.utils.Dimensions.verticalItemsSpacing
import it.unibo.almamensa.utils.openDialer
import it.unibo.almamensa.utils.openMaps
import it.unibo.almamensa.utils.shareCanteenLink

@Composable
fun CanteenScreen(
    modifier: Modifier = Modifier,
    state: CanteenState,
    onReview: () -> Unit,
    onClearError: () -> Unit,
    onToggleFavorite: (Long) -> Unit = {},
    onEditReviewClick: (Long) -> Unit,
    onDeleteReviewClick: (Long) -> Unit,
    currentUserId: String? = null
) {
    val ctx = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Show error as snackbar
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            onClearError()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            state.canteen != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = Dimensions.bottomPaddingButtonBar)
                        .padding(horizontal = Dimensions.screenHorizontalPadding),
                ) {
                    CanteenDetailsContent(
                        state.canteen,
                        state.isFavorite,
                        state.reviews,
                        onToggleFavorite,
                        onEditReviewClick,
                        onDeleteReviewClick,
                        currentUserId
                    )
                }

                if (!state.isLoggedIn) {
                    SingleButtonBar(
                        text = "Condividi",
                        icon = Icons.Default.Share,
                        onClick = { shareCanteenLink(ctx, state.canteen.id) },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                } else {
                    DoubleButtonBar(
                        textPrimary = "Valuta",
                        iconPrimary = Icons.Default.RateReview,
                        onClickPrimary = onReview,
                        textSecondary = "Condividi",
                        iconSecondary = Icons.Default.Share,
                        onClickSecondary = { shareCanteenLink(ctx, state.canteen.id) },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = if (state.isLoggedIn) Dimensions.bottomPaddingButtonBar else Dimensions.bottomPaddingNoBar)
        )
    }
}

@Composable
private fun CanteenDetailsContent(
    canteen: Canteen,
    isFavorite: Boolean,
    reviews: List<ReviewWithUserDto>,
    onToggleFavorite: (Long) -> Unit,
    onEditReviewClick: (Long) -> Unit,
    onDeleteReviewClick: (Long) -> Unit,
    currentUserId: String? = null
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(verticalItemsSpacing)
    ) {
        NameCard(canteen, onToggleFavorite, isFavorite)
        HorizontalDivider()
        InfoCard(canteen)
        HorizontalDivider()
        CanteenReviews(
            reviews = reviews,
            onEditReviewClick = { onEditReviewClick(it) },
            onDeleteReviewClick = { onDeleteReviewClick(it) },
            currentUserId = currentUserId
        )
    }
}

@Composable
private fun NameCard(canteen: Canteen, onToggleFavorite: (Long) -> Unit, isFavorite: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = verticalItemsSpacing),
        verticalArrangement = Arrangement.spacedBy(verticalItemsSpacing)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = canteen.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarOutline,
                contentDescription = if (isFavorite) "Rimuovi dai preferiti" else "Aggiungi ai preferiti",
                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                modifier = Modifier.clickable { onToggleFavorite(canteen.id) }
            )
        }

        if (!canteen.description.isNullOrBlank()) {
            Text(
                text = canteen.description,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun InfoCard(canteen: Canteen) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = verticalItemsSpacing),
        verticalArrangement = Arrangement.spacedBy(verticalItemsSpacing)
    ) {
        Text(
            text = "Informazioni",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        InfoItem(
            icon = Icons.Default.LocationOn,
            label = "Indirizzo",
            value = canteen.address,
            onClick = { openMaps(context, canteen) }
        )

        if (!canteen.phone.isNullOrBlank()) {
            InfoItem(
                icon = Icons.Default.Phone,
                label = "Telefono",
                value = canteen.phone,
                onClick = { openDialer(context, canteen.phone) }
            )
        }
    }
}