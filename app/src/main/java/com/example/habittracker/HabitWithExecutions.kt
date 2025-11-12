package com.example.habittracker

import androidx.room.Embedded
import androidx.room.Relation
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.model.HabitExecution

data class HabitWithExecutions(
    @Embedded
    val habit: Habit,

    @Relation(
        parentColumn = "id", // Поле из Habit
        entityColumn = "habitID" // Поле из HabitExecution
    )
    val executions: List<HabitExecution>
)