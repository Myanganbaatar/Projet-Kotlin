package com.example.projetkotlin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(navController: NavController, task: Task, onUpdateTask: (Task) -> Unit) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var status by remember { mutableStateOf(task.status) }
    var periodicity by remember { mutableStateOf(task.periodicity) }
    var priority by remember { mutableStateOf(task.priority) }

    var statusExpanded by remember { mutableStateOf(false) }
    var periodicityExpanded by remember { mutableStateOf(false) }
    var priorityExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Task") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            // Status Selector (Ajouté pour permettre de passer en LATE ou corriger)
            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = { statusExpanded = !statusExpanded },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                OutlinedTextField(
                    value = status.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false }
                ) {
                    TaskStatus.entries.forEach { s ->
                        DropdownMenuItem(
                            text = { Text(s.name) },
                            onClick = {
                                status = s
                                statusExpanded = false
                            }
                        )
                    }
                }
            }

            // Periodicity Selector (Version 3)
            ExposedDropdownMenuBox(
                expanded = periodicityExpanded,
                onExpandedChange = { periodicityExpanded = !periodicityExpanded },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                OutlinedTextField(
                    value = periodicity.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Periodicity") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = periodicityExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = periodicityExpanded,
                    onDismissRequest = { periodicityExpanded = false }
                ) {
                    Periodicity.entries.forEach { p ->
                        DropdownMenuItem(
                            text = { Text(p.name) },
                            onClick = {
                                periodicity = p
                                periodicityExpanded = false
                            }
                        )
                    }
                }
            }

            // Priority Selector (Version 4)
            ExposedDropdownMenuBox(
                expanded = priorityExpanded,
                onExpandedChange = { priorityExpanded = !priorityExpanded },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                OutlinedTextField(
                    value = priority.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Priority") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = priorityExpanded,
                    onDismissRequest = { priorityExpanded = false }
                ) {
                    Priority.entries.forEach { pr ->
                        DropdownMenuItem(
                            text = { Text(pr.name) },
                            onClick = {
                                priority = pr
                                priorityExpanded = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onUpdateTask(task.copy(
                            title = title, 
                            description = description,
                            status = status,
                            periodicity = periodicity,
                            priority = priority
                        ))
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Update Task")
            }
        }
    }
}
