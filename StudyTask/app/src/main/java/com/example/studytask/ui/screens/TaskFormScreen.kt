package com.example.studytask.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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

class TaskFormViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val taskRepository: TaskRepository = TaskRepository()
) : ViewModel() {

    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var subject by mutableStateOf("")
    var dueDate by mutableStateOf("")
    var status by mutableStateOf(TaskStatus.PENDING)
    var priority by mutableStateOf(1)

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    var currentTaskId: String? = null
        private set

    fun loadTask(taskId: String?) {
        if (taskId == null || currentTaskId != null) return

        currentTaskId = taskId
        viewModelScope.launch {
            val user = authRepository.currentUser ?: return@launch
            isLoading = true
            try {
                val task = taskRepository.getTask(taskId, user.uid)
                task?.let {
                    title = it.title
                    description = it.description
                    subject = it.subject
                    dueDate = it.dueDate
                    status = it.status
                    priority = it.priority
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun save(onSaved: () -> Unit) {
        viewModelScope.launch {
            val user = authRepository.currentUser ?: return@launch
            isLoading = true
            errorMessage = null
            try {
                val task = Task(
                    id = currentTaskId ?: "",
                    title = title,
                    description = description,
                    subject = subject,
                    dueDate = dueDate,
                    status = status,
                    priority = priority,
                    userId = user.uid,
                    createdAt = System.currentTimeMillis()
                )

                if (currentTaskId == null) {
                    taskRepository.createTask(task)
                } else {
                    taskRepository.updateTask(task)
                }
                onSaved()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Erro ao salvar tarefa"
            } finally {
                isLoading = false
            }
        }
    }

    fun delete(onDeleted: () -> Unit) {
        val taskId = currentTaskId ?: return
        viewModelScope.launch {
            val user = authRepository.currentUser ?: return@launch
            isLoading = true
            try {
                taskRepository.deleteTask(taskId, user.uid)
                onDeleted()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Erro ao excluir tarefa"
            } finally {
                isLoading = false
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormScreen(
    taskId: String?,
    onSaved: () -> Unit,
    onDeleted: () -> Unit,
    viewModel: TaskFormViewModel = viewModel()
) {
    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    val isEditing = taskId != null
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val isFormValid = viewModel.title.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editar tarefa" else "Nova tarefa") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Card com o formulário
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = viewModel.title,
                            onValueChange = { viewModel.title = it },
                            label = { Text("Título") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = viewModel.description,
                            onValueChange = { viewModel.description = it },
                            label = { Text("Descrição") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )

                        OutlinedTextField(
                            value = viewModel.subject,
                            onValueChange = { viewModel.subject = it },
                            label = { Text("Matéria / disciplina") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = viewModel.dueDate,
                            onValueChange = { viewModel.dueDate = it },
                            label = { Text("Data limite (texto)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Text(
                            text = "Status",
                            style = MaterialTheme.typography.labelMedium
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            FilterChip(
                                selected = viewModel.status == TaskStatus.PENDING,
                                onClick = { viewModel.status = TaskStatus.PENDING },
                                label = { Text("Pendente") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                            FilterChip(
                                selected = viewModel.status == TaskStatus.DOING,
                                onClick = { viewModel.status = TaskStatus.DOING },
                                label = { Text("Em andamento") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                            FilterChip(
                                selected = viewModel.status == TaskStatus.DONE,
                                onClick = { viewModel.status = TaskStatus.DONE },
                                label = { Text("Concluída") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }

                        OutlinedTextField(
                            value = viewModel.priority.toString(),
                            onValueChange = { value ->
                                viewModel.priority = value.toIntOrNull() ?: 1
                            },
                            label = { Text("Prioridade (1-3)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        if (errorMessage != null) {
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { viewModel.save(onSaved) },
                                modifier = Modifier.weight(1f),
                                enabled = !isLoading && isFormValid
                            ) {
                                Text("Salvar")
                            }

                            if (isEditing) {
                                OutlinedButton(
                                    onClick = { viewModel.delete(onDeleted) },
                                    modifier = Modifier.weight(1f),
                                    enabled = !isLoading
                                ) {
                                    Text("Excluir")
                                }
                            }
                        }
                    }
                }
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
