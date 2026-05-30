package it.unibo.almamensa.ui.screens.profile.edit

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import it.unibo.almamensa.ui.composables.ProfilePhoto
import it.unibo.almamensa.ui.composables.SingleButtonBar
import it.unibo.almamensa.utils.Dimensions
import it.unibo.almamensa.utils.rememberCameraLauncher

@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    state: EditProfileState,
    onNameChange: (String) -> Unit,
    onSurnameChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteImageClick: () -> Unit,
    onAvatarPicked: (Uri) -> Unit,
    onSaveSuccess: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onSaveSuccess()
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onAvatarPicked(it) }
    }

    val (_, takePicture) = rememberCameraLauncher { uri ->
        onAvatarPicked(uri)
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> CircularProgressIndicator()

            else -> {
                EditProfileContent(
                    state = state,
                    onNameChange = onNameChange,
                    onSurnameChange = onSurnameChange,
                    onDeleteImageClick = onDeleteImageClick,
                    onPickImageClick = { imagePickerLauncher.launch("image/*") },
                    onTakePhotoClick = { takePicture() }
                )

                SingleButtonBar(
                    text = "Salva modifiche",
                    icon = Icons.Default.Save,
                    onClick = onSaveClick,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun EditProfileContent(
    state: EditProfileState,
    onNameChange: (String) -> Unit,
    onSurnameChange: (String) -> Unit,
    onPickImageClick: () -> Unit,
    onTakePhotoClick: () -> Unit,
    onDeleteImageClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimensions.screenHorizontalPadding)
            .padding(bottom = Dimensions.bottomPaddingButtonBar),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        var showDialog by remember { mutableStateOf(false) }

        Box(contentAlignment = Alignment.Center) {
            val profilePhotoUrl = remember(state.profilePhotoUrl, state.imageVersion) {
                if (state.profilePhotoUrl != null && state.imageVersion > 0) {
                    "${state.profilePhotoUrl}?v=${state.imageVersion}"
                } else {
                    state.profilePhotoUrl
                }
            }

            ProfilePhoto(profilePhotoUrl = profilePhotoUrl)

            if (state.profilePhotoUrl != null) {
                IconButton(
                    onClick = onDeleteImageClick,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.9f),
                            shape = CircleShape
                        )
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Rimuovi foto profilo",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            IconButton(
                onClick = { showDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.9f),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = if (state.profilePhotoUrl != null) Icons.Default.Edit else Icons.Default.Add,
                    contentDescription = "Cambia foto profilo",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (showDialog) {
            Dialog(
                onDismissRequest = { showDialog = false }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Seleziona sorgente immagine",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        TextButton(
                            onClick = {
                                showDialog = false
                                onPickImageClick()
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Photo,
                                contentDescription = null,
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text("Scegli dalla Galleria")
                        }

                        TextButton(
                            onClick = {
                                showDialog = false
                                onTakePhotoClick()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text("Scatta una Foto")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = state.name,
            onValueChange = onNameChange,
            label = { Text("Nome") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.surname,
            onValueChange = onSurnameChange,
            label = { Text("Cognome") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}