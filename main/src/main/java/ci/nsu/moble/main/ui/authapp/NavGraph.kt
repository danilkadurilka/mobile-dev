package ci.nsu.moble.main.ui.authapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ci.nsu.moble.main.ui.authapp.data.repository.AuthRepository
import ci.nsu.moble.main.ui.authapp.data.storage.TokenManager
import com.example.authapp.ui.screens.LoginScreen
import com.example.authapp.viewmodel.LoginViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main")
}

@Composable
fun NavGraph(
    authRepository: AuthRepository,
    tokenManager: TokenManager
) {
    val navController = rememberNavController()

    val loginViewModel = remember { LoginViewModel(authRepository) }
    val registerViewModel = remember { RegisterViewModel(authRepository) }
    val mainViewModel = remember { MainViewModel(authRepository) }

    val startDestination = if (tokenManager.isLoggedIn()) {
        Screen.Main.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                viewModel = loginViewModel
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.popBackStack()
                },
                viewModel = registerViewModel
            )
        }

        composable(Screen.Main.route) {
            MainScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                },
                viewModel = mainViewModel
            )
        }
    }
}