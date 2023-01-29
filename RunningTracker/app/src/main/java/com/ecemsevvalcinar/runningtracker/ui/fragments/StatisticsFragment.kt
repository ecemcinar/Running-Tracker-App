package com.ecemsevvalcinar.runningtracker.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ecemsevvalcinar.runningtracker.other.CustomMarkerView
import com.ecemsevvalcinar.runningtracker.other.TrackingUtility
import com.example.runningtracker.R
import com.ecemsevvalcinar.runningtracker.ui.viewmodels.StatisticsViewModel
import com.example.runningtracker.databinding.FragmentStatisticsBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class StatisticsFragment: Fragment(R.layout.fragment_statistics) {

    private lateinit var binding: FragmentStatisticsBinding
    private val viewModel: StatisticsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_statistics,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        setUpBarChart()
    }

   private fun subscribeToObservers() {
       viewModel.totalTimeRun.observe(viewLifecycleOwner) {
           it?.let {
               val totalTimeRun = TrackingUtility.getFormattedStopWatchTime(it)
               binding.tvTotalTime.text = totalTimeRun
           }
       }

       viewModel.totalDistance.observe(viewLifecycleOwner) {
           it?.let {
               val km = it / 1000f
               val totalDist = (km * 10f).roundToInt() / 10f
               val totalDistStr = "${totalDist}km"
               binding.tvTotalDistance.text = totalDistStr
           }
       }

       viewModel.totalAvgSpeed.observe(viewLifecycleOwner) {
           it?.let {
               val avgSpeed = (it * 10f).roundToInt() / 10f
               val avgSpeedStr = "${avgSpeed}km/h"
               binding.tvAverageSpeed.text = avgSpeedStr
           }
       }

       viewModel.totalCaloriesBurned.observe(viewLifecycleOwner) {
           it?.let {
               val caloriesBurned = "${it}kcal"
               binding.tvTotalCalories.text = caloriesBurned
           }
       }

       viewModel.runsSortedByDate.observe(viewLifecycleOwner) {
           it?.let {
               val allAvgSpeeds = it.indices.map { i -> BarEntry(i.toFloat(), it[i].avgSpeedInKMH) }
               val barDataSet = BarDataSet(allAvgSpeeds, "Avg Speed Over Time").apply {
                   valueTextColor = Color.WHITE
                   color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
               }

               binding.barChart.apply {
                   data = BarData(barDataSet)
                   marker = CustomMarkerView(it.reversed(), requireContext(), R.layout.marker_view)
                   invalidate()
               }
           }
       }
   }

    private fun setUpBarChart() {
        binding.barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }

        binding.barChart.axisLeft.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }

        binding.barChart.axisRight.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }

        binding.barChart.apply {
            description.text = "Avg Speed Over Time"
            legend.isEnabled = false
        }
    }

}