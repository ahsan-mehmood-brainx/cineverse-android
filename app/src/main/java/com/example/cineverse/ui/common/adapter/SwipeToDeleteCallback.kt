package com.example.cineverse.ui.common.adapter

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.cineverse.R
import com.google.android.material.R as MaterialR
import com.google.android.material.color.MaterialColors

/**
 * Reveals a themed "delete" background behind a swiped row without removing anything itself —
 * the caller decides whether the swipe should actually delete (e.g. after a confirmation dialog)
 * via [onSwiped], and is responsible for calling [RecyclerView.Adapter.notifyItemChanged] on the
 * swiped position to snap the row back if the user cancels.
 */
class SwipeToDeleteCallback(
    private val recyclerView: RecyclerView,
    private val onSwiped: (position: Int) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val background = ColorDrawable(
        MaterialColors.getColor(recyclerView, MaterialR.attr.colorErrorContainer, Color.RED)
    )
    private val icon = recyclerView.context.getDrawable(R.drawable.ic_delete)?.also { drawable ->
        DrawableCompat.setTint(
            drawable,
            MaterialColors.getColor(recyclerView, MaterialR.attr.colorOnErrorContainer, Color.WHITE)
        )
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.bindingAdapterPosition
        if (position != RecyclerView.NO_POSITION) {
            onSwiped(position)
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val top = itemView.top
        val bottom = itemView.bottom
        val iconMargin = (itemView.height - (icon?.intrinsicHeight ?: 0)) / 2

        when {
            dX > 0 -> {
                background.setBounds(itemView.left, top, itemView.left + dX.toInt(), bottom)
                icon?.setBounds(
                    itemView.left + iconMargin,
                    top + iconMargin,
                    itemView.left + iconMargin + (icon.intrinsicWidth),
                    bottom - iconMargin
                )
            }
            dX < 0 -> {
                background.setBounds(itemView.right + dX.toInt(), top, itemView.right, bottom)
                icon?.setBounds(
                    itemView.right - iconMargin - icon.intrinsicWidth,
                    top + iconMargin,
                    itemView.right - iconMargin,
                    bottom - iconMargin
                )
            }
            else -> background.setBounds(0, 0, 0, 0)
        }
        background.draw(c)
        icon?.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    fun attach() {
        ItemTouchHelper(this).attachToRecyclerView(recyclerView)
    }
}
