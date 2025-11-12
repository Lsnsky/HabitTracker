package com.example.habittracker.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.habittracker.data.repository.HabitRepository
import com.example.habittracker.ui.add_edit_habit.AddEditHabitViewModel
import com.example.habittracker.ui.habit_list.HabitDetailViewModel
import com.example.habittracker.ui.habit_list.HabitListViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras

// Эта фабрика будет создавать наши ViewModel
class ViewModelFactory(private val repository: HabitRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return create(modelClass, CreationExtras.Empty)
    }

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {

        val savedStateHandle = extras.createSavedStateHandle()

        return when {
            modelClass.isAssignableFrom(HabitListViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                HabitListViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AddEditHabitViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                AddEditHabitViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HabitDetailViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                HabitDetailViewModel(repository, savedStateHandle) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
