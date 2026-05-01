package ci.nsu.moble.main.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ci.nsu.moble.main.R
import ci.nsu.moble.main.dependences.DependencesInjection
import ci.nsu.moble.main.ui.auth.LoginScreen
import ci.nsu.moble.main.ui.auth.LoginViewModel
import ci.nsu.moble.main.ui.register.RegisterScreen
import ci.nsu.moble.main.ui.register.RegisterViewModel
import ci.nsu.moble.main.ui.deposit.DepositFlowScreen
import ci.nsu.moble.main.ui.deposit.DepositVM
import ci.nsu.moble.main.ui.deposit.HistoryScreen
import ci.nsu.moble.main.ui.users.UsersScreen
import ci.nsu.moble.main.ui.users.UsersViewModel

sealed class BottomNavItem(val route: String, val titleResId: Int)
{
    object Users : BottomNavItem("users", R.string.screen_users)
    object History : BottomNavItem("history", R.string.screen_history)
    object NewDeposit : BottomNavItem("new_deposit", R.string.screen_new_deposit)
}
@Composable
fun AppNavHost(dependencesInjection: DependencesInjection)
{
    val navController = rememberNavController()
    val depositVM: DepositVM = viewModel(factory = dependencesInjection.viewModelFactory)
    val isLoggedIn = dependencesInjection.tokenManager.isLoggedIn()
    val startDestination = if (isLoggedIn)
    {
        BottomNavItem.Users.route
    }
    else
    {
        "auth"
    }

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            if (currentRoute in listOf(
                    BottomNavItem.Users.route,
                    BottomNavItem.History.route,
                    BottomNavItem.NewDeposit.route
                )
            ) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == BottomNavItem.Users.route,
                        onClick = {
                            if (currentRoute != BottomNavItem.Users.route) {
                                navController.navigate(BottomNavItem.Users.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        },
                        icon = { Text("👥") },
                        label = { Text(stringResource(id = BottomNavItem.Users.titleResId)) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == BottomNavItem.History.route,
                        onClick = {
                            if (currentRoute != BottomNavItem.History.route) {
                                navController.navigate(BottomNavItem.History.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        },
                        icon = { Text("📋") },
                        label = { Text(stringResource(id = BottomNavItem.History.titleResId)) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == BottomNavItem.NewDeposit.route,
                        onClick = {
                            if (currentRoute != BottomNavItem.NewDeposit.route) {
                                navController.navigate(BottomNavItem.NewDeposit.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        },
                        icon = { Text("➕") },
                        label = { Text(stringResource(id = BottomNavItem.NewDeposit.titleResId)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("auth")
            {
                val loginViewModel: LoginViewModel = viewModel(factory = dependencesInjection.viewModelFactory)
                LoginScreen(
                    onLoginSuccess = {
                        depositVM.reset()
                        val userId = dependencesInjection.tokenManager.getUserId() ?: -1L
                        depositVM.updateUserId(userId)
                        navController.navigate(BottomNavItem.Users.route) {
                            popUpTo("auth") { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate("register")
                    },
                    viewModel = loginViewModel
                )
            }
            composable("register")
            {
                val registerViewModel: RegisterViewModel = viewModel(factory = dependencesInjection.viewModelFactory)
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.popBackStack()
                    },
                    viewModel = registerViewModel
                )
            }
            composable(BottomNavItem.Users.route)
            {
                val usersViewModel: UsersViewModel = viewModel(factory = dependencesInjection.viewModelFactory)
                UsersScreen(viewModel = usersViewModel)
            }
            composable(BottomNavItem.History.route)
            {
                val state by depositVM.uiState.collectAsState()
                HistoryScreen(
                    history = state.history,
                    onDelete = { depositVM.delete(it) }
                )
            }
            composable(BottomNavItem.NewDeposit.route)
            {
                DepositFlowScreen(
                    viewModel = depositVM,
                    onFinish = {
                        navController.navigate(BottomNavItem.History.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}