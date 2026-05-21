package com.example.aurafit.data

import kotlinx.coroutines.flow.Flow

interface DataRepository {
    // Calorie logs
    fun getAllCalorieLogs(): Flow<List<CalorieLog>>
    fun getCalorieLogsForDay(startOfDay: Long, endOfDay: Long): Flow<List<CalorieLog>>
    fun getTodayTotalCalories(startOfDay: Long, endOfDay: Long): Flow<Int?>
    suspend fun insertCalorieLog(log: CalorieLog)
    suspend fun deleteCalorieLog(log: CalorieLog)

    // Workout logs
    fun getAllWorkoutLogs(): Flow<List<WorkoutLog>>
    fun getWorkoutLogsForDay(startOfDay: Long, endOfDay: Long): Flow<List<WorkoutLog>>
    fun getTodayTotalCaloriesBurned(startOfDay: Long, endOfDay: Long): Flow<Int?>
    suspend fun insertWorkoutLog(log: WorkoutLog)
    suspend fun deleteWorkoutLog(log: WorkoutLog)

    // Water logs
    fun getWaterLogsForDay(startOfDay: Long, endOfDay: Long): Flow<List<WaterLog>>
    fun getTodayTotalWater(startOfDay: Long, endOfDay: Long): Flow<Int?>
    suspend fun insertWaterLog(log: WaterLog)
    suspend fun deleteWaterLog(log: WaterLog)

    // Sleep logs
    fun getRecentSleepLogs(): Flow<List<SleepLog>>
    fun getSleepLogForDay(startOfDay: Long, endOfDay: Long): Flow<SleepLog?>
    suspend fun insertSleepLog(log: SleepLog)
    suspend fun deleteSleepLog(log: SleepLog)

    // Body metrics
    fun getLatestBodyMetric(): Flow<BodyMetric?>
    fun getAllBodyMetrics(): Flow<List<BodyMetric>>
    suspend fun insertBodyMetric(metric: BodyMetric)
}

class DefaultDataRepository(private val database: AuraDatabase) : DataRepository {
    private val calorieDao = database.calorieDao()
    private val workoutDao = database.workoutDao()
    private val waterDao = database.waterDao()
    private val sleepDao = database.sleepDao()
    private val bodyMetricDao = database.bodyMetricDao()

    override fun getAllCalorieLogs(): Flow<List<CalorieLog>> = calorieDao.getAllCalorieLogs()
    
    override fun getCalorieLogsForDay(startOfDay: Long, endOfDay: Long): Flow<List<CalorieLog>> =
        calorieDao.getCalorieLogsForDay(startOfDay, endOfDay)
        
    override fun getTodayTotalCalories(startOfDay: Long, endOfDay: Long): Flow<Int?> =
        calorieDao.getTodayTotalCalories(startOfDay, endOfDay)
        
    override suspend fun insertCalorieLog(log: CalorieLog) = calorieDao.insertCalorieLog(log)
    
    override suspend fun deleteCalorieLog(log: CalorieLog) = calorieDao.deleteCalorieLog(log)

    override fun getAllWorkoutLogs(): Flow<List<WorkoutLog>> = workoutDao.getAllWorkoutLogs()
    
    override fun getWorkoutLogsForDay(startOfDay: Long, endOfDay: Long): Flow<List<WorkoutLog>> =
        workoutDao.getWorkoutLogsForDay(startOfDay, endOfDay)
        
    override fun getTodayTotalCaloriesBurned(startOfDay: Long, endOfDay: Long): Flow<Int?> =
        workoutDao.getTodayTotalCaloriesBurned(startOfDay, endOfDay)
        
    override suspend fun insertWorkoutLog(log: WorkoutLog) = workoutDao.insertWorkoutLog(log)
    
    override suspend fun deleteWorkoutLog(log: WorkoutLog) = workoutDao.deleteWorkoutLog(log)

    override fun getWaterLogsForDay(startOfDay: Long, endOfDay: Long): Flow<List<WaterLog>> =
        waterDao.getWaterLogsForDay(startOfDay, endOfDay)
        
    override fun getTodayTotalWater(startOfDay: Long, endOfDay: Long): Flow<Int?> =
        waterDao.getTodayTotalWater(startOfDay, endOfDay)
        
    override suspend fun insertWaterLog(log: WaterLog) = waterDao.insertWaterLog(log)
    
    override suspend fun deleteWaterLog(log: WaterLog) = waterDao.deleteWaterLog(log)

    override fun getRecentSleepLogs(): Flow<List<SleepLog>> = sleepDao.getRecentSleepLogs()
    
    override fun getSleepLogForDay(startOfDay: Long, endOfDay: Long): Flow<SleepLog?> =
        sleepDao.getSleepLogForDay(startOfDay, endOfDay)
        
    override suspend fun insertSleepLog(log: SleepLog) = sleepDao.insertSleepLog(log)
    
    override suspend fun deleteSleepLog(log: SleepLog) = sleepDao.deleteSleepLog(log)

    override fun getLatestBodyMetric(): Flow<BodyMetric?> = bodyMetricDao.getLatestBodyMetric()
    
    override fun getAllBodyMetrics(): Flow<List<BodyMetric>> = bodyMetricDao.getAllBodyMetrics()
    
    override suspend fun insertBodyMetric(metric: BodyMetric) = bodyMetricDao.insertBodyMetric(metric)
}
