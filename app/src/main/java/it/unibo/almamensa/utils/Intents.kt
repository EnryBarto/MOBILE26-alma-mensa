package it.unibo.almamensa.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.net.toUri
import it.unibo.almamensa.data.model.Canteen

fun openDialer(context: Context, phoneNumber: String) {
    val uri = "tel:$phoneNumber".toUri()
    val intent = Intent(Intent.ACTION_DIAL, uri)
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}

fun openMaps(context: Context, canteen: Canteen) {
    val query = "${canteen.address} (${canteen.name})".toUri()
    val uri = "geo:${canteen.latitude},${canteen.longitude}?q=$query".toUri()
    val intent = Intent(Intent.ACTION_VIEW, uri)
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}

fun shareCanteenLink(context: Context, canteenId: Long) {
    val url = "https://almamensa-e4631.web.app/canteen/$canteenId"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, "Hey! Guarda che bella mensa che ho trovato grazie ad AlmaMensa!: $url")
    }
    val chooserIntent = Intent.createChooser(intent, "Condividi via")
    if (chooserIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(chooserIntent)
    }
}

fun openLocationSettings(context: Context) =
    openSettings(context, Settings.ACTION_LOCATION_SOURCE_SETTINGS)

fun openWirelessSettings(context: Context) =
    openSettings(context, Settings.ACTION_WIRELESS_SETTINGS)

fun openSecuritySettings(context: Context) =
    openSettings(context, Settings.ACTION_SECURITY_SETTINGS)

fun openAppSettings(context: Context) {
    val data = Uri.fromParts("package", context.packageName, null)
    openSettings(context, Settings.ACTION_APPLICATION_DETAILS_SETTINGS, data)
}

private fun openSettings(context: Context, action: String, data: Uri? = null) {
    val intent = Intent(action).apply {
        this.data = data
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}
