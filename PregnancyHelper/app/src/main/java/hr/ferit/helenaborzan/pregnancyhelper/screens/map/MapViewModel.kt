package hr.ferit.helenaborzan.pregnancyhelper.screens.map

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.CameraPositionState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire.Hospital
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val placesClient: PlacesClient
): ViewModel() {
    private val _state = MutableStateFlow(MapState())
    val state: StateFlow<MapState> = _state.asStateFlow()

    fun updateLocationPermission(hasPermission: Boolean) {
        _state.update {
            it.copy(
                properties = it.properties.copy(isMyLocationEnabled = hasPermission),
                uiSettings = it.uiSettings.copy(myLocationButtonEnabled = hasPermission)
            )
        }
    }

    fun updateHospitals(hospitals: List<Hospital>) {
        _state.update { it.copy(nearbyHospitals = hospitals) }
    }

    @SuppressLint("MissingPermission")
    suspend fun fetchNearbyHospitals(context: Context,currentLocation: LatLng) {
        withContext(Dispatchers.IO) {
            val queries = listOf("psiholog", "psihijatar", "psychologist", "psychiatrist")
            val results = mutableListOf<Hospital>()
            val radius = 70000
            val latLngBounds = RectangularBounds.newInstance(
                LatLng(currentLocation.latitude - 0.1, currentLocation.longitude - 0.1),
                LatLng(currentLocation.latitude + 0.1, currentLocation.longitude + 0.1)
            )
            try {
                for (query in queries) {
                    val request = FindAutocompletePredictionsRequest.builder()
                        .setLocationBias(latLngBounds)
                        .setTypesFilter(listOf(PlaceTypes.HOSPITAL, PlaceTypes.HEALTH, PlaceTypes.DOCTOR))
                        .setQuery(query)
                        .build()


                    val response = placesClient.findAutocompletePredictions(request).await()
                    val predictions = response.autocompletePredictions

                    // Fetch detailed information for each prediction
                    predictions.forEach { prediction ->
                        val placeResult = placesClient.fetchPlace(
                            FetchPlaceRequest.newInstance(
                                prediction.placeId,
                                listOf(Place.Field.LAT_LNG, Place.Field.NAME)
                            )
                        ).await()

                        placeResult.place.latLng?.let { latLng ->
                            if (isWithinRadius(currentLocation, latLng, radius)) {
                                results.add(Hospital(placeResult.place.name ?: "Unknown", latLng))
                            }
                        }
                    }
                }
                Log.d("MapViewModel", "Fetched nearby hospitals: $results")
                updateHospitals(results)
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error fetching hospitals", e)
            }
        }
    }

    fun zoomToShowAllMarkers(markers: List<Hospital>) {
        if (markers.isEmpty()) return

        val builder = LatLngBounds.Builder()
        markers.forEach { builder.include(it.position) }
        val bounds = builder.build()

        _state.update { currentState ->
            currentState.copy(
                cameraPositionState = CameraPositionState(
                    position = CameraPosition.fromLatLngZoom(bounds.center, 10f)
                )
            )
        }
    }
    private fun isWithinRadius(center: LatLng, point: LatLng, radius: Int): Boolean {
        val results = FloatArray(1)
        Location.distanceBetween(
            center.latitude, center.longitude,
            point.latitude, point.longitude,
            results
        )
        return results[0] <= radius
    }
}