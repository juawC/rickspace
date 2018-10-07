package com.app.juawcevada.rickspace.ui

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView

class VerticalSpaceItemDecoration(
        private val context: Context,
        @DimenRes private val height: Int,
        private val includeTop: Boolean = false
) : RecyclerView.ItemDecoration() {

    private val heightPx: Int by lazy {
        context.resources.getDimension(height).toInt()
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        if (parent.getChildAdapterPosition(view) == 0 && !includeTop) {
            return
        }

        outRect.top = heightPx
    }
}