package com.example.habittracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.habittracker.data.db.AppDatabase
import com.example.habittracker.data.repository.HabitRepository
import com.example.habittracker.ui.add_edit_habit.AddEditHabitScreen
import com.example.habittracker.ui.habit_list.HabitDetailScreen
import com.example.habittracker.ui.theme.HabitTrackerTheme
import com.example.habittracker.util.ViewModelFactory

object Routes{
    const val HABIT_LIST = "habit_list"
    const val ADD_EDIT_HABIT = "add_edit_habit"
    const val HABIT_DETAIL = "habit_detail"
}

class MainActivity: ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = AppDatabase.getDatabase(application)
        val repository = HabitRepository(database.habitDao())
        val factory = ViewModelFactory(repository)
        enableEdgeToEdge()
        setContent {
            HabitTrackerTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Routes.HABIT_LIST,
                        modifier = Modifier.padding(innerPadding)
                    ){
                        composable(Routes.HABIT_LIST){
                            HabitListScreen(navController = navController, factory = factory)
                        }
                        composable(Routes.ADD_EDIT_HABIT){
                            AddEditHabitScreen(navController = navController, factory = factory)
                        }
                        composable(
                            route = Routes.HABIT_DETAIL + "/{habitId}",
                            arguments = listOf(navArgument("habitId"){type = NavType.StringType})
                        ){
                            HabitDetailScreen(navController = navController, factory = factory)
                        }
                    }
                }
            }
        }
    }
}
