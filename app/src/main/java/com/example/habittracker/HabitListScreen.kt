package com.example.habittracker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.habittracker.data.model.Habit
import com.example.habittracker.ui.habit_list.HabitListViewModel
import com.example.habittracker.util.ViewModelFactory
import androidx.compose.runtime.*
import com.example.habittracker.ui.habit_list.HabitListEvent
import com.example.habittracker.ui.habit_list.UiEvent
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitListScreen(
    navController: NavController,
    factory: ViewModelFactory
) {
    val viewModel: HabitListViewModel = viewModel(factory = factory)
    val habitListItems by viewModel.habits.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(key1 = true){
        viewModel.uiEvent.collectLatest { event ->
            when(event){
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
                else -> {}
            }
        }
    }

    // ИЗМЕНЕНИЕ: Scaffold полностью удален. Используем Box как корневой элемент.
    Box(modifier = Modifier.fillMaxSize()) {
        if (habitListItems.isEmpty()) {
            // Отображаем состояние "пустого экрана", если привычек нет.
            EmptyState()
        } else {
            // Иначе отображаем список привычек.
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                // Отступ снизу, чтобы FAB не перекрывал последний элемент
                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
            ) {

                items(items = habitListItems, key = { it.habit.id }) { item ->
                    MaterialHabitItem(
                        habit = item.habit,
                        streak = item.currentStreak,
                        isDone = item.isDoneToday,
                        onHabitClick = {
                            navController.navigate("${Routes.HABIT_DETAIL}/${item.habit.id}")
                        },
                        onCheckClick = {
                            viewModel.onEvent(HabitListEvent.OnHabitCheckChanged(item.habit))
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Плавающая кнопка действия располагается поверх контента благодаря Box
        FloatingActionButton(
            onClick = { navController.navigate(Routes.ADD_EDIT_HABIT) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Добавить привычку"
            )
        }

        // Snackbar также располагается поверх контента
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// Вспомогательная функция для пустого состояния
@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ... (код EmptyState остается без изменений)
    }
}

@Composable
fun MaterialHabitItem(
    habit: Habit,
    streak: Int,
    isDone: Boolean,
    onHabitClick: () -> Unit,
    onCheckClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // ИЗМЕНЕНИЕ: Заменяем старый HabitItem на более чистый Card
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onHabitClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Стрик: $streak дней",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Checkbox(
                checked = isDone,
                onCheckedChange = { onCheckClick() }
            )
        }
    }
}
