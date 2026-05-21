package com.example.aurafit.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calorie_logs")
data class CalorieLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val foodName: String,
    val calories: Int,
    val protein: Float, // grams
    val carbs: Float,   // grams
    val fat: Float,     // grams
    val mealType: String, // Breakfast, Lunch, Dinner, Snack
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "workout_logs")
data class WorkoutLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseName: String,
    val sets: Int,
    val reps: Int,
    val weight: Float, // kg or lbs
    val caloriesBurned: Int,
    val dayOfWeek: String, // e.g. "Monday"
    val timeOfDay: String, // e.g. "08:30"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "water_logs")
data class WaterLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amountMl: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "sleep_logs")
data class SleepLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val durationMinutes: Int,
    val qualityRating: Int, // 1 to 5 stars
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "body_metrics")
data class BodyMetric(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weightKg: Float,
    val muscleMassKg: Float?,
    val fatPercentage: Float?,
    val timestamp: Long = System.currentTimeMillis()
)
