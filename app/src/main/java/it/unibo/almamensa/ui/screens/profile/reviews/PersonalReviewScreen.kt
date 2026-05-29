package it.unibo.almamensa.ui.screens.profile.reviews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import it.unibo.almamensa.data.model.dto.ReviewWithCanteenDto
import it.unibo.almamensa.data.model.dto.toReview
import it.unibo.almamensa.ui.composables.ReviewCard
import it.unibo.almamensa.utils.Dimensions
import it.unibo.almamensa.utils.Dimensions.verticalItemsSpacing

@Composable
fun PersonalReviewScreen(
    state: PersonalReviewState,
    onRefresh: () -> Unit,
    onReviewClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit
) {
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        onRefresh()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimensions.screenHorizontalPadding)
    ) {
        when {
            state.isLoading && state.reviews.isEmpty() -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            else -> {
                UserReviews(
                    reviews = state.reviews,
                    onReviewClick = onReviewClick,
                    onDeleteClick = onDeleteClick
                )
            }
        }

        state.errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun UserReviews(
    reviews: List<ReviewWithCanteenDto>,
    onReviewClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(verticalItemsSpacing)
    ) {
        item {
            Text(
                text = "Le tue recensioni (${reviews.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (reviews.isEmpty()) {
            item {
                Text(
                    text = "Non hai ancora scritto nessuna recensione.",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            itemsIndexed(reviews) { index, review ->
                ReviewCard(
                    title = review.canteen.name,
                    review = review.toReview(),
                    onEditClick = { onReviewClick(review.id!!) },
                    onDeleteClick = { onDeleteClick(review.id!!) }
                )

                if (index < reviews.lastIndex) {
                    Spacer(Modifier.height(verticalItemsSpacing))

                    HorizontalDivider(
                        thickness = 0.3.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    }
}