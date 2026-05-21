package com.example.aurafit.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CalorieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalorieLog(log: CalorieLog)

    @Delete
    suspend fun deleteCalorieLog(log: CalorieLog)

    @Query("SELECT * FROM calorie_logs ORDER BY timestamp DESC")
    fun getAllCalorieLogs(): Flow<List<CalorieLog>>

    @Query("SELECT * FROM calorie_logs WHERE timestamp >= :startOfDay AND timestamp <= :endOfDay ORDER BY timestamp DESC")
    fun getCalorieLogsForDay(startOfDay: Long, endOfDay: Long): Flow<List<CalorieLog>>

    @Query("SELECT SUM(calories) FROM calorie_logs WHERE timestamp >= :startOfDay AND timestamp <= :endOfDay")
    fun getTodayTotalCalories(startOfDay: Long, endOfDay: Long): Flow<Int?>
}

@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutLog(log: WorkoutLog)

    @Delete
    suspend fun deleteWorkoutLog(log: WorkoutLog)

    @Query("SELECT * FROM workout_logs ORDER BY timestamp DESC")
    fun getAllWorkoutLogs(): Flow<List<WorkoutLog>>

    @Query("SELECT * FROM workout_logs WHERE timestamp >= :startOfDay AND timestamp <= :endOfDay ORDER BY timestamp DESC")
    fun getWorkoutLogsForDay(startOfDay: Long, endOfDay: Long): Flow<List<WorkoutLog>>

    @Query("SELECT SUM(caloriesBurned) FROM workout_logs WHERE timestamp >= :startOfDay AND timestamp <= :endOfDay")
    fun getTodayTotalCaloriesBurned(startOfDay: Long, endOfDay: Long): Flow<Int?>
}

@Dao
interface WaterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterLog(log: WaterLog)

    @Delete
    suspend fun deleteWaterLog(log: WaterLog)

    @Query("SELECT * FROM water_logs WHERE timestamp >= :startOfDay AND timestamp <= :endOfDay ORDER BY timestamp DESC")
    fun getWaterLogsForDay(startOfDay: Long, endOfDay: Long): Flow<List<WaterLog>>

    @Query("SELECT SUM(amountMl) FROM water_logs WHERE timestamp >= :startOfDay AND timestamp <= :endOfDay")
    fun getTodayTotalWater(startOfDay: Long, endOfDay: Long): Flow<Int?>
}

@Dao
interface SleepDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepLog(log: SleepLog)

    @Delete
    suspend fun deleteSleepLog(log: SleepLog)

    @Query("SELECT * FROM sleep_logs ORDER BY timestamp DESC LIMIT 30")
    fun getRecentSleepLogs(): Flow<List<SleepLog>>

    @Query("SELECT * FROM sleep_logs WHERE timestamp >= :startOfDay AND timestamp <= :endOfDay LIMIT 1")
    fun getSleepLogForDay(startOfDay: Long, endOfDay: Long): Flow<SleepLog?>
}

@Dao
interface BodyMetricDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBodyMetric(metric: BodyMetric)

    @Query("SELECT * FROM body_metrics ORDER BY timestamp DESC LIMIT 1")
    fun getLatestBodyMetric(): Flow<BodyMetric?>

    @Query("SELECT * FROM body_metrics ORDER BY timestamp DESC")
    fun getAllBodyMetrics(): Flow<List<BodyMetric>>
}
