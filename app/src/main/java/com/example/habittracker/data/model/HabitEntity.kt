package com.example.habittracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val name: String,
    val description: String = "",
    val frequency: String,
    val createdDate: Long = System.currentTimeMillis(),
    val isProFeatured: Boolean = false
)