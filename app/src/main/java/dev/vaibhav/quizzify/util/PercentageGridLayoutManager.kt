package dev.vaibhav.quizzify.util

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PercentageGridLayoutManager(
    context: Context,
    span: Int,
    private val percentage: Float
) :
    GridLayoutManager(context, span) {
    override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
        val size = (width * percentage).toInt()
        lp?.let {
            it.width = size
            it.height = size
        }
        return true
    }
}