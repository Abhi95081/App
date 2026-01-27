package com.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Apps()
        }
    }
}

/* ----------------------- DATA MODEL ----------------------- */

data class Task(
    val id: Int,
    val title: String,
    val allowedMinutes: Int,
    var isCompleted: Boolean = false
)

/* ----------------------- ROOT APP ----------------------- */

@Composable
fun Apps() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            TaskScreen()
        }
    }
}

/* ----------------------- MAIN SCREEN ----------------------- */

@Composable
fun TaskScreen() {

    var taskTitle by remember { mutableStateOf("") }
    var timeLimit by remember { mutableStateOf("") }

    val tasks = remember { mutableStateListOf<Task>() }
    var taskIdCounter by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {

        Text(
            text = "Distraction Controller",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = taskTitle,
            onValueChange = { taskTitle = it },
            label = { Text("Task name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = timeLimit,
            onValueChange = { timeLimit = it },
            label = { Text("Allowed App Time (minutes)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (taskTitle.isNotBlank() && timeLimit.isNotBlank()) {
                    tasks.add(
                        Task(
                            id = taskIdCounter++,
                            title = taskTitle,
                            allowedMinutes = timeLimit.toInt()
                        )
                    )
                    taskTitle = ""
                    timeLimit = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Task")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Divider()

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn {
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    onComplete = {
                        task.isCompleted = true
                        // 🔥 This is where app access is unlocked
                        // unlockSelectedApps(task.allowedMinutes)
                    }
                )
            }
        }
    }
}

/* ----------------------- TASK ITEM ----------------------- */

@Composable
fun TaskItem(
    task: Task,
    onComplete: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(vertical = 6.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                Text(text = task.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Allowed Time: ${task.allowedMinutes} min",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (!task.isCompleted) {
                Button(onClick = onComplete) {
                    Text("Complete")
                }
            } else {
                Text("✔ Done", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

/* ----------------------- PREVIEW ----------------------- */

@Preview(showBackground = true)
@Composable
fun AppAndroidPreview() {
    App()
}
