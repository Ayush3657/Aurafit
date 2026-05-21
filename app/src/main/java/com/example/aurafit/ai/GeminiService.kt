package com.example.aurafit.ai

import android.content.Context
import android.util.Log
import com.example.aurafit.data.CalorieLog
import com.example.aurafit.data.WorkoutLog
import com.example.aurafit.data.SleepLog
import com.example.aurafit.data.BodyMetric
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.util.Properties

class GeminiService(private val context: Context) {

    private val sharedPrefs = context.getSharedPreferences("aurafit_prefs", Context.MODE_PRIVATE)

    fun getApiKey(): String? {
        // 1. Check SharedPreferences first
        val savedKey = sharedPrefs.getString("gemini_api_key", null)
        if (!savedKey.isNullOrBlank()) return savedKey

        // 2. Check assets as fallback
        return try {
            val inputStream: InputStream = context.assets.open("secrets.properties")
            val properties = Properties()
            properties.load(inputStream)
            properties.getProperty("gemini.api.key")
        } catch (e: Exception) {
            null
        }
    }

    fun saveApiKey(key: String) {
        sharedPrefs.edit().putString("gemini_api_key", key).apply()
    }

    suspend fun getCoachingAdvice(
        caloriesToday: Int,
        targetCalories: Int,
        waterTodayMl: Int,
        sleepTodayMinutes: Int,
        workoutsToday: List<WorkoutLog>,
        recentWorkouts: List<WorkoutLog>,
        latestBodyMetric: BodyMetric?,
        userQuestion: String? = null
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isNullOrBlank()) {
            return@withContext getMockAdvice(caloriesToday, targetCalories, waterTodayMl, sleepTodayMinutes, workoutsToday, userQuestion)
        }

        try {
            val generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = apiKey
            )

            val prompt = buildPrompt(
                caloriesToday, targetCalories, waterTodayMl, sleepTodayMinutes, workoutsToday, recentWorkouts, latestBodyMetric, userQuestion
            )

