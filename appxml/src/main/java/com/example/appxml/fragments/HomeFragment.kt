package com.example.appxml.fragments

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appxml.R
import com.example.appxml.base.BaseSystemBarsFragment
import com.example.appxml.databinding.FragmentHomeBinding
import com.example.design.ui.xml.extensions.dp
import com.example.design.ui.xml.widgets.base.BaseTextView
import com.example.design.ui.xml.widgets.base.SurfaceTextView

class HomeFragment : BaseSystemBarsFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.homeRv) {
            layoutManager = LinearLayoutManager(requireContext())

            adapter = MyAdapter(Buttons.values().toList())
        }
    }

    inner class MyAdapter(private val data: List<Buttons>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(
                SurfaceTextView(parent.context).apply {
                    gravity = Gravity.CENTER
                    setPadding(0, 8.dp, 0, 8.dp)

                    val layoutParams = RecyclerView.LayoutParams(
                        RecyclerView.LayoutParams.MATCH_PARENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT
                    )
                    layoutParams.setMargins(0, 16.dp, 0, 16.dp)
                    this.layoutParams = layoutParams
                }
            ) {}
        }

        override fun getItemCount() = data.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder.itemView as BaseTextView).apply {
                text = data[position].name

                setOnClickListener {
                    try {
                        val name = data[position].fragmentName
                        val fr: Fragment = Class.forName("com.example.appxml.fragments.$name").newInstance() as Fragment

                        parentFragmentManager.commit {
//                            setCustomAnimations(
//                                android.R.anim.slide_in_left,
//                                android.R.anim.slide_out_right
//                            )
                            replace(R.id.container_fcv, fr, name)
                            addToBackStack(name)
                        }
                    } catch (e: ClassNotFoundException) {
                        Log.e("loading level", "level class not found", e)
                    }
                }
            }
        }
    }


    enum class Buttons(val fragmentName: String) {
        CheckScreen(CheckFragment.TAG),
        ColorSettings(ColorSettingsFragment.TAG),
        SizeSettings(DynamicRoundedCornersFragment.TAG),
        NightThemeSettings(NightThemeFragment.TAG)
    }

    companion object {
        const val TAG = "HomeFragment"
    }
}
