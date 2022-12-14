package com.ecemsevvalcinar.runningtrackerapp.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ecemsevvalcinar.runningtrackerapp.R
import com.ecemsevvalcinar.runningtrackerapp.databinding.FragmentRunBinding
import com.ecemsevvalcinar.runningtrackerapp.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.ecemsevvalcinar.runningtrackerapp.other.TrackingUtility
import com.ecemsevvalcinar.runningtrackerapp.ui.viewmodels.MainViewModel
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RunFragment: Fragment(R.layout.fragment_run), EasyPermissions.PermissionCallbacks {

    private lateinit var binding: FragmentRunBinding
    private val viewModel: MainViewModel by viewModels()

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
        requestPermissions()
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }
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