package it.unibo.almamensa.data.repositories

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import org.osmdroid.util.GeoPoint

interface LocationRepository {
    suspend fun getCurrentLocation(): GeoPoint?
}

class LocationRepositoryImpl(private val context: Context) : LocationRepository {
    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    override suspend fun getCurrentLocation(): GeoPoint? {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            throw IllegalStateException("GPS disabilitato")
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) return null

        return suspendCancellableCoroutine { cont ->
            fusedClient.lastLocation
                .addOnSuccessListener { location ->
                    cont.resumeWith(Result.success(
                        if (location != null) GeoPoint(location.latitude, location.longitude)
                        else null
                    ))
                }
                .addOnFailureListener { cont.resumeWith(Result.success(null)) }
        }
    }
}