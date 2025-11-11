package com.example.habittracker.ui.habit_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.repository.HabitRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.ZoneId

data class HabitListItem(
    val habit: Habit,
    val isDoneToday: Boolean,
    val currentStreak: Int
)

sealed class UiEvent{
    data class ShowSnackbar(val message: String): UiEvent()
    data class NavigateTo(val route: String): UiEvent()
}

class HabitListViewModel(
    private val repository: HabitRepository
) : ViewModel() {
    val habits: StateFlow<List<HabitListItem>> = repository.getAllHabits()
        .map{ habits ->
            habits.map { habit->
                val today = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                val isDoneToday = repository.getExecutionByDate(habit.id, today)?.isDone ?: false
                val currentStreak = repository.calculateCurrentStreak(habit.id)
                HabitListItem(
                    habit = habit,
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
}