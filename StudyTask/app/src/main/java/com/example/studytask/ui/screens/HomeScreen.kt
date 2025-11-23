package com.example.studytask.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studytask.data.auth.AuthRepository
import com.example.studytask.data.model.Task
import com.example.studytask.data.model.TaskStatus
import com.example.studytask.data.task.TaskRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val taskRepository: TaskRepository = TaskRepository()
) : ViewModel() {

    var tasks by mutableStateOf<List<Task>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            val user = authRepository.currentUser ?: return@launch
            isLoading = true
            try {
                tasks = taskRepository.getTasksByUser(user.uid)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            val user = authRepository.currentUser ?: return@launch
            isLoading = true
            try {
                taskRepository.deleteTask(taskId, user.uid)
                loadTasks()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }


    fun logout(onLoggedOut: () -> Unit) {
        authRepository.logout()
        onLoggedOut()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddTask: () -> Unit,
    onEditTask: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val tasks = viewModel.tasks
    val isLoading = viewModel.isLoading

    LaunchedEffect(Unit) {
        viewModel.loadTasks()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minhas Tarefas") },
                actions = {
                    TextButton(onClick = { viewModel.logout(onLogout) }) {
                        Text(
                            text = "Sair",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Nova tarefa") },
                icon = { Icon(Icons.Default.Add, contentDescription = "Adicionar tarefa") },
                onClick = onAddTask
            )
        }

    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                if (tasks.isEmpty()) {
                    // Estado vazio bonitinho
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Nenhuma tarefa por aqui.",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Toque em \"Nova tarefa\" para começar.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Resumo rápido no topo
                        Text(
                            text = "Você tem ${tasks.size} tarefa(s).",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(tasks) { task ->
                                TaskItem(
                                    task = task,
                                    onClick = { onEditTask(task.id) },
                                    onDelete = { viewModel.deleteTask(task.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val statusText = when (task.status) {
        TaskStatus.PENDING -> "Pendente"
        TaskStatus.DOING -> "Em andamento"
        TaskStatus.DONE -> "Concluída"
    }

    val statusColor = when (task.status) {
        TaskStatus.PENDING -> MaterialTheme.colorScheme.tertiary
        TaskStatus.DOING -> MaterialTheme.colorScheme.primary
        TaskStatus.DONE -> MaterialTheme.colorScheme.secondary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Título + botão excluir
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(onClick = onDelete) {
                    Text(
                        text = "Excluir",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            if (task.subject.isNotEmpty()) {
                Text(
                    text = task.subject,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = "Status: $statusText",
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor
                )

                if (task.dueDate.isNotEmpty()) {
                    Text(
                        text = "Prazo: ${task.dueDate}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (task.description.isNotEmpty()) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
