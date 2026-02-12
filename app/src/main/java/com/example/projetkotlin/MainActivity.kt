package com.example.projetkotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                        TaskListScreen(navController = navController, tasks = tasks)
                    }
                    composable("addTask") {
                        AddTaskScreen(navController = navController) {
                            val newId = (tasks.maxOfOrNull { it.id } ?: 0) + 1
                            tasks.add(it.copy(id = newId))
                            navController.popBackStack()
                        }
                    }
                }
            }
        }
    }
}

val mockTasks = listOf(
    Task(1, "Acheter du café", " pendant la pause"),
    Task(2, "Préparer la présentation", "Pour la soutenance"),
    Task(3, "Sport", "Ne pas oublier !")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(navController: NavController, tasks: List<Task>) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Todo List") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addTask") }) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding).padding(8.dp)) {
            items(tasks) { task ->
                TaskItem(task = task)
            }
        }
    }
}

@Composable
fun TaskItem(task: Task) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = task.title, fontWeight = FontWeight.Bold)
                if (task.description.isNotBlank()) {
                    Text(text = task.description)
                }
            }
            Text(text = task.status.name, style = MaterialTheme.typography.bodySmall)
        }
    }
}
