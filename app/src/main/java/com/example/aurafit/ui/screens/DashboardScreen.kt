package com.example.aurafit.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aurafit.ui.components.AuraGlassCard
import com.example.aurafit.ui.components.AuraGradientText
import com.example.aurafit.ui.components.NeuralAuraCore
import com.example.aurafit.ui.main.MainScreenViewModel
import com.example.aurafit.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: MainScreenViewModel,
    onTabSelect: (Int) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val todayCalories by viewModel.todayTotalCalories.collectAsState()
    val targetCalories by viewModel.calorieTarget.collectAsState()
    val todayBurned by viewModel.todayTotalCaloriesBurned.collectAsState()
    val todayWater by viewModel.todayTotalWater.collectAsState()
    val todaySleepLog by viewModel.todaySleepLog.collectAsState()
    val latestBodyMetric by viewModel.latestBodyMetric.collectAsState()

    var showSleepDialog by remember { mutableStateOf(false) }
    var showWeightDialog by remember { mutableStateOf(false) }

    val remainingCalories = targetCalories - todayCalories + todayBurned
    val calorieProgress = if (targetCalories > 0) todayCalories.toFloat() / targetCalories.toFloat() else 0f
    
    // Overall completion ratio for the Aura Core (average of calories, water, sleep goals)
    val waterProgress = (todayWater.toFloat() / 2500f).coerceIn(0f, 1f)
    val sleepProgress = (if (todaySleepLog != null) todaySleepLog!!.durationMinutes.toFloat() / 480f else 0f).coerceIn(0f, 1f)
    val foodProgress = (todayCalories.toFloat() / targetCalories.toFloat()).coerceIn(0f, 1f)
    val auraCompletionRatio = (foodProgress + waterProgress + sleepProgress) / 3f

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AuraGradientText("AuraFit")
            IconButton(onClick = onOpenSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Glowing Pulsating Aura
        NeuralAuraCore(
            completionRatio = auraCompletionRatio,
            size = 200.dp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Today's Aura Completion: ${(auraCompletionRatio * 100).toInt()}%",
            color = TextSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Calories Remaining Summary
        AuraGlassCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("CALORIE BALANCE", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text("$remainingCalories", color = TextPrimary, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                    Text("kcal remaining", color = TextMuted, fontSize = 12.sp)
                }
                Text("Budget: $targetCalories | Burned: +$todayBurned", color = TextSecondary, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = calorieProgress.coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = AuraPurple,
                trackColor = Color(0x1AFFFFFF)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grid of widgets (2x2)
        Row(modifier = Modifier.fillMaxWidth()) {
            // Calorie Card
            AuraGlassCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelect(1) }
            ) {
                Text("FOOD INTAKE", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("$todayCalories", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("/ $targetCalories kcal", color = TextMuted, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Workouts Card
            AuraGlassCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelect(2) }
            ) {
                Text("GYM BURN", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("+$todayBurned", color = SecondaryDark, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("kcal burned", color = TextMuted, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            // Water Card
            AuraGlassCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelect(1) } // Water is logged in Calorie & Water tab
            ) {
                Text("HYDRATION", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("$todayWater", color = AuraCyan, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("/ 2500 mL", color = TextMuted, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Sleep Card
            AuraGlassCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable { showSleepDialog = true }
            ) {
                Text("SLEEP", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                val sleepHours = if (todaySleepLog != null) todaySleepLog!!.durationMinutes / 60.0 else 0.0
                Text(String.format("%.1fh", sleepHours), color = AuraBlue, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(if (todaySleepLog != null) "Quality: ${todaySleepLog!!.qualityRating}/5" else "Tap to log", color = TextMuted, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Body Fitness Updates Card
        AuraGlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showWeightDialog = true }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("BODY METRICS", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    if (latestBodyMetric != null) {
                        Text("${latestBodyMetric!!.weightKg} kg", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        val muscle = latestBodyMetric!!.muscleMassKg?.let { "${it}kg muscle" } ?: ""
                        val fat = latestBodyMetric!!.fatPercentage?.let { "${it}% fat" } ?: ""
                        Text(listOf(muscle, fat).filter { it.isNotEmpty() }.joinToString(" | "), color = TextMuted, fontSize = 12.sp)
                    } else {
                        Text("No metrics logged", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text("Tap to record weight, muscle mass, or fat %", color = TextMuted, fontSize = 12.sp)
                    }
                }
                Text("LOG +", color = AuraPink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Sleep logging dialog
    if (showSleepDialog) {
        var durationHrsStr by remember { mutableStateOf("") }
        var durationMinsStr by remember { mutableStateOf("") }
        var quality by remember { mutableStateOf(3f) } // default 3 stars

        AlertDialog(
            onDismissRequest = { showSleepDialog = false },
            title = { Text("Log Sleep", color = TextPrimary) },
            text = {
                Column {
                    Text("Duration:", color = TextSecondary, fontSize = 14.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        TextField(
                            value = durationHrsStr,
                            onValueChange = { durationHrsStr = it },
                            label = { Text("Hours") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TextField(
                            value = durationMinsStr,
                            onValueChange = { durationMinsStr = it },
                            label = { Text("Mins") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Quality: ${quality.toInt()} / 5 stars", color = TextSecondary, fontSize = 14.sp)
                    Slider(
                        value = quality,
                        onValueChange = { quality = it },
                        valueRange = 1f..5f,
                        steps = 3,
                        colors = SliderDefaults.colors(
                            thumbColor = AuraBlue,
                            activeTrackColor = AuraBlue
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val hrs = durationHrsStr.toIntOrNull() ?: 0
                        val mins = durationMinsStr.toIntOrNull() ?: 0
                        if (hrs > 0 || mins > 0) {
                            viewModel.logSleep(hrs * 60 + mins, quality.toInt())
                        }
                        showSleepDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AuraBlue)
                ) {
                    Text("Save", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSleepDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }

    // Weight/Body metrics logging dialog
    if (showWeightDialog) {
        var weightStr by remember { mutableStateOf(latestBodyMetric?.weightKg?.toString() ?: "") }
        var muscleStr by remember { mutableStateOf(latestBodyMetric?.muscleMassKg?.toString() ?: "") }
        var fatStr by remember { mutableStateOf(latestBodyMetric?.fatPercentage?.toString() ?: "") }

        AlertDialog(
            onDismissRequest = { showWeightDialog = false },
            title = { Text("Log Body Metrics", color = TextPrimary) },
            text = {
                Column {
                    TextField(
                        value = weightStr,
                        onValueChange = { weightStr = it },
                        label = { Text("Weight (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = muscleStr,
                        onValueChange = { muscleStr = it },
                        label = { Text("Muscle Mass (kg - Optional)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = fatStr,
                        onValueChange = { fatStr = it },
                        label = { Text("Fat Percentage (% - Optional)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val w = weightStr.toFloatOrNull()
                        val m = muscleStr.toFloatOrNull()
                        val f = fatStr.toFloatOrNull()
                        if (w != null) {
                            viewModel.logBodyMetric(w, m, f)
                        }
                        showWeightDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AuraPink)
                ) {
                    Text("Save", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWeightDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}
