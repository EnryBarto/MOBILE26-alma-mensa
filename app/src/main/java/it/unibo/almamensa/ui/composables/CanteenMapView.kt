package it.unibo.almamensa.ui.composables

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import it.unibo.almamensa.R
import it.unibo.almamensa.data.model.Canteen
import it.unibo.almamensa.utils.PermissionPermanentlyDeniedSnackbar
import it.unibo.almamensa.utils.PermissionStatus
import it.unibo.almamensa.utils.isGpsEnabled
import it.unibo.almamensa.utils.openAppSettings
import it.unibo.almamensa.utils.openLocationSettings
import it.unibo.almamensa.utils.rememberMultiplePermissions
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Composable
fun CanteensMapView(
    canteens: List<Canteen>,
    modifier: Modifier = Modifier,
    onCanteenClick: (Canteen) -> Unit = {},
    isDarkTheme: Boolean = false
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember { MapView(context) }
    val myLocationOverlay = remember {
        MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
    }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    var showLocationDisabledAlert by remember { mutableStateOf(false) }
    var showPermissionDeniedAlert by remember { mutableStateOf(false) }
    var showPermissionPermanentlyDeniedSnackbar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val locationPermission = rememberMultiplePermissions(
        listOf(Manifest.permission.ACCESS_FINE_LOCATION)
    ) { statuses ->
        hasLocationPermission = statuses.values.any { it == PermissionStatus.Granted }
        when {
            hasLocationPermission && !isGpsEnabled(context) -> showLocationDisabledAlert = true
            hasLocationPermission -> {
                myLocationOverlay.enableMyLocation()
                myLocationOverlay.runOnFirstFix {
                    mapView.post {
                        val userLocation = myLocationOverlay.myLocation
                        if (userLocation != null) {
                            mapView.controller.animateTo(userLocation)
                            mapView.controller.setZoom(16.0)
                        }
                    }
                }
            }
            statuses.values.all { it == PermissionStatus.PermanentlyDenied } ->
                showPermissionPermanentlyDeniedSnackbar = true
            else -> {}
        }
    }

    if (showLocationDisabledAlert) {
        AlertDialog(
            onDismissRequest = { showLocationDisabledAlert = false },
            title = { Text("GPS disabilitato") },
            text = { Text("Abilita il GPS per vedere la tua posizione sulla mappa.") },
            confirmButton = {
                TextButton(onClick = {
                    showLocationDisabledAlert = false
                    openLocationSettings(context)
                }) { Text("Impostazioni") }
            },
            dismissButton = {
                TextButton(onClick = { showLocationDisabledAlert = false }) { Text("Annulla") }
            }
        )
    }

    if (showPermissionDeniedAlert) {
        AlertDialog(
            onDismissRequest = { showPermissionDeniedAlert = false },
            title = { Text("Permesso posizione") },
            text = { Text("Concedi il permesso di posizione per vedere la tua posizione sulla mappa.") },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionDeniedAlert = false
                    locationPermission.launchPermissionRequest()
                }) { Text("Concedi") }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDeniedAlert = false }) { Text("Annulla") }
            }
        )
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            locationPermission.launchPermissionRequest()
        } else if (!isGpsEnabled(context)) {
            showLocationDisabledAlert = true
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    mapView.onResume()
                    if (hasLocationPermission) myLocationOverlay.enableMyLocation()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    myLocationOverlay.disableMyLocation()
                    mapView.onPause()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(
        modifier = modifier.clip(RoundedCornerShape(12.dp))
    ) {
        val currentPrimaryColor = MaterialTheme.colorScheme.primary.toArgb()

        AndroidView(
                factory = { ctx ->
                    mapView.apply {
                        val tileSource = if (isDarkTheme) {
                            XYTileSource(
                                "CartoDB Dark", 0, 19, 256, ".png",
                                arrayOf("https://a.basemaps.cartocdn.com/dark_all/")
                            )
                        } else {
                            TileSourceFactory.MAPNIK
                        }
                        setTileSource(tileSource)
                        setMultiTouchControls(true)
                        controller.setZoom(15.0)
                        controller.setCenter(GeoPoint(44.1391, 12.2435)) // Default zoom to Cesena

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
                        marker.title = canteen.name
                        marker.snippet = canteen.address

                        marker.icon = createMarkerDrawable(view.context, currentPrimaryColor)

                        marker.setOnMarkerClickListener { m, _ ->
                            if (m.isInfoWindowShown) {
                                onCanteenClick(canteen)
                            } else {
                                m.showInfoWindow()
                            }
                            true
                        }
                        view.overlays.add(marker)
                    }

                    if (!hasLocationPermission && view.mapCenter.latitude == 0.0 && view.mapCenter.longitude == 0.0) {
                        canteens.firstOrNull()?.let { first ->
                            view.controller.setCenter(GeoPoint(first.latitude, first.longitude))
                        }
                    }
                    view.invalidate()
                },
                modifier = Modifier.fillMaxSize()
            )

        IconButton(
            onClick = {
                when {
                    !hasLocationPermission -> showPermissionDeniedAlert = true
                    !isGpsEnabled(context) -> showLocationDisabledAlert = true
                    else -> {
                        val userLocation = myLocationOverlay.myLocation
                        if (userLocation != null) {
                            mapView.controller.animateTo(userLocation)
                            mapView.controller.setZoom(16.0)
                        }
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Centra sulla mia posizione",
                tint = if (hasLocationPermission && isGpsEnabled(context))
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    PermissionPermanentlyDeniedSnackbar(
        snackbarHostState,
        show = showPermissionPermanentlyDeniedSnackbar,
        onAction = { openAppSettings(context) },
        onHide = { showPermissionPermanentlyDeniedSnackbar = false }
    )
}

private fun createMarkerDrawable(context: android.content.Context, color: Int): BitmapDrawable {
    val original = ContextCompat.getDrawable(context, R.drawable.ic_location_pin)?.mutate()
    DrawableCompat.setTint(original!!, color)
    val width = (original.intrinsicWidth * 1.75).toInt()
    val height = (original.intrinsicHeight * 1.75).toInt()
    val bitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    original.setBounds(0, 0, width, height)
    original.draw(canvas)
    return bitmap.toDrawable(context.resources)
}