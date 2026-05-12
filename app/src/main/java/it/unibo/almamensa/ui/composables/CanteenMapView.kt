package it.unibo.almamensa.ui.composables

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import it.unibo.almamensa.data.model.Canteen
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Composable
fun CanteensMapView(canteens: List<Canteen>) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember { MapView(context) }
    val myLocationOverlay = remember {
        MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
    }

    // State to handle location permission
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED        )
    }

    // Request location permission if not enabled
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
    }

    // Request location permission on initial composition
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    mapView.onResume()
                    if (hasLocationPermission) {
                        myLocationOverlay.enableMyLocation()
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    myLocationOverlay.disableMyLocation()
                    mapView.onPause()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        AndroidView(
            factory = { ctx ->
                mapView.apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(15.0)

                    if (hasLocationPermission) {
                        myLocationOverlay.enableMyLocation()
                        myLocationOverlay.runOnFirstFix {
                            post {
                                val userLocation = myLocationOverlay.myLocation
                                if (userLocation != null) {
                                    controller.animateTo(userLocation)
                                    controller.setZoom(16.0)
                                }
                            }
                        }
                        if (!overlays.contains(myLocationOverlay)) {
                            overlays.add(myLocationOverlay)
                        }
                    }
                }
            },
            update = { view ->
                view.overlays.removeAll { it is Marker }

                for (canteen in canteens) {
                    val node = GeoPoint(canteen.latitude, canteen.longitude)
                    val marker = Marker(view)
                    marker.position = node
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    marker.title = "${canteen.name}\n${canteen.address}"
                    view.overlays.add(marker)
                }

                if (!hasLocationPermission && canteens.isNotEmpty() && view.mapCenter.latitude == 0.0 && view.mapCenter.longitude == 0.0) {
                    val firstCanteen = GeoPoint(canteens[0].latitude, canteens[0].longitude)
                    view.controller.setCenter(firstCanteen)
                }
                view.invalidate()
            },
            modifier = Modifier.fillMaxWidth().height(250.dp)
        )
    }
}