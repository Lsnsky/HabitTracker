package com.example.habittracker.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.habittracker.data.repository.HabitRepository
import com.example.habittracker.ui.add_edit_habit.AddEditHabitViewModel
import com.example.habittracker.ui.habit_list.HabitListViewModel

// Эта фабрика будет создавать наши ViewModel
class ViewModelFactory(private val repository: HabitRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HabitListViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(AddEditHabitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddEditHabitViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
