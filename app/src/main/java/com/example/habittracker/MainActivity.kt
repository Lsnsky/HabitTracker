package com.example.habittracker

import HabitListScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
// Добавляем все необходимые иконки
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.habittracker.data.db.AppDatabase
import com.example.habittracker.data.repository.HabitRepository
import com.example.habittracker.ui.add_edit_habit.AddEditHabitScreen
//import com.example.habittracker.ui.habit_detail.HabitDetailScreen
import com.example.habittracker.ui.habit_list.HabitDetailScreen
import com.example.habittracker.ui.theme.HabitTrackerTheme
import com.example.habittracker.util.ViewModelFactory

object Routes {
    const val HABIT_LIST = "habit_list"
    const val STATS = "stats"
    const val PROFILE = "profile"
    const val ADD_EDIT_HABIT = "add_edit_habit"
    const val HABIT_DETAIL = "habit_detail"
}

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Habits : BottomBarScreen(Routes.HABIT_LIST, "Привычки", Icons.Default.Checklist)
    object Stats : BottomBarScreen(Routes.STATS, "Статистика", Icons.Default.Insights)
    object Profile : BottomBarScreen(Routes.PROFILE, "Профиль", Icons.Default.Person)
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = AppDatabase.getDatabase(application)
        val repository = HabitRepository(database.habitDao())
        val factory = ViewModelFactory(repository)
        enableEdgeToEdge()
        setContent {
            HabitTrackerTheme(darkTheme = false) {
                val navController = rememberNavController()
                val screens = listOf(
                    BottomBarScreen.Habits,
                    BottomBarScreen.Stats,
                    BottomBarScreen.Profile
                )
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val isTopLevelDestination = screens.any { it.route == currentDestination?.route }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                // Показываем заголовок только для главных экранов, чтобы не было "мусора"
                                if (isTopLevelDestination) {
                                    val currentScreen = screens.find { it.route == currentDestination?.route }
                                    Text(currentScreen?.title ?: "")
                                }
                            },
                            navigationIcon = {
                                // Показываем стрелку назад, если это не главный экран
                                if (!isTopLevelDestination) {
                                    IconButton(onClick = { navController.navigateUp() }) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowBack,
                                            contentDescription = "Назад"
                                        )
                                    }
                                }
                            },
                            actions = {
                                // Иконки, которые есть всегда
                                IconButton(onClick = { /* TODO: Navigate to Premium */ }) {
                                    Icon(
                                        imageVector = Icons.Default.WorkspacePremium,
                                        contentDescription = "Премиум"
                                    )
                                }
                                IconButton(onClick = { /* TODO: Navigate to Settings */ }) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Настройки"
                                    )
                                }
                            }
                        )
                    },
                    // --- ИЗМЕНЕНИЕ: Убираем условие `if (isTopLevelDestination)` ---
                    bottomBar = {
                        NavigationBar {
                            screens.forEach { screen ->
                                NavigationBarItem(
                                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(screen.icon, contentDescription = screen.title) },
                                    label = { Text(screen.title) }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Routes.HABIT_LIST,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Важно: Убедитесь, что Scaffold удален из всех дочерних экранов!
                        composable(Routes.HABIT_LIST) {
                            HabitListScreen(navController = navController, factory = factory)
                        }
                        composable(Routes.STATS) { ScreenPlaceholder("Статистика") }
                        composable(Routes.PROFILE) { ScreenPlaceholder("Профиль") }
                        composable(Routes.ADD_EDIT_HABIT) {
                            AddEditHabitScreen(navController = navController, factory = factory)
                        }
                        composable(
                            route = Routes.HABIT_DETAIL + "/{habitId}",
                            arguments = listOf(navArgument("habitId") { type = NavType.StringType })
                        ) {
                            HabitDetailScreen(navController = navController, factory = factory)
                        }
                    }
                }
            }
        }
    }
}

// Вспомогательная функция для экранов-заглушек
@Composable
fun ScreenPlaceholder(name: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Экран '$name' в разработке")
    }
}
