package it.unibo.almamensa.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import it.unibo.almamensa.data.model.dto.ReviewWithCanteenDto
import it.unibo.almamensa.utils.Dimensions.horizontalPaddingUserReviews
import it.unibo.almamensa.utils.Dimensions.verticalItemsSpacing
import kotlinx.datetime.Instant

@Composable
fun UserReviews(
    reviews: List<ReviewWithCanteenDto>,
    onReviewClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = verticalItemsSpacing, horizontal = horizontalPaddingUserReviews),
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
                    ReviewItem(review = review, onReviewClick = onReviewClick, onDeleteClick = onDeleteClick)

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
fun ReviewItem(review: ReviewWithCanteenDto, onReviewClick: (Long) -> Unit, onDeleteClick: (Long) -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false }, // Close dialog if user clicks outside
            title = { Text(text = "Conferma eliminazione") },
            text = { Text(text = "Sei sicuro di voler eliminare questa recensione? l'azione non è reversibile.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClick(review.id!!)
                        showDeleteDialog = false // Close dalog after confiirming
                    }
                ) {
                    Text("Elimina", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Annulla")
                }
            }
        )
    }

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
                text = review.canteen.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Elimina",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    text = "Elimina",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onReviewClick(review.id!!) },
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
