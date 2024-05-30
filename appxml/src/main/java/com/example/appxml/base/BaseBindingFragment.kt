package com.example.appxml.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.design.provider.api.ResourceProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal typealias BindingInflater<VB> = (LayoutInflater, ViewGroup?, Boolean) -> VB

open class BaseBindingFragment<VB : ViewBinding>(private val bindingInflater: BindingInflater<VB>) : Fragment(), KoinComponent {

    protected val resourceProvider: ResourceProvider by inject()

    private var _binding: ViewBinding? = null

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = requireNotNull(_binding) as VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = bindingInflater.invoke(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}