            val response = generativeModel.generateContent(prompt)
            return@withContext response.text ?: "I couldn't generate a response. Please try again."
        } catch (e: Exception) {
            Log.e("GeminiService", "Error calling Gemini API", e)
            return@withContext "Error contacting Gemini: ${e.localizedMessage}. Falling back to offline advice:\n\n" + 
                    getMockAdvice(caloriesToday, targetCalories, waterTodayMl, sleepTodayMinutes, workoutsToday, userQuestion)
        }
    }

    private fun buildPrompt(
        caloriesToday: Int,
        targetCalories: Int,
        waterTodayMl: Int,
        sleepTodayMinutes: Int,
        workoutsToday: List<WorkoutLog>,
        recentWorkouts: List<WorkoutLog>,
        latestBodyMetric: BodyMetric?,
        userQuestion: String?
    ): String {
        val workoutDetails = if (workoutsToday.isEmpty()) "None logged yet." else workoutsToday.joinToString("\n") { 
            "- ${it.exerciseName}: ${it.sets} sets x ${it.reps} reps @ ${it.weight}kg (${it.caloriesBurned} kcal)"
        }
        val recentWorkoutDetails = if (recentWorkouts.isEmpty()) "None recently." else recentWorkouts.take(5).joinToString("\n") { 
            "- ${it.exerciseName} on ${it.dayOfWeek}: ${it.sets}x${it.reps} (${it.caloriesBurned} kcal)"
        }
        val bodyMetricDetails = if (latestBodyMetric == null) "Not recorded." else 
            "Weight: ${latestBodyMetric.weightKg}kg, Muscle: ${latestBodyMetric.muscleMassKg ?: "N/A"}kg, Fat: ${latestBodyMetric.fatPercentage ?: "N/A"}%"

        return """
            You are "Aura", a neural, highly encouraging and premium AI fitness coach integrated directly into the AuraFit app.
            Your response must be styled in a friendly, conversational, concise manner, utilizing clean markdown formatting. 
            
            Here are the user's details for today:
            - Calorie Intake: $caloriesToday kcal (Target: $targetCalories kcal)
            - Water Intake: $waterTodayMl mL (Goal: 2500 mL)
            - Sleep: ${sleepTodayMinutes / 60}h ${sleepTodayMinutes % 60}m (Goal: 8 hours)
            - Workouts Done Today:
            $workoutDetails
            
            Context on user's progress:
            - Recent workouts:
            $recentWorkoutDetails
            - Current Body Metrics:
            $bodyMetricDetails
            
            ${if (!userQuestion.isNullOrBlank()) "The user is asking: \"$userQuestion\"" else "Give a daily progress review, highlighting where they did well, giving suggestions on what to eat, or advising on modifications to their workout routine."}
            
            Be highly encouraging but realistic. Focus on actionable tips. Keep it relatively short (under 250 words). Do not mention that you are a language model.
        """.trimIndent()
    }

    private fun getMockAdvice(
        caloriesToday: Int,
        targetCalories: Int,
        waterTodayMl: Int,
        sleepTodayMinutes: Int,
        workoutsToday: List<WorkoutLog>,
        userQuestion: String?
    ): String {
        val calDiff = targetCalories - caloriesToday
        val waterDiff = 2500 - waterTodayMl
        val sleepHours = sleepTodayMinutes / 60.0

        val sb = StringBuilder()
        sb.append("✨ **Welcome to Aura Coach!** ✨\n\n")
        sb.append("> *Note: Set your Gemini API Key in Settings to enable live neural AI coaching suggestions. Showing simulated advice based on your logs:*\n\n")

        if (userQuestion != null) {
            sb.append("You asked: *\"$userQuestion\"*\n\n")
            sb.append("To answer your question with full personalization, I need your Gemini API Key. Generally speaking, to achieve your fitness goals, maintain a consistent workout split, track macros, and ensure you're sleeping at least 7-8 hours for muscle recovery.")
            return sb.toString()
        }

        // Calorie suggestions
        if (calDiff > 300) {
            sb.append("🍎 **Nutrition:** You have **$calDiff kcal** remaining today. Consider a protein-rich snack like greek yogurt or a protein shake to support muscle recovery.\n\n")
        } else if (calDiff in -100..300) {
            sb.append("🥗 **Nutrition:** Spot on! You are right on track with your calorie target today. Great job managing your meals.\n\n")
        } else {
            sb.append("⚠️ **Nutrition:** You've exceeded your target by **${-calDiff} kcal** today. Don't worry! Keep your next meals clean, focus on fiber/protein, and drink water to stay full.\n\n")
        }

        // Hydration suggestions
        if (waterDiff > 0) {
            sb.append("💧 **Hydration:** You need **$waterDiff mL** more water to meet your daily target. Grab a glass now! Proper hydration boosts fat loss and workout performance.\n\n")
        } else {
            sb.append("🥤 **Hydration:** Fully hydrated! Outstanding job keeping up with your water intake today.\n\n")
        }

        // Sleep suggestions
        if (sleepHours < 7.0) {
            sb.append("💤 **Sleep:** You got **${String.format("%.1f", sleepHours)} hours** of sleep. Muscles repair during deep sleep. Aim for 8 hours tonight, and consider limiting screen time 30 minutes before bed.\n\n")
        } else {
            sb.append("😴 **Sleep:** You slept for **${String.format("%.1f", sleepHours)} hours**. Excellent rest! Your nervous system is primed for training.\n\n")
        }

        // Workout suggestions
        if (workoutsToday.isEmpty()) {
            sb.append("🏋️ **Training:** No workouts logged today. If it's a rest day, focus on active recovery like walking. If it's a training day, try to fit in a quick 30-minute resistance session!")
        } else {
            sb.append("💪 **Training:** Awesome job logging ${workoutsToday.size} exercises today! Your body burned calories during the session; make sure to refuel with adequate amino acids.")
        }

        return sb.toString()
    }
}
