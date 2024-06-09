package com.example.todolistapplication

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapplication.databinding.ItemTaskBinding

class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onTaskUpdated: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.binding.apply {
            textViewTask.text = task.description
            textViewTask.paintFlags = if (task.isDone) {
                textViewTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                textViewTask.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            textViewTask.setOnClickListener {
                val options = arrayOf("Edit", "Mark as done", "Remove")
                android.app.AlertDialog.Builder(root.context)
                    .setTitle("Choose an option")
                    .setItems(options) { _, which ->
                        when (options[which]) {
                            "Edit" -> {
                                val editText = android.widget.EditText(root.context)
                                editText.setText(task.description)
                                android.app.AlertDialog.Builder(root.context)
                                    .setTitle("Edit Task")
                                    .setView(editText)
                                    .setPositiveButton("Save") { _, _ ->
                                        task.description = editText.text.toString()
                                        onTaskUpdated(task)
                                        notifyItemChanged(position)
                                    }
                                    .setNegativeButton("Cancel", null)
                                    .show()
                            }
                            "Mark as done" -> {
                                task.isDone = true
                                onTaskUpdated(task)
                                notifyItemChanged(position)
                            }
                            "Remove" -> {
                                tasks.removeAt(position)
                                onTaskUpdated(task)
                                notifyItemRemoved(position)
                            }
                        }
                    }
                    .show()
            }
        }
    }

    override fun getItemCount(): Int = tasks.size

    fun addTask(task: Task) {
        tasks.add(task)
        notifyItemInserted(tasks.size - 1)
    }
}
