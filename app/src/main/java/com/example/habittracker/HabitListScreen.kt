package com.example.habittracker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
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

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Мои привычки") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                .padding(paddingValues),
                contentPadding = PaddingValues(all = 8.dp)
        ) {

            items(items = habitListItems, key = { it.habit.id }) { item ->
                HabitItem(
                    habit = item.habit,
                    streak = item.currentStreak,
                    isDone = item.isDoneToday,
                    onHabitClick = {
                         navController.navigate("${Routes.HABIT_DETAIL}/${item.habit.id}")
                    },
                    onCheckClick = {
                        viewModel.onEvent(HabitListEvent.OnHabitCheckChanged(item.habit))
                    }
                )
                HorizontalDivider()
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onHabitClick() }
            .padding(vertical = 12.dp)
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
                    text = "Streak: $streak days",
                    style = MaterialTheme.typography.bodyMedium,
                    color = androidx . compose . material3 . MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box(
                modifier = Modifier
                    .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                    .clickable(
                        role = androidx.compose.ui.semantics.Role.Checkbox,
                        onClick = onCheckClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Checkbox(
                    checked = isDone,
                    onCheckedChange = null
                )
            }
        }
    }
}