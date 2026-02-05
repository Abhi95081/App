package com.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ----------------------- ACTIVITY ----------------------- */

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { FocusApp() }
    }
}

/* ----------------------- DATA MODELS ----------------------- */

data class Task(
    val id: Int,
    val title: String,
    val allowedMinutes: Int,
    val isCompleted: Boolean = false
)

data class BlockedApp(
    val name: String,
    val packageName: String,
    val isSelected: Boolean = false
)

/* ----------------------- ROOT APP ----------------------- */

@Composable
fun FocusApp() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(modifier = Modifier.fillMaxSize()) {
            TaskScreen()
        }
    }
}

/* ----------------------- MAIN SCREEN ----------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen() {

    var taskTitle by remember { mutableStateOf("") }
    var timeLimit by remember { mutableStateOf("") }
    var taskId by remember { mutableIntStateOf(0) }

    val tasks = remember { mutableStateListOf<Task>() }
    val apps = remember {
        mutableStateListOf(
            BlockedApp("Instagram", "com.instagram.android"),
            BlockedApp("YouTube", "com.google.android.youtube"),
            BlockedApp("Facebook", "com.facebook.katana")
        )
    }

    val allTasksCompleted = tasks.isNotEmpty() && tasks.all { it.isCompleted }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Distraction Controller") })
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            /* ---------- STATUS ---------- */
            item {
                StatusCard(allTasksCompleted, tasks)
            }

            /* ---------- ADD TASK ---------- */
            item {
                Text("Add Task", fontWeight = FontWeight.Bold)
            }

            item {
                OutlinedTextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text("Task name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = timeLimit,
                    onValueChange = { timeLimit = it },
                    label = { Text("Allowed app time (minutes)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val minutes = timeLimit.toIntOrNull()
                        if (taskTitle.isNotBlank() && minutes != null && minutes > 0) {
                            tasks.add(Task(taskId++, taskTitle, minutes))
                            taskTitle = ""
                            timeLimit = ""
                        }
                    }
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(Modifier.width(6.dp))
                    Text("Add Task")
                }
            }

            /* ---------- TASK LIST ---------- */
            item {
                Text("Your Tasks", fontWeight = FontWeight.Bold)
            }

            items(tasks, key = { it.id }) { task ->
                TaskItem(
                    task = task,
                    onComplete = {
                        val i = tasks.indexOf(task)
                        tasks[i] = task.copy(isCompleted = true)
                    },
                    onDelete = { tasks.remove(task) }
                )
            }

            /* ---------- APP SELECTION ---------- */
            item {
                Spacer(Modifier.height(8.dp))
                Text("Blocked Applications", fontWeight = FontWeight.Bold)
            }

            items(apps, key = { it.packageName }) { app ->
                AppItem(
                    app = app,
                    unlocked = allTasksCompleted,
                    onToggle = {
                        val i = apps.indexOf(app)
                        apps[i] = app.copy(isSelected = !app.isSelected)
                    }
                )
            }
        }
    }
}

/* ----------------------- STATUS CARD ----------------------- */

@Composable
fun StatusCard(unlocked: Boolean, tasks: List<Task>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (unlocked)
                MaterialTheme.colorScheme.secondaryContainer
            else
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                if (unlocked) "Apps Unlocked 🎉" else "Apps Locked 🔒",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text("${tasks.count { it.isCompleted }} / ${tasks.size} tasks completed")
        }
    }
}

/* ----------------------- TASK ITEM ----------------------- */

@Composable
fun TaskItem(task: Task, onComplete: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(Modifier.weight(1f)) {
                Text(
                    task.title,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (task.isCompleted)
                        TextDecoration.LineThrough else TextDecoration.None
                )
                Text("Allowed: ${task.allowedMinutes} min")
            }

            if (!task.isCompleted) {
                IconButton(onClick = onComplete) {
                    Icon(Icons.Default.Check, "Complete")
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Delete")
            }
        }
    }
}

/* ----------------------- APP ITEM ----------------------- */

@Composable
fun AppItem(
    app: BlockedApp,
    unlocked: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(Modifier.weight(1f)) {
                Text(app.name, fontWeight = FontWeight.Medium)
                Text(
                    if (unlocked) "Unlocked" else "Locked until tasks complete",
                    color = if (unlocked)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                )
            }

            Switch(
                checked = app.isSelected,
                onCheckedChange = { if (unlocked) onToggle() },
                enabled = unlocked
            )
        }
    }
}

/* ----------------------- PREVIEW ----------------------- */

@Preview(showBackground = true)
@Composable
fun PreviewApp() {
    FocusApp()
}
