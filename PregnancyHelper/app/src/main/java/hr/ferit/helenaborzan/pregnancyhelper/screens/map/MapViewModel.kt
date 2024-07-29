package hr.ferit.helenaborzan.pregnancyhelper.screens.map

import android.annotation.SuppressLint
import android.util.Log
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
import hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire.Hospital
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(private val placesClient: PlacesClient): ViewModel() {
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
    suspend fun fetchNearbyHospitals(currentLocation: LatLng) {
        withContext(Dispatchers.IO) {
            val request = FindAutocompletePredictionsRequest.builder()
                .setLocationBias(RectangularBounds.newInstance(
                    LatLng(currentLocation.latitude - 0.1, currentLocation.longitude - 0.1),
                    LatLng(currentLocation.latitude + 0.1, currentLocation.longitude + 0.1)
                ))
                .setTypesFilter(listOf(PlaceTypes.HOSPITAL, PlaceTypes.HEALTH, PlaceTypes.DOCTOR))
                .setQuery("psiholog")
                .build()

            try {
                Log.d("MapViewModel", "Fetching nearby hospitals for location: $currentLocation")
                val response = placesClient.findAutocompletePredictions(request).await()
                val hospitals = response.autocompletePredictions.map { prediction ->
                    val placeResult = placesClient.fetchPlace(
                        FetchPlaceRequest.newInstance(
                            prediction.placeId,
                            listOf(Place.Field.LAT_LNG, Place.Field.NAME)
                        )
                    ).await()

                    placeResult.place.latLng.let { latLng ->
                        Hospital(placeResult.place.name ?: "", latLng)
                    }
                }
                Log.d("MapViewModel", "Fetched nearby hospitals: $hospitals")
                updateHospitals(hospitals)
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

}