package com.example.habittracker.ui.add_edit_habit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.repository.HabitRepository
import com.example.habittracker.ui.habit_list.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditHabitViewModel(
    private val repository: HabitRepository
) : ViewModel() {

    // Состояние UI
    var habitName by mutableStateOf("")
        private set

    var habitFrequency by mutableStateOf("Ежедневно") // Значение по умолчанию
        private set

    // Канал для отправки одноразовых событий в UI (навигация, Snackbar)
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: AddEditHabitEvent) {
        when (event) {
            is AddEditHabitEvent.OnNameChange -> {
                habitName = event.name
            }
            is AddEditHabitEvent.OnFrequencyChange -> {
                habitFrequency = event.frequency
            }
            is AddEditHabitEvent.OnSaveHabitClick -> {
                viewModelScope.launch {
                    if (habitName.isBlank()) {
                        // Можно отправить событие для показа Snackbar, если нужно
                        return@launch
                    }
                    val newHabit = Habit(
                        name = habitName,
                        frequency = habitFrequency
                        // Остальные поля получают значения по умолчанию
                    )
                    repository.insertHabit(newHabit)
                    _uiEvent.send(UiEvent.NavigateTo(null)) // null для возврата назад
                }
            }
        }
    }
}

// События, которые UI может отправить в ViewModel
sealed class AddEditHabitEvent {
    data class OnNameChange(val name: String) : AddEditHabitEvent()
    data class OnFrequencyChange(val frequency: String) : AddEditHabitEvent()
    object OnSaveHabitClick : AddEditHabitEvent()
}
