package com.example.habittracker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.habittracker.data.model.Habit
import com.example.habittracker.ui.habit_list.HabitListViewModel
import com.example.habittracker.util.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitListScreen(
    navController: NavController,
    factory: ViewModelFactory
) {
    val viewModel: HabitListViewModel = viewModel(factory = factory)
    val habitListItems by viewModel.habits.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Мои привычки") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Routes.ADD_EDIT_HABIT)
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить привычку"
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {

            items(habitListItems) { item ->
                HabitItem(
                    habit = item.habit,
                    streak = item.currentStreak,
                    isDone = item.isDoneToday,
                    onHabitClick = {
                        // TODO: Шаг 1.6 - Переход на экран деталей
                        // navController.navigate("${Routes.HABIT_DETAIL}/${item.habit.id}")
                    },
                    onCheckClick = {
                        // TODO: Шаг 1.5 - Реализация отметки выполнения
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun HabitItem(
    habit: Habit,
    streak: Int,
    isDone: Boolean,
    onHabitClick: () -> Unit,
    onCheckClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onHabitClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Стрик: $streak дней",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Checkbox(
                checked = isDone,
                onCheckedChange = { onCheckClick() }
            )
        }
    }
}