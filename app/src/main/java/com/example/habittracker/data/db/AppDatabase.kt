package com.example.habittracker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.TypeConverters
import androidx.room.RoomDatabase
import com.example.habittracker.data.model.HabitExecution
import com.example.habittracker.data.model.Habit


@Database(
    entities = [Habit::class, HabitExecution::class],
    version = 1,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "habit_tracker_db"
                )
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}