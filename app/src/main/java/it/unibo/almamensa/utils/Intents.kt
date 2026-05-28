package it.unibo.almamensa.utils

import android.content.Context
import android.content.Intent
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

fun openLocationSettings(context: Context) {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}

fun openWirelessSettings(ctx: Context) {
    val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    if (intent.resolveActivity(ctx.packageManager) != null) {
        ctx.applicationContext.startActivity(intent)
    }
}
