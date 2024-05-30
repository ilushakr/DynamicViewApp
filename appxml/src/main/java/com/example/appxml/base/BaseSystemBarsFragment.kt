package com.example.appxml.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.appxml.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

open class BaseSystemBarsFragment<VB : ViewBinding>(bindingInflater: BindingInflater<VB>) :
    BaseBindingFragment<VB>(bindingInflater) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val contentView = super.onCreateView(inflater, container, savedInstanceState)
        return createRoot(contentView)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            resourceProvider.currentPaletteState.collectLatest { palette ->
                palette?.systemBarsColor?.let {
                    view.findViewById<View>(R.id.status_bar_view).setBackgroundColor(it)
                    view.findViewById<View>(R.id.navigation_bar_view).setBackgroundColor(it)
//                    setBarAppearance(it)
                }
            }
        }

    }

    private fun createRoot(content: View): View {
        return LinearLayout(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            orientation = LinearLayout.VERTICAL

            addView(
                View(requireContext()).apply {
                    id = R.id.status_bar_view
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        resourceProvider.barsHeightsHolder.statusBarHeight
                    )
                }
            )

            addView(content.apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0
                ).apply { weight = 1f }
            })

            addView(
                View(requireContext()).apply {
                    id = R.id.navigation_bar_view
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        resourceProvider.barsHeightsHolder.navigationBarHeight
                    )
                }
            )
        }
    }

}