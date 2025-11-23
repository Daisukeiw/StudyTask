package com.example.studytask.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.studytask.ui.screens.HomeScreen
import com.example.studytask.ui.screens.LoginScreen
import com.example.studytask.ui.screens.TaskFormScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Routes.LOGIN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onAddTask = {
                    navController.navigate(Routes.TASK_FORM)
                },
                onEditTask = { taskId ->
                    navController.navigate("${Routes.TASK_FORM}/$taskId")
                },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        // Nova tarefa
        composable(Routes.TASK_FORM) {
            TaskFormScreen(
                taskId = null,
                onSaved = { navController.popBackStack() },
                onDeleted = { navController.popBackStack() }
            )
        }

        // Editar tarefa
        composable(
            route = "${Routes.TASK_FORM}/{taskId}",
            arguments = listOf(
                navArgument("taskId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")
            TaskFormScreen(
                taskId = taskId,
                onSaved = { navController.popBackStack() },
                onDeleted = { navController.popBackStack() }
            )
        }
    }
}
