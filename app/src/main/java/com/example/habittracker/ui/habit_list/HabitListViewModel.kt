package com.example.habittracker.ui.habit_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.model.HabitExecution
import com.example.habittracker.data.repository.HabitRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

sealed interface HabitListEvent{
    data class OnHabitCheckChanged(val habit: Habit): HabitListEvent
}
data class HabitListItem(
    val habit: Habit,
    val isDoneToday: Boolean,
    val currentStreak: Int
)

sealed class UiEvent{
    data class ShowSnackbar(val message: String): UiEvent()
    data class NavigateTo(val route: String?): UiEvent()
}

class HabitListViewModel(
    private val repository: HabitRepository
) : ViewModel() {
    val habits: StateFlow<List<HabitListItem>> = repository.getHabitsWithExecutions()
        .map{ habitsWithExecutions ->
            habitsWithExecutions.map { item ->
                val today = LocalDate.now()
                val isDoneToday = item.executions.any{execution ->
                    val executionDate = Instant.ofEpochMilli(execution.executionDate)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    executionDate.isEqual(today) && execution.isDone
                }
                val currentStreak = calculateStreakFromExecutions(item.executions)

                HabitListItem(
                    habit = item.habit,
                    isDoneToday = isDoneToday,
                    currentStreak = currentStreak
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onEvent(event: HabitListEvent){
        when(event){
            is HabitListEvent.OnHabitCheckChanged -> {
                viewModelScope.launch {
                    repository.toggleHabitExecution(event.habit.id, LocalDate.now())
                    _uiEvent.emit(UiEvent.ShowSnackbar("Habit ${event.habit.name} has been updated"))
                }
            }
        }
    }
}

private fun calculateStreakFromExecutions(executions: List<HabitExecution>): Int{
    val sortedDoneExecutions = executions
        .filter{it.isDone}
        .sortedByDescending { it.executionDate }
    if(sortedDoneExecutions.isEmpty()) return 0
    var streak = 0
    val today = LocalDate.now(ZoneId.systemDefault())
    var expectedDate = today

    val mostRecentExecutionDate = Instant.ofEpochMilli(sortedDoneExecutions.first().executionDate)
        .atZone(ZoneId.systemDefault()).toLocalDate()

    if (!mostRecentExecutionDate.isEqual(today) && !mostRecentExecutionDate.isEqual(today.minusDays(1))) {
        return 0
    }

    if (mostRecentExecutionDate.isEqual(today.minusDays(1))) {
        expectedDate = today.minusDays(1)
    }

    for (execution in sortedDoneExecutions) {
        val executionDate = Instant.ofEpochMilli(execution.executionDate)
            .atZone(ZoneId.systemDefault()).toLocalDate()
        if (executionDate.isEqual(expectedDate)) {
            streak++
            expectedDate = expectedDate.minusDays(1)
        } else if (executionDate.isBefore(expectedDate)) {
            break
        }
    }
    return streak
}