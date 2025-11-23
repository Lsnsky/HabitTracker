import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.habittracker.Routes
import com.example.habittracker.data.model.Habit
import com.example.habittracker.ui.habit_list.HabitListEvent
import com.example.habittracker.ui.habit_list.HabitListViewModel
import com.example.habittracker.ui.habit_list.UiEvent
import com.example.habittracker.util.ViewModelFactory
// ВНИМАНИЕ: Вам нужно импортировать Routes. Нажмите Alt+Enter на Routes в коде ниже,
// или добавьте импорт вручную, например: import com.example.habittracker.navigation.Routes
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

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action,
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(HabitListEvent.OnUndoDeleteClick)
                    }
                }
                else -> {}
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
        ) {
            if (habitListItems.isEmpty()) {
                item {
                    EmptyState(modifier = Modifier.fillParentMaxSize())
                }
            } else {
                items(
                    items = habitListItems,
                    key = { it.habit.id }
                ) { item ->
                    // ИСПРАВЛЕНИЕ 1: Используем rememberSwipeToDismissBoxState вместо rememberDismissState
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissValue ->
                            when (dismissValue) {
                                // ИСПРАВЛЕНИЕ 2: Используем SwipeToDismissBoxValue
                                SwipeToDismissBoxValue.StartToEnd -> { // Свайп вправо (Удалить)
                                    viewModel.onEvent(HabitListEvent.OnDeleteHabit(item.habit))
                                    true
                                }
                                SwipeToDismissBoxValue.EndToStart -> { // Свайп влево (Пропустить)
                                    viewModel.onEvent(HabitListEvent.OnSkipHabit(item.habit))
                                    false
                                }
                                SwipeToDismissBoxValue.Settled -> false // Settled вместо Default
                            }
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        modifier = Modifier.padding(vertical = 4.dp),
                        backgroundContent = {
                            SwipeBackground(dismissState = dismissState)
                        }
                    ) {
                        MaterialHabitItem(
                            habit = item.habit,
                            streak = item.currentStreak,
                            isDone = item.isDoneToday,
                            onHabitClick = {
                                // Убедитесь, что Routes импортирован
                                navController.navigate("${Routes.HABIT_DETAIL}/${item.habit.id}")
                            },
                            onCheckClick = {
                                viewModel.onEvent(HabitListEvent.OnHabitCheckChanged(item.habit))
                            },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            // Убедитесь, что Routes импортирован
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

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// --- ОСТАЛЬНЫЕ ФУНКЦИИ ---

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Привычек пока нет",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Нажмите на '+' чтобы добавить новую привычку.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// ИСПРАВЛЕНИЕ 3: Тип параметра изменен на SwipeToDismissBoxState
fun SwipeBackground(dismissState: SwipeToDismissBoxState) {
    val direction = dismissState.dismissDirection
    val color = when (direction) {
        SwipeToDismissBoxValue.StartToEnd -> Color.Red.copy(alpha = 0.8f)
        SwipeToDismissBoxValue.EndToStart -> Color.Yellow.copy(alpha = 0.8f)
        SwipeToDismissBoxValue.Settled -> Color.Transparent
    }
    val alignment = when (direction) {
        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
        SwipeToDismissBoxValue.Settled -> Alignment.Center
    }
    val icon = when (direction) {
        SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Delete
        SwipeToDismissBoxValue.EndToStart -> Icons.Default.Close
        SwipeToDismissBoxValue.Settled -> null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = 16.dp),
        contentAlignment = alignment
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Black
            )
        }
    }
}