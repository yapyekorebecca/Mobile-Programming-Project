package com.group10.taskmanagerapplication.ui
import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
class SwipeToEditCallback(private val adapter: TaskAdapter) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT), TaskAdapter.EditTaskListener {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        // This method is not used but must be overridden
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        if (direction == ItemTouchHelper.RIGHT) {
            adapter.showDeleteConfirmation(position)
        } else if (direction == ItemTouchHelper.LEFT) {
            // Show the MyCustomDialog to edit the task
            val task = adapter.tasks[position]
            val dialog = MyCustomDialog.newInstance(task)
            dialog.show(adapter.activity.supportFragmentManager, "MyCustomDialog")
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
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView = viewHolder.itemView
        val background = Paint().apply { color = if (dX > 0) Color.RED else Color.BLUE }
        val icon = if (dX > 0) R.drawable.ic_delete else R.drawable.ic_menu_edit
        val iconDrawable = ContextCompat.getDrawable(adapter.getContext()!!, icon)

        if (dX != 0f && isCurrentlyActive) {
            val itemHeight = itemView.bottom - itemView.top
            val intrinsicWidth = iconDrawable!!.intrinsicWidth
            val intrinsicHeight = iconDrawable.intrinsicHeight
            val iconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
            val iconMargin = (itemHeight - intrinsicHeight) / 2
            val iconLeft = if (dX > 0) itemView.left + iconMargin else itemView.right - iconMargin - intrinsicWidth
            val iconRight = if (dX > 0) itemView.left + iconMargin + intrinsicWidth else itemView.right - iconMargin
            val iconBottom = iconTop + intrinsicHeight

            c.drawRect(
                if (dX > 0) itemView.left.toFloat() else itemView.right + dX,
                itemView.top.toFloat(),
                if (dX > 0) itemView.left + dX else itemView.right.toFloat(),
                itemView.bottom.toFloat(),
                background
            )

            iconDrawable.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            iconDrawable.draw(c)
        } else {
            background.color = Color.TRANSPARENT
            c.drawRect(
                itemView.left.toFloat(),
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat(),
                background
            )
        }
    }

    override fun onEditTask(task: com.group10.taskmanagerapplication.ui.Task) {

    }
}