package ci.nsu.moble.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import androidx.compose.ui.unit.sp


sealed class AppScreens(val route: String, val title: String) {
    object Home : AppScreens("home", "Главная")
    object Profile : AppScreens("profile", "Профиль")
    object Settings : AppScreens("settings", "Настройки")
    object Favorites : AppScreens("favorites", "Избранное")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppScreens.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppScreens.Home.route) {
                HomeScreen(
                    onNavigateToProfile = {
                        navController.navigate(AppScreens.Profile.route)
                    }
                )
            }
            composable(AppScreens.Profile.route) {
                ProfileScreen(
                    onNavigateToSettings = {
                        navController.navigate(AppScreens.Settings.route)
                    }
                )
            }
            composable(AppScreens.Settings.route) {
                SettingsScreen()
            }
            composable(AppScreens.Favorites.route) {
                FavoritesScreen()
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        AppScreens.Home,
        AppScreens.Profile,
        AppScreens.Settings,
        AppScreens.Favorites
    )

    NavigationBar {
        val currentRoute = currentRoute(navController)

        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Text(
                        text = when (screen) {
                            AppScreens.Home -> "🏠"
                            AppScreens.Profile -> "👤"
                            AppScreens.Settings -> "⚙️"
                            AppScreens.Favorites -> "❤️"
                        },
                        fontSize = 20.sp
                    )
                },
                label = { Text(screen.title) },
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

// Вспомогательная функция для получения текущего маршрута
@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}