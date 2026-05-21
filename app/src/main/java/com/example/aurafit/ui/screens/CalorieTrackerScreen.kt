package com.example.aurafit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.aurafit.data.CalorieLog
import com.example.aurafit.ui.components.AuraGlassCard
import com.example.aurafit.ui.components.AuraGradientButton
import com.example.aurafit.ui.main.MainScreenViewModel
import com.example.aurafit.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalorieTrackerScreen(
    viewModel: MainScreenViewModel,
    modifier: Modifier = Modifier
) {
    val logs by viewModel.todayCalorieLogs.collectAsState()
    val todayCalories by viewModel.todayTotalCalories.collectAsState()
    val targetCalories by viewModel.calorieTarget.collectAsState()
    val todayWater by viewModel.todayTotalWater.collectAsState()

    var showFoodDialog by remember { mutableStateOf(false) }
    var showWaterDialog by remember { mutableStateOf(false) }

    // Calculate total macros
    val totalProtein = logs.sumOf { it.protein.toDouble() }.toFloat()
    val totalCarbs = logs.sumOf { it.carbs.toDouble() }.toFloat()
    val totalFat = logs.sumOf { it.fat.toDouble() }.toFloat()

    // Targets (generic for demonstration, can be dynamic later)
    val proteinTarget = 150f
    val carbsTarget = 250f
    val fatTarget = 70f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Nutrition & Water", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Fuel your body correctly", color = TextSecondary, fontSize = 14.sp)
        }

        // Calorie and Macro summary
        item {
            AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
                Text("DAILY MACROS", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("$todayCalories", color = TextPrimary, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                        Text("consumed / $targetCalories kcal", color = TextMuted, fontSize = 12.sp)
                    }
                    AuraGradientButton(onClick = { showFoodDialog = true }) {
                        Text("Add Food", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Macros progress bars
                MacroBar("Protein", totalProtein, proteinTarget, AuraPink)
                Spacer(modifier = Modifier.height(8.dp))
                MacroBar("Carbs", totalCarbs, carbsTarget, AuraCyan)
                Spacer(modifier = Modifier.height(8.dp))
                MacroBar("Fat", totalFat, fatTarget, AuraOrange)
            }
        }

        // Water Hydration Tracker
        item {
            AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
                Text("HYDRATION", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("$todayWater mL", color = AuraCyan, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Text("Goal: 2500 mL", color = TextMuted, fontSize = 12.sp)
                    }
                    Row {
                        TextButton(
                            onClick = { viewModel.logWater(250) },
                            colors = ButtonDefaults.textButtonColors(contentColor = AuraCyan)
                        ) {
                            Text("+250ml")
                        }
                        TextButton(
                            onClick = { viewModel.logWater(500) },
                            colors = ButtonDefaults.textButtonColors(contentColor = AuraCyan)
                        ) {
                            Text("+500ml")
                        }
                        IconButton(onClick = { showWaterDialog = true }) {
                            Text("+", color = AuraCyan, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = (todayWater.toFloat() / 2500f).coerceIn(0f, 1f),
                    modifier = Modifier.fillMaxWidth().height(6.dp),
                    color = AuraCyan,
                    trackColor = Color(0x1AFFFFFF)
                )
            }
        }

        // Logs Header
        item {
            Text("Logged Today", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
                    Text("No food logged yet today.", color = TextMuted, fontSize = 14.sp)
                }
            }
        } else {
            items(logs) { log ->
                CalorieLogItem(log = log, onDelete = { viewModel.deleteFood(log) })
            }
        }
    }

    // Add Food Dialog
    if (showFoodDialog) {
        var name by remember { mutableStateOf("") }
        var caloriesStr by remember { mutableStateOf("") }
        var proteinStr by remember { mutableStateOf("") }
        var carbsStr by remember { mutableStateOf("") }
        var fatStr by remember { mutableStateOf("") }
        var mealType by remember { mutableStateOf("Breakfast") }

        AlertDialog(
            onDismissRequest = { showFoodDialog = false },
            title = { Text("Add Food Item", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Food Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextField(
                            value = caloriesStr,
                            onValueChange = { caloriesStr = it },
                            label = { Text("Calories") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        TextField(
                            value = proteinStr,
                            onValueChange = { proteinStr = it },
                            label = { Text("Protein (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextField(
                            value = carbsStr,
                            onValueChange = { carbsStr = it },
                            label = { Text("Carbs (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        TextField(
                            value = fatStr,
                            onValueChange = { fatStr = it },
                            label = { Text("Fat (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    // Meal Type selector
                    Text("Meal Type", color = TextSecondary, fontSize = 14.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("Breakfast", "Lunch", "Dinner", "Snack").forEach { type ->
                            FilterChip(
                                selected = mealType == type,
                                onClick = { mealType = type },
                                label = { Text(type, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val c = caloriesStr.toIntOrNull() ?: 0
                        val p = proteinStr.toFloatOrNull() ?: 0f
                        val cb = carbsStr.toFloatOrNull() ?: 0f
                        val f = fatStr.toFloatOrNull() ?: 0f
                        if (name.isNotBlank()) {
                            viewModel.logFood(name, c, p, cb, f, mealType)
                        }
                        showFoodDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AuraPurple)
                ) {
                    Text("Save", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFoodDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }

    // Custom Water Dialog
    if (showWaterDialog) {
        var waterStr by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showWaterDialog = false },
            title = { Text("Log Custom Water", color = TextPrimary) },
            text = {
                TextField(
                    value = waterStr,
                    onValueChange = { waterStr = it },
                    label = { Text("Amount (mL)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = waterStr.toIntOrNull() ?: 0
                        if (amount > 0) {
                            viewModel.logWater(amount)
                        }
                        showWaterDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AuraCyan)
                ) {
                    Text("Log", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWaterDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
fun MacroBar(label: String, value: Float, target: Float, barColor: Color) {
    val progress = if (target > 0) value / target else 0f
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text("${value.toInt()}g / ${target.toInt()}g", color = TextSecondary, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = progress.coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = barColor,
            trackColor = Color(0x0DFFFFFF)
        )
    }
}

@Composable
fun CalorieLogItem(log: CalorieLog, onDelete: () -> Unit) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(log.foodName, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0x1AFFFFFF), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(log.mealType, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "P: ${log.protein.toInt()}g | C: ${log.carbs.toInt()}g | F: ${log.fat.toInt()}g",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${log.calories} kcal", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
