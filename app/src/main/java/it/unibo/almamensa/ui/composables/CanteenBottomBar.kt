package it.unibo.almamensa.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import it.unibo.almamensa.utils.Dimensions.screenHorizontalPadding
import it.unibo.almamensa.utils.shareCanteenLink

@Composable
fun CanteenBottomBar(
    onReview: () -> Unit,
    canteenId: Long,
    modifier: Modifier = Modifier,
    loggedIn: Boolean
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .padding(horizontal = screenHorizontalPadding)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            val context = LocalContext.current

            OutlinedButton(
                onClick = { shareCanteenLink(context, canteenId) },
                modifier = Modifier.weight(1f),
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Condividi")
            }

            if (loggedIn) {
                Button(
                    onClick = onReview,
                    modifier = Modifier.weight(1f),
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                ) {
                    Icon(
                        imageVector = Icons.Default.RateReview,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Valuta")
                }
            }
        }
    }
}