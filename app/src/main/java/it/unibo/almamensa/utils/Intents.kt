package it.unibo.almamensa.utils

import android.content.Context
import android.content.Intent
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