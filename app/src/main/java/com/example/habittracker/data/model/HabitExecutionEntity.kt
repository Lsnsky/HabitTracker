package com.example.habittracker.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habit_executions",
    indices = [Index(value = ["habitID", "executionDate"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = Habit::class,
        parentColumns = ["id"],
        childColumns = ["habitID"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class HabitExecution(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,
        val habitId: Long,
        val executionDate: Long,
        val isDone: Boolean
)

