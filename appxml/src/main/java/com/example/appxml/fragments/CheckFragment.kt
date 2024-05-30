package com.example.appxml.fragments

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.appxml.base.BaseSystemBarsFragment
import com.example.appxml.databinding.FragmentCheckBinding
import com.example.design.ui.xml.extensions.dp
import com.example.design.ui.xml.widgets.base.SurfaceConstraintLayout
import kotlin.random.Random

class CheckFragment : BaseSystemBarsFragment<FragmentCheckBinding>(FragmentCheckBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.checkRv) {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = MyAdapter(
                List(30) {
                    Random.nextInt(64.dp, 128.dp)
                }
            )

            addItemDecoration(
                RecyclerViewMargin(
                    8.dp,
                    2
                )
            )
        }
    }

    private inner class MyAdapter(private val data: List<Int>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(
                SurfaceConstraintLayout(parent.context)
            ) {}
        }

        override fun getItemCount() = data.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder.itemView as SurfaceConstraintLayout).apply {
                val layoutParams = RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    data[position]
                )
                this.layoutParams = layoutParams
            }
        }
    }

    private class RecyclerViewMargin(private val margin: Int, private val columns: Int) : ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView, state: RecyclerView.State
        ) {
            val position = parent.getChildLayoutPosition(view)
            //set right margin to all
            outRect.right = margin
            //set bottom margin to all
            outRect.bottom = margin
            //we only add top margin to the first row
            if (position < columns) {
                outRect.top = margin
            }
            //add left margin only to the first column
            if (position % columns == 0) {
                outRect.left = margin
            }
        }
    }

    companion object {
        const val TAG = "CheckFragment"
    }
}
