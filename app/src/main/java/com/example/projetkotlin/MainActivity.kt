package com.example.projetkotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.projetkotlin.ui.theme.ProjetKotlinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjetKotlinTheme {
                val navController = rememberNavController()
                val tasks = remember { mutableStateListOf<Task>() }
                tasks.addAll(mockTasks)

                NavHost(navController = navController, startDestination = "taskList") {
                    composable("taskList") {
                        TaskListScreen(navController = navController, tasks = tasks) { updatedTask ->
                            val index = tasks.indexOfFirst { it.id == updatedTask.id }
                            if (index != -1) {
                                tasks[index] = updatedTask
                            }
                        }
                    }
                    composable("addTask") {
                        AddTaskScreen(navController = navController) {
                            val newId = (tasks.maxOfOrNull { it.id } ?: 0) + 1
                            tasks.add(it.copy(id = newId))
                            navController.popBackStack()
                        }
                    }
                    composable(
                        "editTask/{taskId}",
                        arguments = listOf(navArgument("taskId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val taskId = backStackEntry.arguments?.getInt("taskId")
                        val task = tasks.find { it.id == taskId }
                        if (task != null) {
                            EditTaskScreen(navController = navController, task = task) {
                                val index = tasks.indexOf(task)
                                tasks[index] = it
                                navController.popBackStack()
                            }
                        }
                    }
                }
            }
        }
    }
}

val mockTasks = listOf(
    Task(1, "Acheter du café", "Indispensable pour coder"),
    Task(2, "Préparer la présentation", "Pour le 13 mars", TaskStatus.DONE),
    Task(3, "Sport", "Ne pas oublier !", TaskStatus.LATE)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(navController: NavController, tasks: List<Task>, onTaskUpdated: (Task) -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    var filter by remember { mutableStateOf<TaskStatus?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Todo List: ${filter?.name ?: "All"}") },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filter Tasks")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All") },
                                onClick = {
                                    filter = null
                                    showMenu = false
                                }
                            )
                            TaskStatus.values().forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status.name) },
                                    onClick = {
                                        filter = status
                                        showMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addTask") }) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        val filteredTasks = if (filter == null) tasks else tasks.filter { it.status == filter }

        LazyColumn(modifier = Modifier.padding(innerPadding).padding(8.dp)) {
            items(filteredTasks) { task ->
                TaskItem(task = task, onTaskClicked = {
                    navController.navigate("editTask/${task.id}")
                }, onTaskCompleted = { isChecked ->
                    val newStatus = if (isChecked) TaskStatus.DONE else TaskStatus.TODO
                    onTaskUpdated(task.copy(status = newStatus))
                })
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onTaskClicked: () -> Unit,
    onTaskCompleted: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onTaskClicked() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = if (task.status == TaskStatus.LATE) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.status == TaskStatus.DONE,
                onCheckedChange = { onTaskCompleted(it) }
            )
            Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
                val textDecoration = if (task.status == TaskStatus.DONE) TextDecoration.LineThrough else null
                Text(text = task.title, fontWeight = FontWeight.Bold, textDecoration = textDecoration)
                if (task.description.isNotBlank()) {
                    Text(text = task.description, textDecoration = textDecoration)
                }
            }
            Text(text = task.status.name, style = MaterialTheme.typography.bodySmall)
        }
    }
}
