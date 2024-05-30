package com.example.appxml.fragments

import android.os.Bundle
import android.view.View
import com.example.appxml.base.BaseSystemBarsFragment
import com.example.appxml.databinding.FragmentNightThemeBinding
import com.example.appxml.listeners.ThemeModeTogglerListener
import org.koin.core.component.inject

class NightThemeFragment: BaseSystemBarsFragment<FragmentNightThemeBinding>(FragmentNightThemeBinding::inflate) {

    private val themeModeTogglerListener by inject<ThemeModeTogglerListener>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){
            loremIpsum1Tv.setOnClickListener {
                themeModeTogglerListener.toggleThemeMode(loremIpsum1Tv)
            }
            loremIpsum2Tv.setOnClickListener {
                themeModeTogglerListener.toggleThemeMode(loremIpsum2Tv)
            }
            myview.setOnClickListener {
                themeModeTogglerListener.toggleThemeMode(myview)
            }
        }
    }

    companion object{
        const val TAG = "NightThemeFragment"
    }
}