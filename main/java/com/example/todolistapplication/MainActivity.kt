package com.example.todolistapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskDao: TaskDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Task Master"

        val db = AppDatabase.getDatabase(applicationContext)
        taskDao = db.taskDao()

        // Load tasks from the database
        lifecycleScope.launch {
            val tasks = withContext(Dispatchers.IO) {
                taskDao.getAllTasks().map { Task(it.id, it.description, it.isDone) }.toMutableList()
            }
            taskAdapter = TaskAdapter(tasks) { task ->
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        taskDao.updateTask(TaskEntity(task.id, task.description, task.isDone))
                    }
                }
            }
            binding.recyclerViewTasks.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = taskAdapter
            }
        }

        // Add task button click listener
        binding.buttonAddTask.setOnClickListener {
            val taskDescription = binding.editTextTask.text.toString()
            if (taskDescription.isNotEmpty()) {
                lifecycleScope.launch {
                    val newTask = withContext(Dispatchers.IO) {
                        val taskEntity = TaskEntity(description = taskDescription)
                        taskDao.insertTask(taskEntity)
                        taskDao.getAllTasks().last() // Simplified example
                    }
                    taskAdapter.addTask(Task(newTask.id, newTask.description, newTask.isDone))
                    binding.editTextTask.text.clear()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
