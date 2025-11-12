package com.example.habittracker.ui.habit_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.model.HabitExecution
import com.example.habittracker.data.repository.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Состояние UI для экрана деталей
data class HabitDetailState(
    val habit: Habit? = null,
    val executions: List<HabitExecution> = emptyList()
)

class HabitDetailViewModel(
    private val repository: HabitRepository,
    savedStateHandle: SavedStateHandle // Для получения habitId из аргументов навигации
) : ViewModel() {

    private val habitId: Long = savedStateHandle.get<String>("habitId")?.toLongOrNull() ?: -1

    // StateFlow для хранения состояния экрана
    val state: StateFlow<HabitDetailState> = repository.getExecutionHistoryForHabit(habitId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        .let { executionsFlow ->
            // Объединяем информацию о привычке и ее выполнениях
            val stateFlow = MutableStateFlow(HabitDetailState())
            viewModelScope.launch {
                val habit = repository.getHabitById(habitId)
                stateFlow.value = stateFlow.value.copy(habit = habit)

                executionsFlow.collect { executions ->
                    stateFlow.value = stateFlow.value.copy(executions = executions)
                }
            }
            stateFlow
        }
}