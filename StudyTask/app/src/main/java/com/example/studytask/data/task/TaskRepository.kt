package com.example.studytask.data.task

import com.example.studytask.data.model.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TaskRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private fun tasksCollection(userId: String) =
        db.collection("users")
            .document(userId)
            .collection("tasks")

    suspend fun createTask(task: Task): String {
        val userId = task.userId
        val collection = tasksCollection(userId)
        val docRef = collection.add(task).await()
        collection.document(docRef.id).update("id", docRef.id).await()
        return docRef.id
    }

    suspend fun updateTask(task: Task) {
        require(task.id.isNotEmpty()) { "Task id não pode ser vazio para update" }
        tasksCollection(task.userId)
            .document(task.id)
            .set(task)
            .await()
    }

    suspend fun deleteTask(taskId: String, userId: String) {
        tasksCollection(userId)
            .document(taskId)
            .delete()
            .await()
    }

    suspend fun getTask(taskId: String, userId: String): Task? {
        val snapshot = tasksCollection(userId)
            .document(taskId)
            .get()
            .await()

        return snapshot.toObject(Task::class.java)
    }

    suspend fun getTasksByUser(userId: String): List<Task> {
        val snapshot = tasksCollection(userId)
            .get()
            .await()

        return snapshot.documents
            .mapNotNull { it.toObject(Task::class.java) }
            .sortedBy { it.createdAt }
    }
}
