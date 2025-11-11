package com.example.habittracker

import android.os.Bundle
import android.os.PersistableBundle
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
import androidx.navigation.compose.rememberNavController
import com.example.habittracker.ui.theme.HabitTrackerTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

object Routes{
    const val HABIT_LIST = "habit_list"
    const val ADD_EDIT_HABIT = "add_edit_habit"
    const val HABIT_DETAIL = "habit_detail"
}

class MainActivity: ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                            ScreenPlaceholder(name = "Habit List")
                        }
                        composable(Routes.ADD_EDIT_HABIT){
                            ScreenPlaceholder(name = "Add/Edit Habit")
                        }
                        composable(Routes.HABIT_DETAIL){
                            ScreenPlaceholder(name = "Habit Detail")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScreenPlaceholder(name: String, modifier: Modifier = Modifier){
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Text(text = "Placeholder for ${name}")
    }
}

@Preview
@Composable
fun ScreenPlaceholderPreview(){
    ScreenPlaceholder(name = "Screen name")
}