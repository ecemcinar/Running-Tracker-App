package com.ecemsevvalcinar.runningtracker.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.ecemsevvalcinar.runningtracker.other.Constants.KEY_NAME
import com.ecemsevvalcinar.runningtracker.other.Constants.KEY_WEIGHT
import com.example.runningtracker.R
import com.example.runningtracker.databinding.FragmentSettingsBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment: Fragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding

    @Inject
    lateinit var sharedPref: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadFieldsFromSharedPref()

        binding.btnApplyChanges.setOnClickListener {
            val success = applyChangesToSharedPref()

            if (success) {
                Snackbar.make(view, "Saved changes!", Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(view, "Please fill out all the fields!", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        return binding.root
    }

    private fun loadFieldsFromSharedPref() {
        val name = sharedPref.getString(KEY_NAME, "")
        val weight = sharedPref.getFloat(KEY_WEIGHT, 60f)
        binding.etName.setText(name)
        binding.etWeight.setText(weight.toString())
    }

            private fun applyChangesToSharedPref(): Boolean {
        val nameText = binding.etName.text.toString()
        val weightText = binding.etWeight.text.toString()

        if (nameText.isEmpty() || weightText.isEmpty()) {
            return false
        }

        sharedPref.edit()
            .putString(KEY_NAME, nameText)
            .putFloat(KEY_WEIGHT, weightText.toFloat())
            .apply()

        val toolbarText = "Let's go $nameText!"
        requireActivity().findViewById<MaterialTextView>(R.id.tvToolbarTitle).text = toolbarText
        return true
    }
}