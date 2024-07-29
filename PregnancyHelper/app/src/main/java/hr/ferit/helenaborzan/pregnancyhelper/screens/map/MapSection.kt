package hr.ferit.helenaborzan.pregnancyhelper.screens.map

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.tasks.await


@RequiresApi(34)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapSection(viewModel: MapViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val state by viewModel.state.collectAsState()
        

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            try {
                val location = fusedLocationClient.lastLocation.await()
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    state.cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
                    viewModel.fetchNearbyHospitals(latLng)
                    Log.d("MapSection", "Fetching nearby hospitals: ${state.nearbyHospitals}")
                    viewModel.zoomToShowAllMarkers(state.nearbyHospitals)
                }
            } catch (e: Exception) {
                Log.e("MapSection", "Error fetching location or hospitals", e)
            }
        } else {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = state.properties,
            uiSettings = state.uiSettings.copy(zoomControlsEnabled = true, mapToolbarEnabled = true),
            cameraPositionState = state.cameraPositionState,
            onMyLocationButtonClick = {
                if (hasLocationPermission) {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                        location?.let {
                            val latLng = LatLng(it.latitude, it.longitude)
                            state.cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
                        }
                    }
                } else {
                    ActivityCompat.requestPermissions(
                        context as Activity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        1
                    )
                }
                true // true indicates that the click event is handled
            }
        ) {
            state.nearbyHospitals.forEach { hospital ->
                Marker(
                    state = rememberMarkerState(position = hospital.position),
                    title = hospital.name,
                    snippet = "Hospital"
                )
            }
        }
    }

}


