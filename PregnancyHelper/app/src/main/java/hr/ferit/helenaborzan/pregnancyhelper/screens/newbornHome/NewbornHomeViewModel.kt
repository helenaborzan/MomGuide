package hr.ferit.helenaborzan.pregnancyhelper.screens.newbornHome

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.type.DateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.getDate
import hr.ferit.helenaborzan.pregnancyhelper.model.data.breastfeeding.BottleInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.data.breastfeeding.BreastfeedingInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.data.growthAndDevelopment.GrowthAndDevelopmentResult
import hr.ferit.helenaborzan.pregnancyhelper.model.data.newborn.NewbornInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.data.common.Point
import hr.ferit.helenaborzan.pregnancyhelper.model.service.AccountService
import hr.ferit.helenaborzan.pregnancyhelper.repository.NewbornInfoRepository
import hr.ferit.helenaborzan.pregnancyhelper.screens.breastfeeding.BreastfeedingInfoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@HiltViewModel
class NewbornHomeViewModel @Inject constructor(
    private val newbornInfoRepository: NewbornInfoRepository,
    private val accountService: AccountService
) : ViewModel(){

    private val _newbornInfo = MutableStateFlow<List<NewbornInfo>>(emptyList())
    val newbornInfo: StateFlow<List<NewbornInfo>> = _newbornInfo.asStateFlow()

    var uiState = mutableStateOf(NewbornHomeUiState())
        private set

    @RequiresApi(Build.VERSION_CODES.O)
    var breastfeedingInfoUiState = mutableStateOf(BreastfeedingInfoUiState())
        private set

    private val _showDialog = mutableStateOf(false)
    val showDialog : State<Boolean> = _showDialog

    private val _selectedChartType = MutableStateFlow("lengthForAge")
    val selectedChartType: StateFlow<String> = _selectedChartType

    fun selectChartType(chartType: String) {
        _selectedChartType.value = chartType
    }

    private val _points = MutableStateFlow<List<Point>>(emptyList())
    val points: StateFlow<List<Point>> = _points

    fun updatePoints(growthAndDevelopmentResults: List<GrowthAndDevelopmentResult>, selectedChartType: String) {
        viewModelScope.launch {
            val newPoints = growthAndDevelopmentResults.mapNotNull {
                val info = it.growthAndDevelopmentInfo
                when (selectedChartType) {
                    "lengthForAge" -> {
                        info.age.toIntOrNull()?.let { age ->
                            info.length.toIntOrNull()?.let { length ->
                                Point(age, length)
                            }
                        }
                    }
                    "weightForAge" -> {
                        info.age.toIntOrNull()?.let { age ->
                            info.weight.toIntOrNull()?.let { weight ->
                                Point(age, weight)
                            }
                        }
                    }
                    "weightForLength" -> {
                        info.length.toIntOrNull()?.let { length ->
                            info.weight.toIntOrNull()?.let { weight ->
                                Point(length, weight)
                            }
                        }
                    }
                    "headCircumferenceForAge" -> {
                        info.age.toIntOrNull()?.let { age ->
                            info.headCircumference.toIntOrNull()?.let { headCircumference ->
                                Point(age, headCircumference)
                            }
                        }
                    }
                    else -> null
                }
            }
            _points.value = newPoints
        }
    }

    fun getUsersNewbornInfo() {
        viewModelScope.launch {
            newbornInfoRepository.getUserInfo()
                .catch { exception ->
                    Log.e("NewbornHomeViewModel", "Error fetching Newborn Info", exception)
                }
                .collect { newbornInfo ->
                    _newbornInfo.value = newbornInfo
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(date : DateTime){
        val localDateTime = LocalDateTime.parse("2018-12-14T09:55:00")
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        val output = formatter.format(localDateTime)
    }

    fun onSignOutClick(){
        viewModelScope.launch {
            try {
                accountService.signOut()
                uiState.value = uiState.value.copy(isSignedOut = true)
            } catch (e : Exception){
                uiState.value = uiState.value.copy(errorMessage = R.string.signOutError, isSignedOut = false)
            }
        }
    }
    fun clearError() {
        uiState.value = uiState.value.copy(errorMessage = null)
    }

    fun onDeleteResultClick(){
        _showDialog.value = !_showDialog.value
    }

    fun deletePercentileResult(
        growthAndDevelopmentResultIndex: Int,
        growthAndDevelopmentResults: List<GrowthAndDevelopmentResult>
    ){
        viewModelScope.launch {
            newbornInfoRepository.deletePercentileResult(growthAndDevelopmentResultIndex)
        }
    }
    fun onDeleteResultDialogDismiss(){
        _showDialog.value = false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateSelectedDate(newDate : LocalDate){
        breastfeedingInfoUiState.value = breastfeedingInfoUiState.value.copy(selectedDate = newDate)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onPreviousDayClick() {
        val currentDate = breastfeedingInfoUiState.value.selectedDate
        breastfeedingInfoUiState.value = breastfeedingInfoUiState.value.copy(
                selectedDate = currentDate?.minusDays(1) ?: LocalDate.now().minusDays(1)
            )
        }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onNextDayClick() {
        val currentDate = breastfeedingInfoUiState.value.selectedDate
        breastfeedingInfoUiState.value = breastfeedingInfoUiState.value.copy(
                selectedDate = currentDate?.plusDays(1) ?: LocalDate.now().plusDays(1)
            )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getBreastfeedingInfoByDate(breastfeedingInfo: List<BreastfeedingInfo>) : MutableList<BreastfeedingInfo>{
        val selectedDate = breastfeedingInfoUiState.value.selectedDate
        val selectedYear = getDate(selectedDate).get("year")
        val selectedMonth = getDate(selectedDate).get("month")
        val selectedDay = getDate(selectedDate).get("day")
        var breastfeedingInfoByDate = mutableListOf<BreastfeedingInfo>()

        for (row in breastfeedingInfo){
            if (selectedYear == getDate(row.startTime).get("year")
                && selectedMonth == getDate(row.startTime).get("month")
                && selectedDay == getDate(row.startTime).get("day")
            ){
                breastfeedingInfoByDate.add(row)
            }
        }
        return breastfeedingInfoByDate
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTodaysBreastfeedingInfo(breastfeedingInfo: List<BreastfeedingInfo>) : MutableList<BreastfeedingInfo>{
        val date = LocalDate.now()
        val selectedYear = getDate(date).get("year")
        val selectedMonth = getDate(date).get("month")
        val selectedDay = getDate(date).get("day")
        var todaysBreastfeedingInfo = mutableListOf<BreastfeedingInfo>()

        for (row in breastfeedingInfo){
            if (selectedYear == getDate(row.startTime).get("year")
                && selectedMonth == getDate(row.startTime).get("month")
                && selectedDay == getDate(row.startTime).get("day")
            ){
                todaysBreastfeedingInfo.add(row)
            }
        }
        return todaysBreastfeedingInfo
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getBottleInfoByDate(bottleInfo: List<BottleInfo>) : MutableList<BottleInfo>{
        val selectedDate = breastfeedingInfoUiState.value.selectedDate
        val selectedYear = getDate(selectedDate).get("year")
        val selectedMonth = getDate(selectedDate).get("month")
        val selectedDay = getDate(selectedDate).get("day")
        var todaysBottleInfo = mutableListOf<BottleInfo>()

        for (row in bottleInfo){
            if (selectedYear == getDate(row.time).get("year")
                && selectedMonth == getDate(row.time).get("month")
                && selectedDay == getDate(row.time).get("day")
            ){
                todaysBottleInfo.add(row)
            }
        }
        return todaysBottleInfo
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTodaysBottleInfo(bottleInfo: List<BottleInfo>) : MutableList<BottleInfo>{
        val date = LocalDate.now()
        val selectedYear = getDate(date).get("year")
        val selectedMonth = getDate(date).get("month")
        val selectedDay = getDate(date).get("day")
        var bottleInfoByDate = mutableListOf<BottleInfo>()

        for (row in bottleInfo){
            if (selectedYear == getDate(row.time).get("year")
                && selectedMonth == getDate(row.time).get("month")
                && selectedDay == getDate(row.time).get("day")
            ){
                bottleInfoByDate.add(row)
            }
        }
        return bottleInfoByDate
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onFeedingTypeChange(newValue : String){
        breastfeedingInfoUiState.value = breastfeedingInfoUiState.value.copy(
            feedingType = newValue
        )
    }

    fun onFeedingTypeChangeHome(newValue: String){
        uiState.value = uiState.value.copy(feedingType = newValue)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isSelectedDayToday(selectedDate : LocalDate) : Boolean{
        val today = LocalDate.now()
        val selectedYear = getDate(selectedDate).get("year")
        val selectedMonth = getDate(selectedDate).get("month")
        val selectedDay = getDate(selectedDate).get("day")

        return ((selectedYear == getDate(today).get("year")) &&
                (selectedMonth == getDate(today).get("month")) &&
                (selectedDay == getDate(today).get("day")))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getFeedingDuration(breastfeedingInfo: List<BreastfeedingInfo>) : List<Int>{
        val feedingDurations = mutableListOf<Int>()
        for (row in breastfeedingInfo){
            feedingDurations.add(row.getMinutesDifference() ?: 0)
        }
        return feedingDurations
    }
}
