package ci.nsu.moble.main

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import androidx.compose.ui.unit.sp
import ci.nsu.moble.main.R


sealed class DepositAppScreens(val route: String, @StringRes val titleResId: Int)
{
    object Main : DepositAppScreens("main", R.string.screen_main)
    object Step1 : DepositAppScreens("step1", R.string.screen_step1)
    object Step2 : DepositAppScreens("step2", R.string.screen_step2)
    object Result : DepositAppScreens("result", R.string.screen_result)
    object History : DepositAppScreens("history", R.string.screen_history)
}
@Composable
fun DepositNavigation(viewModel: DepositVM) {
    val navController = rememberNavController()
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = { DepositBottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = DepositAppScreens.Main.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(DepositAppScreens.Main.route) {
                MainScreen(
                    onNavigateToStep1 = {
                        viewModel.reset()  // Сбрасываем поля перед новым расчетом
                        navController.navigate(DepositAppScreens.Step1.route) {
                            popUpTo(DepositAppScreens.Main.route) {
                                inclusive = false
                            }
                        }
                    },
                    onNavigateToHistory = {
                        navController.navigate(DepositAppScreens.History.route)
                    }
                )
            }

            composable(DepositAppScreens.Step1.route) {
                Step1Screen(
                    state = state,
                    onAmountChange = viewModel::onAmountChange,
                    onMonthsChange = viewModel::onMonthsChange,
                    onNavigateBack = {
                        viewModel.reset()  // Сбрасываем при возврате в главное меню
                        navController.popBackStack(
                            DepositAppScreens.Main.route,
                            inclusive = false
                        )
                    },
                    onNavigateToStep2 = {
                        navController.navigate(DepositAppScreens.Step2.route)
                    }
                )
            }

            composable(DepositAppScreens.Step2.route) {
                Step2Screen(
                    state = state,
                    onTopUpChange = viewModel::onTopUpChange,
                    onNavigateBack = { navController.popBackStack() },
                    onCalculate = {
                        viewModel.calculate()
                        navController.navigate(DepositAppScreens.Result.route)
                    }
                )
            }

            composable(DepositAppScreens.Result.route) {
                ResultScreen(
                    result = state.result,
                    onSaveAndExit = {
                        viewModel.save()
                        navController.popBackStack(
                            DepositAppScreens.Main.route,
                            inclusive = false
                        )
                    }
                )
            }

            composable(DepositAppScreens.History.route) {
                HistoryScreen(
                    history = state.history,
                    onNavigateToMain = {
                        navController.popBackStack(
                            DepositAppScreens.Main.route,
                            inclusive = false
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun DepositBottomNavigationBar(navController: NavController) {
    val items = listOf(
        DepositAppScreens.Main,
        DepositAppScreens.History
    )

    NavigationBar {
        val currentRoute = currentRoute(navController)

        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Text(
                        text = when (screen) {
                            DepositAppScreens.Main -> "🏠"
                            DepositAppScreens.History -> "📋"
                            else -> "•"
                        },
                        fontSize = 20.sp
                    )
                },
                label = {  Text(stringResource(id = screen.titleResId))  },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}