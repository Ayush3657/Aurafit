package com.example.aurafit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aurafit.data.WorkoutLog
import com.example.aurafit.ui.components.AuraGlassCard
import com.example.aurafit.ui.components.AuraGradientButton
import com.example.aurafit.ui.main.MainScreenViewModel
import com.example.aurafit.theme.*
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(
    viewModel: MainScreenViewModel,
    modifier: Modifier = Modifier
) {
    val logs by viewModel.todayWorkoutLogs.collectAsState()
    val todayBurned by viewModel.todayTotalCaloriesBurned.collectAsState()

    var selectedDay by remember { mutableStateOf(getCurrentDayName()) }
    var showAddDialog by remember { mutableStateOf(false) }

    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    // Custom split workouts dictionary
    val splits = mapOf(
        "Monday" to "Push (Chest, Shoulders, Triceps)",
        "Tuesday" to "Pull (Back, Biceps)",
        "Wednesday" to "Legs & Core",
        "Thursday" to "Active Recovery / Rest",
        "Friday" to "Push (Chest, Shoulders, Triceps)",
        "Saturday" to "Pull (Back, Biceps)",
        "Sunday" to "Legs & Cardio"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Gym Workouts", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Train hard, recover smart", color = TextSecondary, fontSize = 14.sp)
        }

        // Horizontal Week Selector
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(daysOfWeek) { day ->
                    val isSelected = selectedDay == day
                    val isToday = getCurrentDayName() == day
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                when {
                                    isSelected -> AuraBlue
                                    isToday -> Color(0x33FFFFFF)
                                    else -> Color(0x0AFFFFFF)
                                }
                            )
                            .border(
                                1.dp,
                                if (isSelected) AuraBlue else Color(0x1AFFFFFF),
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedDay = day }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.take(3),
                            color = if (isSelected) Color.White else TextPrimary,
                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Selected Day split card
        item {
            AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
                Text("SCHEDULED WORKOUT", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(selectedDay, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(splits[selectedDay] ?: "Rest Day", color = AuraBlue, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Today's Gym Burn", color = TextSecondary, fontSize = 12.sp)
                        Text("$todayBurned kcal", color = SecondaryDark, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    AuraGradientButton(onClick = { showAddDialog = true }) {
                        Text("Log Exercise", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Today's Logs header
        item {
            Text("Today's Exercises", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        // Log history list
        if (logs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No exercises logged yet today.", color = TextMuted, fontSize = 14.sp)
                }
            }
        } else {
            items(logs) { log ->
                WorkoutLogItem(log = log, onDelete = { viewModel.deleteWorkout(log) })
            }
        }
    }

    // Log Exercise Dialog
    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var setsStr by remember { mutableStateOf("") }
        var repsStr by remember { mutableStateOf("") }
        var weightStr by remember { mutableStateOf("") }
        var caloriesStr by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Log Gym Exercise", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Exercise Name (e.g. Bench Press)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextField(
                            value = setsStr,
                            onValueChange = { setsStr = it },
                            label = { Text("Sets") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        TextField(
                            value = repsStr,
                            onValueChange = { repsStr = it },
                            label = { Text("Reps") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextField(
                            value = weightStr,
                            onValueChange = { weightStr = it },
                            label = { Text("Weight (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        TextField(
                            value = caloriesStr,
                            onValueChange = { caloriesStr = it },
                            label = { Text("Est. Burn (kcal)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val s = setsStr.toIntOrNull() ?: 0
                        val r = repsStr.toIntOrNull() ?: 0
                        val w = weightStr.toFloatOrNull() ?: 0f
                        val c = caloriesStr.toIntOrNull() ?: 0
                        if (name.isNotBlank()) {
                            viewModel.logWorkout(
                                name, s, r, w, c,
                                dayOfWeek = getCurrentDayName(),
                                timeOfDay = getCurrentTimeStr()
                            )
                        }
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AuraBlue)
                ) {
                    Text("Save", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
fun WorkoutLogItem(log: WorkoutLog, onDelete: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x05FFFFFF))
            .border(1.dp, Color(0x0AFFFFFF), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(log.exerciseName, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "${log.sets} sets x ${log.reps} reps @ ${log.weight} kg",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
                Text(
                    "Logged at ${log.timeOfDay}",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("-${log.caloriesBurned} kcal", color = SecondaryDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0x80FF5555),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun getCurrentDayName(): String {
    val calendar = Calendar.getInstance()
    val day = calendar.get(Calendar.DAY_OF_WEEK)
    return when (day) {
        Calendar.MONDAY -> "Monday"
        Calendar.TUESDAY -> "Tuesday"
        Calendar.WEDNESDAY -> "Wednesday"
        Calendar.THURSDAY -> "Thursday"
        Calendar.FRIDAY -> "Friday"
        Calendar.SATURDAY -> "Saturday"
        Calendar.SUNDAY -> "Sunday"
        else -> "Monday"
    }
}

private fun getCurrentTimeStr(): String {
    val calendar = Calendar.getInstance()
    val hr = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY))
    val min = String.format("%02d", calendar.get(Calendar.MINUTE))
    return "$hr:$min"
}
