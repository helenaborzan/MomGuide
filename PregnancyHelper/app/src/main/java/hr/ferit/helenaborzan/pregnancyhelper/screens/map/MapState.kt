package hr.ferit.helenaborzan.pregnancyhelper.screens.map

import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire.Hospital

data class MapState(
    val properties: MapProperties = MapProperties(isMyLocationEnabled = false),
    val uiSettings: MapUiSettings = MapUiSettings(zoomControlsEnabled = false, zoomGesturesEnabled = true, myLocationButtonEnabled = true),
    val nearbyHospitals: List<Hospital> = emptyList(),
    val cameraPositionState: CameraPositionState = CameraPositionState()
)
