package it.unibo.almamensa.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import it.unibo.almamensa.data.model.dto.ReviewWithUserDto
import it.unibo.almamensa.data.model.dto.toReview
import it.unibo.almamensa.utils.Dimensions.verticalItemsSpacing

@Composable
fun CanteenReviews(
    reviews: List<ReviewWithUserDto>,
    onEditReviewClick: (Long) -> Unit,
    onDeleteReviewClick: (Long) -> Unit,
    currentUserId: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = verticalItemsSpacing),
        verticalArrangement = Arrangement.spacedBy(verticalItemsSpacing)
    ) {
        Text(
            text = "Recensioni",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        if (reviews.isEmpty()) {
            Text(
                text = "Ancora nessuna recensione per questa mensa.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            // Using a simple Column instead of LazyColumn to avoid nesting scrollable containers
            Column(
                verticalArrangement = Arrangement.spacedBy(verticalItemsSpacing),
                modifier = Modifier.fillMaxWidth()
            ) {
                reviews.forEachIndexed { index, review ->
                    val isOwner = review.userId == currentUserId

                    ReviewCard(
                        title = "${review.user.name} ${review.user.surname}",
                        review = review.toReview(),
                        onEditClick = if (isOwner) { { onEditReviewClick(review.id) } } else null,
                        onDeleteClick = if (isOwner) { { onDeleteReviewClick(review.id) } } else null
                    )

                    if (index < reviews.lastIndex) {
                        HorizontalDivider(
                            thickness = 0.3.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }
    }
}