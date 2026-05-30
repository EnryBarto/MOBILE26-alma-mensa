package it.unibo.almamensa.ui.screens.profile.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Feed
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import io.github.jan.supabase.auth.status.SessionStatus
import it.unibo.almamensa.data.model.User
import it.unibo.almamensa.ui.composables.DoubleButtonBar
import it.unibo.almamensa.ui.composables.ProfilePhoto
import it.unibo.almamensa.ui.screens.auth.AuthState
import it.unibo.almamensa.utils.Dimensions
import it.unibo.almamensa.utils.Dimensions.verticalItemsSpacing
import it.unibo.almamensa.utils.openSecuritySettings

@Composable
fun ProfileScreen(
    onShowReviewClick: () -> Unit,
    profileState: ProfileState,
    onModifyPassword: () -> Unit,
    onClearSnackbar: () -> Unit,
    authState: AuthState,
    modifier: Modifier = Modifier,
    onLogoutSuccess: () -> Unit,
    onEditClick: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Auto-redirect to home when the user has logged out
    LaunchedEffect(authState.sessionStatus) {
        if (authState.sessionStatus is SessionStatus.NotAuthenticated) {
            onLogoutSuccess()
        }
    }

    LaunchedEffect(profileState.snackbarMessage) {
        profileState.snackbarMessage?.let { message ->
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = "Imposta",
                duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) {
                openSecuritySettings(context)
            }
            onClearSnackbar()
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            profileState.isLoading -> CircularProgressIndicator()
            profileState.errorMessage != null -> Text(
                text = profileState.errorMessage,
                color = MaterialTheme.colorScheme.error
            )
            profileState.user != null -> {
                ProfileContent(
                    profileState.user,
                    imageVersion = profileState.imageVersion,
                    onModifyPassword = onModifyPassword
                )
                DoubleButtonBar(
                    textPrimary = "Modifica",
                    iconPrimary = Icons.Default.Edit,
                    onClickPrimary = onEditClick,
                    textSecondary = "Recensioni",
                    iconSecondary = Icons.AutoMirrored.Filled.Feed,
                    onClickSecondary = onShowReviewClick,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
            else -> CircularProgressIndicator()
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = Dimensions.bottomPaddingButtonBar)
        )
    }
}

@Composable
private fun ProfileContent(
    user: User,
    imageVersion: Long,
    onModifyPassword: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimensions.screenHorizontalPadding)
            .padding(bottom = Dimensions.bottomPaddingButtonBar),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(verticalItemsSpacing)
    ) {

        val profilePhotoUrl = remember(user.profilePhotoUrl, imageVersion) {
            if (user.profilePhotoUrl != null && imageVersion > 0) {
                "${user.profilePhotoUrl}?v=$imageVersion"
            } else {
                user.profilePhotoUrl
            }
        }

        ProfilePhoto(profilePhotoUrl)

        Column (
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "${user.name} ${user.surname}",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = user.email,
                style = MaterialTheme.typography.labelMedium
            )
        }

        TextButton(onClick = onModifyPassword) {
            Text(
                text = "Modifica Password",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
