package com.example.aurafit.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aurafit.theme.*

@Composable
fun AuraGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(Color(0x0AFFFFFF))
            .border(1.dp, GlassBorder, RoundedCornerShape(cornerRadius))
            .padding(16.dp)
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun AuraGradientButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val auraGradient = Brush.linearGradient(
        colors = listOf(AuraPurple, AuraPink, AuraBlue, AuraCyan)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (enabled) auraGradient else Brush.linearGradient(colors = listOf(Color(0xFF333333), Color(0xFF555555))))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}

@Composable
fun AuraGradientText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)
) {
    val auraGradient = Brush.linearGradient(
        colors = listOf(AuraCyan, AuraPurple, AuraPink, AuraOrange)
    )
    
    Text(
        text = text,
        modifier = modifier.drawWithContent {
            drawContent()
            drawRect(
                brush = auraGradient,
                blendMode = BlendMode.SrcAtop
            )
        },
        style = style
    )
}

@Composable
fun NeuralAuraCore(
    modifier: Modifier = Modifier,
    completionRatio: Float = 0.5f,
    size: Dp = 200.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "aura_transition")
    
    // Breathing scale animation
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.93f,
        targetValue = 1.07f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_scale"
    )

    // Shimmer rotation animation
    val rotationOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "aura_rotation"
    )

    val gradientColors = when {
        completionRatio < 0.3f -> listOf(AuraBlue, AuraCyan, AuraBlue)
        completionRatio < 0.7f -> listOf(AuraCyan, AuraPurple, AuraPink)
        else -> listOf(AuraPurple, AuraPink, AuraOrange, AuraMagenta)
    }

    Box(
        modifier = modifier
            .size(size)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        // Glowing background blur layer
        Box(
            modifier = Modifier
                .fillMaxSize(scale)
                .blur(30.dp)
                .drawBehind {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            colors = gradientColors,
                            tileMode = TileMode.Repeated
                        ),
                        alpha = 0.65f
                    )
                }
        )

        // Crisp central sphere layer
        Box(
            modifier = Modifier
                .fillMaxSize(scale * 0.85f)
                .clip(RoundedCornerShape(100.dp))
                .border(2.dp, Color(0x3FFFFFFF), RoundedCornerShape(100.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0x33FFFFFF), Color(0x05FFFFFF)),
                        radius = 280f
                    )
                )
                .drawBehind {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            colors = gradientColors.map { it.copy(alpha = 0.8f) }
                        )
                    )
                }
        )
    }
}
