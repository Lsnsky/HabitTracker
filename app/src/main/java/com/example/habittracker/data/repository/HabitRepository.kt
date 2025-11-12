package com.example.habittracker.data.repository

import com.example.habittracker.HabitWithExecutions
import com.example.habittracker.data.db.HabitDao
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.model.HabitExecution
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class HabitRepository(private val habitDao: HabitDao) {
    fun getHabitsWithExecutions(): Flow<List<HabitWithExecutions>> {
        return habitDao.getHabitsWithExecutions()
    }
    suspend fun insertHabit(habit: Habit): Long {
        return habitDao.insertHabit(habit)
    }
    suspend fun updateHabit(habit: Habit) {
        habitDao.updateHabit(habit)
    }
    suspend fun deleteHabit(habit: Habit) {
        habitDao.deleteHabit(habit)
    }
    suspend fun getHabitById(id: Long): Habit? {
        return habitDao.getHabitById(id)
    }
    suspend fun insertExecution(execution: HabitExecution) {
        habitDao.insertExecution(execution)
    }
    fun getExecutionHistoryForHabit(habitId: Long): Flow<List<HabitExecution>> {
        return habitDao.getExecutionHistoryForHabit(habitId)
    }
    suspend fun getExecutionByDate(habitId: Long, dateTimestamp: Long): HabitExecution? {
        return habitDao.getExecutionByDate(habitId, dateTimestamp)
    }

    suspend fun calculateCurrentStreak(habitId: Long): Int {
        val executions = habitDao.getExecutionsForStreakCalculation(habitId)
        if (executions.isEmpty()) {
            return 0
        }
        var streak = 0
        val today = LocalDate.now(ZoneId.systemDefault())
        var expectedDate = today

        val mostRecentExecutionDate = Instant.ofEpochMilli(executions.first().executionDate)
            .atZone(ZoneId.systemDefault()).toLocalDate()

        if(mostRecentExecutionDate != today && mostRecentExecutionDate != today.minusDays(1)){
            return 0
        }
        if(mostRecentExecutionDate != today){
            expectedDate = today.minusDays(1)
        }
        for (execution in executions) {
            val executionDate = Instant.ofEpochMilli(execution.executionDate)
                .atZone(ZoneId.systemDefault()).toLocalDate()
            if (executionDate.isEqual(expectedDate)) {
                if(execution.isDone){
                    streak++
                    expectedDate = expectedDate.minusDays(1)
                } else {
                    break
                }
            }
            else if(executionDate.isBefore(expectedDate)){
                break
            }
        }
        return streak
    }

    suspend fun toggleHabitExecution(habitId: Long, date: LocalDate) {
        val dateTimestamp = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val existingExecution = habitDao.getExecutionByDate(habitId, dateTimestamp)

        if (existingExecution == null) {
            val newExecution = HabitExecution(
                habitID = habitId,
                executionDate = dateTimestamp,
                isDone = true
            )
            insertExecution(newExecution)
        } else {
            val updateExecution = existingExecution.copy(isDone = !existingExecution.isDone)
            insertExecution(updateExecution)
        }
    }

    suspend fun deleteExecutionsForHabit(habitId: Long) {
        habitDao.deleteExecutionsForHabit(habitId)
    }
    fun getHabitWithStreak(habitId:Long):Flow<Int>{
        return kotlinx.coroutines.flow.flowOf(0)
    }
    fun isHabitLimitExceeded():Flow<Boolean>{
        return getHabitsWithExecutions().map { habits ->
            habits.size >= 3
        }
    }
}


