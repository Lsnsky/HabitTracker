package com.example.habittracker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.habittracker.HabitWithExecutions
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.model.HabitExecution
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit): Long

    @Update
    suspend fun updateHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)
    @Transaction
    @Query("SELECT * FROM habits ORDER BY createdDate DESC")
    fun getHabitsWithExecutions(): Flow<List<HabitWithExecutions>>

    @Query("SELECT * FROM habits WHERE Id = :habitId")
    suspend fun getHabitById(habitId: Long): Habit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExecution(execution: HabitExecution)

    @Query("SELECT * FROM habit_executions WHERE habitId = :habitId ORDER BY executionDate DESC")
    fun getExecutionHistoryForHabit(habitId: Long): Flow<List<HabitExecution>>

    @Query("SELECT * FROM habit_executions WHERE habitId = :habitId AND executionDate = :date LIMIT 1")
    suspend fun getExecutionByDate(habitId: Long, date: Long): HabitExecution?

    @Query("SELECT * FROM habit_executions WHERE habitId = :habitId AND isDone = 1 ORDER BY executionDate DESC")
    suspend fun getExecutionsForStreakCalculation(habitId: Long): List<HabitExecution>
    @Query("DELETE FROM habit_executions WHERE habitId = :habitId")
    suspend fun deleteExecutionsForHabit(habitId: Long)
}