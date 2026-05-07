package it.unibo.almamensa.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import it.unibo.almamensa.data.model.Canteen

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.core.net.toUri


@Composable
fun CanteenMapView(canteen: Canteen) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(false)
                    controller.setZoom(17.0)

                    val node = GeoPoint(canteen.latitude, canteen.longitude)
                    controller.setCenter(node)

                    val marker = Marker(this)
                    marker.position = node
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    marker.title = canteen.name
                    overlays.add(marker)

                    val gestureDetector = android.view.GestureDetector(
                        ctx,
                        object : android.view.GestureDetector.SimpleOnGestureListener() {
                            override fun onSingleTapUp(e: android.view.MotionEvent) = true
                        }
                    )

                    setOnTouchListener { v, event ->
                        if (gestureDetector.onTouchEvent(event)) {
                            val geolocIntentUri = "geo:${canteen.latitude},${canteen.longitude}?q=${Uri.encode(canteen.address)}".toUri()
                            val intent = Intent(Intent.ACTION_VIEW, geolocIntentUri)
                            if (intent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(intent)
                            }
                            v.performClick()
                        }
                        true
                    }

                }
            }
        )

        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                "Tocca per vedere la posizione",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}