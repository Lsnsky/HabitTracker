package com.example.habittracker.ui.add_edit_habit

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.habittracker.ui.habit_list.UiEvent
import com.example.habittracker.util.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditHabitScreen(
    navController: NavController,
    // ViewModel будет предоставлена через Hilt или другую фабрику ViewModel
    factory: ViewModelFactory
) {
    val viewModel: AddEditHabitViewModel = viewModel(factory = factory)
    // Этот блок слушает одноразовые события (UiEvent) от ViewModel
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                // Событие навигации
                is UiEvent.NavigateTo -> {
                    // Если route равен null, это сигнал для возврата назад
                    if (event.route == null) {
                        navController.popBackStack()
                    } else {
                        navController.navigate(event.route)
                    }
                }
                is UiEvent.ShowSnackbar -> {
                    // Логика для показа Snackbar будет добавлена позже (Шаг 1.7)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новая привычка") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // При клике отправляем событие сохранения в ViewModel
                viewModel.onEvent(AddEditHabitEvent.OnSaveHabitClick)
            }) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Сохранить привычку"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp) // Пространство между элементами
        ) {
            // Поле для ввода названия привычки
            OutlinedTextField(
                value = viewModel.habitName,
                onValueChange = { newName ->
                    viewModel.onEvent(AddEditHabitEvent.OnNameChange(newName))
                },
                label = { Text("Название привычки") },
                modifier = Modifier.fillMaxWidth()
            )

            // Выбор частоты выполнения
            Text("Частота", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = viewModel.habitFrequency == "Ежедневно",
                    onClick = { viewModel.onEvent(AddEditHabitEvent.OnFrequencyChange("Ежедневно")) },
                    label = { Text("Ежедневно") }
                )
                FilterChip(
                    selected = viewModel.habitFrequency == "Дни недели",
                    onClick = { viewModel.onEvent(AddEditHabitEvent.OnFrequencyChange("Дни недели")) },
                    label = { Text("Дни недели") }
                )
            }
        }
    }
}
