package com.ecemsevvalcinar.runningtracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.ecemsevvalcinar.runningtracker.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(val mainRepository: MainRepository): ViewModel() {


}