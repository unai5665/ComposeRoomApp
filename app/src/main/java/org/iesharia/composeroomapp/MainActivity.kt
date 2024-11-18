package org.iesharia.composeroomapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.iesharia.composeroomapp.AppDatabase
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import org.iesharia.composeroomapp.Task


class MainActivity : ComponentActivity() {

    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar la base de datos
        database = AppDatabase.getDatabase(this)

        setContent {
            TaskApp(database)
        }
    }
}
@Composable
fun TaskApp(database: AppDatabase) {
    // Obtener el DAO para interactuar con la base de datos
    val taskDao = database.taskDao()
    val scope = rememberCoroutineScope()

    // Estado para las tareas y el nombre de la nueva tarea
    var tasks by remember { mutableStateOf(listOf<Task>()) }
    var newTaskName by remember { mutableStateOf("") }

    // Cargar tareas al iniciar la Composable
    LaunchedEffect(Unit) {
        tasks = taskDao.getAllTasks()
    }

    // Interfaz de usuario
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Campo de texto para agregar una nueva tarea
        androidx.compose.material.OutlinedTextField(
            value = newTaskName,
            onValueChange = { newTaskName = it },
            label = { androidx.compose.material.Text("New Task") },
            modifier = Modifier.fillMaxWidth()
        )

        // Botón para agregar una tarea
        androidx.compose.material.Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    try {
                        val newTask = Task(name = newTaskName)
                        taskDao.insert(newTask)
                        tasks = taskDao.getAllTasks() // Actualizar la lista de tareas
                        newTaskName = "" // Limpiar el campo de texto
                    } catch (e: Exception) {
                        // Manejo de posibles errores en la base de datos
                        e.printStackTrace()
                    }
                }
            }
        ) {
            androidx.compose.material.Text("Add Task")
        }

        // Mostrar la lista de tareas
        tasks.forEach { task ->
            androidx.compose.material.Text(text = task.name)
        }
    }
}