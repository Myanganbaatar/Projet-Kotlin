package com.example.projetkotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.example.projetkotlin.ui.theme.ProjetKotlinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjetKotlinTheme {
                val navController = rememberNavController()
                val tasks = remember { mutableStateListOf<Task>() }
                var totalPoints by remember { mutableIntStateOf(0) }
                
                if (tasks.isEmpty()) tasks.addAll(mockTasks)

                NavHost(navController = navController, startDestination = "taskList") {
                    composable("taskList") {
                        TaskListScreen(
                            navController = navController,
                            tasks = tasks,
                            points = totalPoints,
                            onTaskUpdated = { updatedTask ->
                                val index = tasks.indexOfFirst { it.id == updatedTask.id }
                                if (index != -1) {
                                    if (tasks[index].status != TaskStatus.DONE && updatedTask.status == TaskStatus.DONE) {
                                        totalPoints += when(updatedTask.priority) {
                                            Priority.HIGH -> 30
                                            Priority.MEDIUM -> 20
                                            Priority.LOW -> 10
                                        }
                                    }
                                    tasks[index] = updatedTask
                                }
                            },
                            onClearCompleted = {
                                tasks.removeAll { it.status == TaskStatus.DONE }
                            }
                        )
                    }
                    composable("addTask") {
                        AddTaskScreen(navController = navController) { task ->
                            val newId = (tasks.maxOfOrNull { it.id } ?: 0) + 1
                            tasks.add(task.copy(id = newId))
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
                            EditTaskScreen(navController = navController, task = task) { updatedTask ->
                                val index = tasks.indexOfFirst { it.id == task.id }
                                if (index != -1) tasks[index] = updatedTask
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
    Task(1, "Acheter du café", "Indispensable pour coder", TaskStatus.TODO, Periodicity.DAILY, Priority.HIGH),
    Task(2, "Préparer la présentation", "Pour le 13 mars", TaskStatus.DONE, Periodicity.NONE, Priority.MEDIUM),
    Task(3, "Sport", "Ne pas oublier !", TaskStatus.LATE, Periodicity.WEEKLY, Priority.LOW)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    navController: NavController,
    tasks: List<Task>,
    points: Int,
    onTaskUpdated: (Task) -> Unit,
    onClearCompleted: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var filter by remember { mutableStateOf<TaskStatus?>(null) }
    
    val animatedPoints by animateIntAsState(targetValue = points, label = "pointsAnimation")
    val rank = when {
        points >= 100 -> "Expert 🏆"
        points >= 50 -> "Intermédiaire ⭐"
        else -> "Débutant 🌱"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("My Todo List", fontSize = 18.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFBC02D), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("$animatedPoints pts - $rank", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onClearCompleted) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Purge DONE")
                    }
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
                            TaskStatus.entries.forEach { status ->
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
        val filteredTasks = (if (filter == null) tasks else tasks.filter { it.status == filter })
            .sortedByDescending { it.priority }

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
        ) {
            items(filteredTasks, key = { it.id }) { task ->
                Box(modifier = Modifier.animateItem()) {
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
}

@Composable
fun TaskItem(
    task: Task,
    onTaskClicked: () -> Unit,
    onTaskCompleted: (Boolean) -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = when (task.status) {
            TaskStatus.LATE -> MaterialTheme.colorScheme.errorContainer
            TaskStatus.DONE -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = spring(),
        label = "cardBackground"
    )

    val priorityColor = when (task.priority) {
        Priority.HIGH -> Color.Red
        Priority.MEDIUM -> Color(0xFFFFA500)
        Priority.LOW -> Color.Blue
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onTaskClicked() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (task.status == TaskStatus.DONE) 0.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier.padding(end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .padding(vertical = 8.dp)
                    .background(priorityColor, MaterialTheme.shapes.small)
                    .align(Alignment.CenterVertically)
                    .fillMaxHeight()
                    .size(width = 6.dp, height = 60.dp)
            )

            Checkbox(
                checked = task.status == TaskStatus.DONE,
                onCheckedChange = { onTaskCompleted(it) },
                modifier = Modifier.padding(start = 8.dp)
            )

            // Miniature de la photo (Version 6)
            if (task.imageUri != null) {
                AsyncImage(
                    model = task.imageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(start = 8.dp)
                        .clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.weight(1f).padding(start = 8.dp, top = 16.dp, bottom = 16.dp)) {
                val textDecoration = if (task.status == TaskStatus.DONE) TextDecoration.LineThrough else null
                val textColor = if (task.status == TaskStatus.DONE) Color.Gray else Color.Unspecified

                Text(
                    text = task.title,
                    fontWeight = FontWeight.Bold,
                    textDecoration = textDecoration,
                    color = textColor
                )
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        textDecoration = textDecoration,
                        color = textColor.copy(alpha = 0.8f),
                        maxLines = 1
                    )
                }
                if (task.periodicity != Periodicity.NONE) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Repeat,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = task.periodicity.name.lowercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = task.status.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (task.status == TaskStatus.LATE) MaterialTheme.colorScheme.error else Color.Gray
                )
                Text(
                    text = task.priority.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = priorityColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
