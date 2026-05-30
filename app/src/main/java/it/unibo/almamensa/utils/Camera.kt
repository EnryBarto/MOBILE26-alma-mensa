package it.unibo.almamensa.utils

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

@Composable
fun rememberCameraLauncher(
    onPictureTaken: (Uri) -> Unit = {}
): Pair<Uri?, () -> Unit> {
    var launcherUri by remember { mutableStateOf<Uri?>(null) }
    var pictureUri by remember { mutableStateOf<Uri?>(null) }

    val ctx = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { pictureTaken ->
        if (pictureTaken) launcherUri?.let { uri ->
            pictureUri = uri
            saveToGallery(ctx, uri)
            onPictureTaken(uri)
        }
    }

    val takePicture = {
        val file = File.createTempFile("tmp_image", ".jpg", ctx.externalCacheDir)
        launcherUri = FileProvider.getUriForFile(ctx, "${ctx.packageName}.provider", file)
        launcher.launch(launcherUri!!)
    }

    return pictureUri to takePicture
}

private fun saveToGallery(ctx: android.content.Context, uri: Uri) {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "ProfilePhoto_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/AlmaMensa")
        }
    }

    val resolver = ctx.contentResolver
    val galleryUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    galleryUri?.let { dest ->
        resolver.openInputStream(uri)?.use { input ->
            resolver.openOutputStream(dest)?.use { output ->
                input.copyTo(output)
            }
        }
    }
}