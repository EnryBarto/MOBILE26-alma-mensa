package it.unibo.almamensa.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import it.unibo.almamensa.data.model.dto.ReviewWithCanteenDto
import it.unibo.almamensa.utils.Dimensions.verticalItemsSpacing
import kotlinx.datetime.Instant

@Composable
fun UserReviews(reviews: List<ReviewWithCanteenDto>) {
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
                    ReviewItem(review = review)

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

@Composable
fun ReviewItem(review: ReviewWithCanteenDto) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = review.canteens.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = review.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            RatingBar(score = review.score)
        }

        review.description?.let {
            if (it.isNotBlank()) {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Text(
            text = formatReviewDate(review.createdAt),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatReviewDate(dateInstant: Instant): String {
    return try {
        dateInstant.toString().split("T")[0]
    } catch (e: Exception) {
        dateInstant.toString()
    }
}
