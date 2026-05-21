package com.example.aurafit.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aurafit.ai.GeminiService
import com.example.aurafit.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class MainScreenViewModel(
    private val repository: DataRepository,
    private val geminiService: GeminiService
) : ViewModel() {

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(ChatMessage("Hi there! I'm Aura, your AI coach. How can I help you today?", isUser = false))
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

    private val _apiKey = MutableStateFlow(geminiService.getApiKey() ?: "")
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    val calorieTarget = MutableStateFlow(2200) // Default target, customizable

    // Today's boundaries
    private val startOfDay = getStartOfDay()
    private val endOfDay = getEndOfDay()

    val todayCalorieLogs = repository.getCalorieLogsForDay(startOfDay, endOfDay)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayTotalCalories = repository.getTodayTotalCalories(startOfDay, endOfDay)
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todayWorkoutLogs = repository.getWorkoutLogsForDay(startOfDay, endOfDay)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayTotalCaloriesBurned = repository.getTodayTotalCaloriesBurned(startOfDay, endOfDay)
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todayTotalWater = repository.getTodayTotalWater(startOfDay, endOfDay)
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todaySleepLog = repository.getSleepLogForDay(startOfDay, endOfDay)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val latestBodyMetric = repository.getLatestBodyMetric()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allBodyMetrics = repository.getAllBodyMetrics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveApiKey(key: String) {
        geminiService.saveApiKey(key)
        _apiKey.value = key
    }

    fun logFood(name: String, calories: Int, protein: Float, carbs: Float, fat: Float, mealType: String) {
        viewModelScope.launch {
            repository.insertCalorieLog(
                CalorieLog(
                    foodName = name,
                    calories = calories,
                    protein = protein,
                    carbs = carbs,
                    fat = fat,
                    mealType = mealType
                )
            )
        }
    }

    fun deleteFood(log: CalorieLog) {
        viewModelScope.launch {
            repository.deleteCalorieLog(log)
        }
    }

    fun logWorkout(name: String, sets: Int, reps: Int, weight: Float, caloriesBurned: Int, dayOfWeek: String, timeOfDay: String) {
        viewModelScope.launch {
            repository.insertWorkoutLog(
                WorkoutLog(
                    exerciseName = name,
                    sets = sets,
                    reps = reps,
                    weight = weight,
                    caloriesBurned = caloriesBurned,
                    dayOfWeek = dayOfWeek,
                    timeOfDay = timeOfDay
                )
            )
        }
    }

    fun deleteWorkout(log: WorkoutLog) {
        viewModelScope.launch {
            repository.deleteWorkoutLog(log)
        }
    }

    fun logWater(amountMl: Int) {
        viewModelScope.launch {
            repository.insertWaterLog(WaterLog(amountMl = amountMl))
        }
    }

    fun logSleep(durationMinutes: Int, qualityRating: Int) {
        viewModelScope.launch {
            repository.insertSleepLog(
                SleepLog(
                    durationMinutes = durationMinutes,
                    qualityRating = qualityRating
                )
            )
        }
    }

    fun logBodyMetric(weightKg: Float, muscleMassKg: Float?, fatPercentage: Float?) {
        viewModelScope.launch {
            repository.insertBodyMetric(
                BodyMetric(
                    weightKg = weightKg,
                    muscleMassKg = muscleMassKg,
                    fatPercentage = fatPercentage
                )
            )
        }
    }

    fun askGemini(question: String) {
        if (question.isBlank()) return
        
        viewModelScope.launch {
            // Add user message
            _chatMessages.update { it + ChatMessage(question, isUser = true) }
            _aiLoading.value = true

            // Gather context
            val calories = todayTotalCalories.value
            val target = calorieTarget.value
            val water = todayTotalWater.value
            val sleep = todaySleepLog.value?.durationMinutes ?: 0
            val todayWorkouts = todayWorkoutLogs.value
            val recentWorkouts = repository.getAllWorkoutLogs().firstOrNull() ?: emptyList()
            val metric = latestBodyMetric.value

            // Call API
            val reply = geminiService.getCoachingAdvice(
                caloriesToday = calories,
                targetCalories = target,
                waterTodayMl = water,
                sleepTodayMinutes = sleep,
                workoutsToday = todayWorkouts,
                recentWorkouts = recentWorkouts,
                latestBodyMetric = metric,
                userQuestion = question
            )

            _aiLoading.value = false
            _chatMessages.update { it + ChatMessage(reply, isUser = false) }
        }
    }

    fun getDailySummaryCoaching() {
        viewModelScope.launch {
            _aiLoading.value = true
            val calories = todayTotalCalories.value
            val target = calorieTarget.value
            val water = todayTotalWater.value
            val sleep = todaySleepLog.value?.durationMinutes ?: 0
            val todayWorkouts = todayWorkoutLogs.value
            val recentWorkouts = repository.getAllWorkoutLogs().firstOrNull() ?: emptyList()
            val metric = latestBodyMetric.value

            val reply = geminiService.getCoachingAdvice(
                caloriesToday = calories,
                targetCalories = target,
                waterTodayMl = water,
                sleepTodayMinutes = sleep,
                workoutsToday = todayWorkouts,
                recentWorkouts = recentWorkouts,
                latestBodyMetric = metric,
                userQuestion = null // Trigger daily summary review
            )

            _aiLoading.value = false
            _chatMessages.update { it + ChatMessage(reply, isUser = false) }
        }
    }

    // Date boundary helpers
    private fun getStartOfDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getEndOfDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
}
