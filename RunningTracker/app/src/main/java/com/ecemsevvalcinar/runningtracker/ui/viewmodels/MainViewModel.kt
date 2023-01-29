package com.ecemsevvalcinar.runningtracker.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecemsevvalcinar.runningtracker.db.Run
import com.ecemsevvalcinar.runningtracker.other.SortType
import com.ecemsevvalcinar.runningtracker.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository: MainRepository): ViewModel() {

    private val runsSortedByDate = mainRepository.getAllRunsSortedByDate()
    private val runsSortedByDistance = mainRepository.getAllRunsSortedByDistance()
    private val runsSortedByAvgSpeed = mainRepository.getAllRunsSortedByAvgSpeed()
    private val runsSortedByCaloriesBurned = mainRepository.getAllRunsSortedByCaloriesBurned()
    private val runsSortedByTimeInMillis = mainRepository.getAllRunsSortedByTimeInMillis()

    // default sort type
    var sortType = SortType.DATE

    val selectedRunType = MediatorLiveData<List<Run>>()

    init {
        selectedRunType.addSource(runsSortedByDate) { result ->
            if (sortType == SortType.DATE) {
                result?.let { selectedRunType.value = result }
            }
        }
        selectedRunType.addSource(runsSortedByDistance) { result ->
            if (sortType == SortType.DISTANCE) {
                result?.let { selectedRunType.value = result }
            }
        }
        selectedRunType.addSource(runsSortedByAvgSpeed) { result ->
            if (sortType == SortType.AVG_SPEED) {
                result?.let { selectedRunType.value = result }
            }
        }
        selectedRunType.addSource(runsSortedByCaloriesBurned) { result ->
            if (sortType == SortType.CALORIES_BURNED) {
                result?.let { selectedRunType.value = result }
            }
        }
        selectedRunType.addSource(runsSortedByTimeInMillis) { result ->
            if (sortType == SortType.RUNNING_TIME) {
                result?.let { selectedRunType.value = result }
            }
        }
    }

    fun insertRun(run: Run) = viewModelScope.launch {
        mainRepository.insertRun(run)
    }

    fun sortRuns(sortType: SortType) = when (sortType) {
        SortType.DATE -> runsSortedByDate.value?.let { selectedRunType.value = it }
        SortType.DISTANCE -> runsSortedByDistance.value?.let { selectedRunType.value = it }
        SortType.CALORIES_BURNED -> runsSortedByCaloriesBurned.value?.let { selectedRunType.value = it }
        SortType.RUNNING_TIME -> runsSortedByTimeInMillis.value?.let { selectedRunType.value = it }
        SortType.AVG_SPEED -> runsSortedByAvgSpeed.value?.let { selectedRunType.value = it }
    }.also {
        this.sortType = sortType
    }

}