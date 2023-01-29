package com.ecemsevvalcinar.runningtracker.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ecemsevvalcinar.runningtracker.adapter.RunAdapter
import com.example.runningtracker.R
import com.example.runningtracker.databinding.FragmentRunBinding
import com.ecemsevvalcinar.runningtracker.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.ecemsevvalcinar.runningtracker.other.SortType
import com.ecemsevvalcinar.runningtracker.other.TrackingUtility
import com.ecemsevvalcinar.runningtracker.ui.viewmodels.MainViewModel
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RunFragment: Fragment(R.layout.fragment_run), EasyPermissions.PermissionCallbacks {

    private lateinit var binding: FragmentRunBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var runAdapter: RunAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_run, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        requestPermissions()

        when (viewModel.sortType) {
            SortType.DATE -> binding.spFilter.setSelection(0)
            SortType.RUNNING_TIME -> binding.spFilter.setSelection(1)
            SortType.DISTANCE -> binding.spFilter.setSelection(2)
            SortType.AVG_SPEED -> binding.spFilter.setSelection(3)
            SortType.CALORIES_BURNED -> binding.spFilter.setSelection(4)
        }

        binding.spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                when (pos) {
                    0 -> viewModel.sortRuns(SortType.DATE)
                    1 -> viewModel.sortRuns(SortType.RUNNING_TIME)
                    2 -> viewModel.sortRuns(SortType.DISTANCE)
                    3 -> viewModel.sortRuns(SortType.AVG_SPEED)
                    4 -> viewModel.sortRuns(SortType.CALORIES_BURNED)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}

        }

        viewModel.selectedRunType.observe(viewLifecycleOwner) {
            runAdapter.submitList(it)
        }


        /*
        Flow version TO DO
        lifecycleScope.launch {
            viewModel.runsSortedByDate.collect {
                runAdapter.submitList(it)
            }
        }
         */


        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }
    }

    private fun setUpRecyclerView() = binding.rvRuns.apply {
        runAdapter = RunAdapter()
        adapter = runAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun requestPermissions() {
        // println(TrackingUtility.hasLocationPermissions(requireContext()))
        if(TrackingUtility.hasLocationPermissions(requireContext())) {
            return
        }

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            requestEasyPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            requestEasyPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            requestEasyPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            requestEasyPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            requestEasyPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }

    private fun requestEasyPermission(permission: String) {
        EasyPermissions.requestPermissions(
            this,
            getString(R.string.run_fragment_text_accept_permissions),
            REQUEST_CODE_LOCATION_PERMISSION,
            permission
        )
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(requireContext()).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Toast.makeText(
            requireContext(),
            "Permission Granted!",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}