package com.example.appxml.fragments

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.appxml.base.BaseSystemBarsFragment
import com.example.appxml.databinding.FragmentDynamicRoundedCornersBinding
import com.example.design.ui.xml.extensions.state
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DynamicRoundedCornersFragment :
    BaseSystemBarsFragment<FragmentDynamicRoundedCornersBinding>(FragmentDynamicRoundedCornersBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpListeners()
        initViews()
    }

    private fun setUpListeners() = with(binding) {
        loremIpsum1Tv.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Corner radius = ${resourceProvider.cornerRadius?.toInt()}\nText size = ${resourceProvider.textSize?.toInt()}",
                Toast.LENGTH_SHORT
            ).show()
        }

        lifecycleScope.launch {
            cornerSlider.state(resourceProvider.cornerRadius).collectLatest { event ->
                resourceProvider.cornerRadius = event.value
                if (event.fromUser) {
                    vibrate()
                }
            }
        }

        lifecycleScope.launch {
            val oni = resourceProvider.textSize.takeIf { it != null && it >= 13f } ?: 16f
            textSizeSlider.state(oni).collectLatest { event ->
                resourceProvider.textSize = event.value
                if (event.fromUser) {
                    vibrate()
                }
            }
        }
    }

    private fun initViews() = with(binding) {
        cornerSlider.apply {
            valueFrom = 0f
            valueTo = 16f
            stepSize = 1f
        }

        textSizeSlider.apply {
            valueFrom = 13f
            valueTo = 20f
            stepSize = 1f
        }
    }


    private fun vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                requireContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vib = vibratorManager.defaultVibrator
            vib.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            val vib = requireContext().getSystemService(VIBRATOR_SERVICE) as Vibrator
            @Suppress("DEPRECATION")
            vib.vibrate(10)
        }
    }

    companion object {
        const val TAG = "DynamicRoundedCornersFragment"
    }

}