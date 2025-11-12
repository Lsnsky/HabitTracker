package com.example.habittracker

import com.example.habittracker.data.db.HabitDao
import com.example.habittracker.data.model.HabitExecution
import com.example.habittracker.data.repository.HabitRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class HabitRepositoryTest {

    // Мок (подделка) нашего DAO
    private lateinit var habitDao: HabitDao

    // Тестируемый класс
    private lateinit var repository: HabitRepository

    // Функция для создания метки времени
    private fun dateToTimestamp(date: LocalDate): Long {
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    @Before
    fun setUp() {
        // Создаем мок перед каждым тестом
        habitDao = mockk()
        repository = HabitRepository(habitDao)
    }

    @Test
    fun `calculateCurrentStreak returns 0 when no executions`() = runTest {
        // Arrange: Настраиваем мок DAO. Если его попросят список выполнений, он вернет пустой список.
        coEvery { habitDao.getExecutionsForStreakCalculation(any()) } returns emptyList()

        // Act: Вызываем тестируемый метод
        val streak = repository.calculateCurrentStreak(1L)

        // Assert: Проверяем, что результат равен 0
        assertEquals(0, streak)
    }

    @Test
    fun `calculateCurrentStreak returns 1 if habit was done only today`() = runTest {
        val today = LocalDate.now()
        val executions = listOf(
            HabitExecution(
                id = 1,
                habitID = 1L,
                executionDate = dateToTimestamp(today),
                isDone = true
            )
        )
        coEvery { habitDao.getExecutionsForStreakCalculation(1L) } returns executions

        val streak = repository.calculateCurrentStreak(1L)

        assertEquals(1, streak)
    }

    @Test
    fun `calculateCurrentStreak returns correct streak for several consecutive days`() = runTest {
        val today = LocalDate.now()
        val executions = listOf(
            HabitExecution(
                id = 1,
                habitID = 1L,
                executionDate = dateToTimestamp(today),
                isDone = true
            ),
            HabitExecution(
                id = 2,
                habitID = 1L,
                executionDate = dateToTimestamp(today.minusDays(1)),
                isDone = true
            ),
            HabitExecution(
                id = 3,
                habitID = 1L,
                executionDate = dateToTimestamp(today.minusDays(2)),
                isDone = true
            )
        )
        coEvery { habitDao.getExecutionsForStreakCalculation(1L) } returns executions

        val streak = repository.calculateCurrentStreak(1L)

        assertEquals(3, streak)
    }

    @Test
    fun `calculateCurrentStreak returns 0 if last execution was two days ago`() = runTest {
        val today = LocalDate.now()
        val executions = listOf(
            HabitExecution(
                id = 1,
                habitID = 1L,
                executionDate = dateToTimestamp(today.minusDays(2)),
                isDone = true
            ),
            HabitExecution(
                id = 2,
                habitID = 1L,
                executionDate = dateToTimestamp(today.minusDays(3)),
                isDone = true
            )
        )
        coEvery { habitDao.getExecutionsForStreakCalculation(1L) } returns executions

        val streak = repository.calculateCurrentStreak(1L)

        // Серия прервана, поэтому ожидаем 0
        assertEquals(0, streak)
    }

    @Test
    fun `calculateCurrentStreak returns correct streak if streak was broken`() = runTest {
        val today = LocalDate.now()
        // Серия: выполнено сегодня, вчера, но пропущено позавчера.
        val executions = listOf(
            HabitExecution(
                id = 1,
                habitID = 1L,
                executionDate = dateToTimestamp(today),
                isDone = true
            ),
            HabitExecution(
                id = 2,
                habitID = 1L,
                executionDate = dateToTimestamp(today.minusDays(1)),
                isDone = true
            ),
            // Пропуск 2 дня назад
            HabitExecution(
                id = 3,
                habitID = 1L,
                executionDate = dateToTimestamp(today.minusDays(3)),
                isDone = true
            )
        )
        coEvery { habitDao.getExecutionsForStreakCalculation(1L) } returns executions

        val streak = repository.calculateCurrentStreak(1L)

        // Считаем только непрерывную серию с конца
        assertEquals(2, streak)
    }

    @Test
    fun `calculateCurrentStreak works correctly if done yesterday but not today`() = runTest {
        val today = LocalDate.now()
        val executions = listOf(
            HabitExecution(
                id = 1,
                habitID = 1L,
                executionDate = dateToTimestamp(today.minusDays(1)),
                isDone = true
            ),
            HabitExecution(
                id = 2,
                habitID = 1L,
                executionDate = dateToTimestamp(today.minusDays(2)),
                isDone = true
            )
        )
        coEvery { habitDao.getExecutionsForStreakCalculation(1L) } returns executions

        val streak = repository.calculateCurrentStreak(1L)

        assertEquals(2, streak)
    }
}