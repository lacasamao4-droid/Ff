package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AviatorRed
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.MultiplierGreen
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import java.util.Locale
import kotlin.math.pow

@Composable
fun FlightRadarAnimation(
    currentMultiplier: Double,
    isFlying: Boolean,
    isCrashed: Boolean,
    hasCashedOut: Boolean,
    cashedOutMultiplier: Double,
    flightProgress: Float, // 0.0 to 1.0 representing flight curve
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Draw subtle background radar grid
            val gridColor = Color(0x1AFFFFFF)
            val stepX = width / 6
            val stepY = height / 4

            for (i in 1..5) {
                drawLine(
                    color = gridColor,
                    start = Offset(stepX * i, 0f),
                    end = Offset(stepX * i, height),
                    strokeWidth = 1f
                )
            }
            for (j in 1..3) {
                drawLine(
                    color = gridColor,
                    start = Offset(0f, stepY * j),
                    end = Offset(width, stepY * j),
                    strokeWidth = 1f
                )
            }

            // 2. Draw flight curve
            val curvePath = Path()
            val startX = 24.dp.toPx()
            val startY = height - 24.dp.toPx()
            curvePath.moveTo(startX, startY)

            val clampedProgress = flightProgress.coerceIn(0.01f, 1.0f)
            val currentTipX = startX + (width - startX - 40.dp.toPx()) * clampedProgress
            // Parabolic lift curve: y lifts exponentially
            val liftRatio = clampedProgress.toDouble().pow(1.6).toFloat()
            val currentTipY = startY - (height - 60.dp.toPx()) * liftRatio

            // Sample points along the trajectory
            val pointsCount = 40
            for (step in 1..pointsCount) {
                val t = (step.toFloat() / pointsCount) * clampedProgress
                val px = startX + (width - startX - 40.dp.toPx()) * t
                val py = startY - (height - 60.dp.toPx()) * (t.toDouble().pow(1.6).toFloat())
                curvePath.lineTo(px, py)
            }

            val strokeBrush = when {
                isCrashed -> Brush.linearGradient(listOf(Color(0x55FF2A4B), AviatorRed))
                hasCashedOut -> Brush.linearGradient(listOf(Color(0x5510B981), MultiplierGreen))
                else -> Brush.linearGradient(listOf(Color(0x3300E5FF), AviatorRed))
            }

            drawPath(
                path = curvePath,
                brush = strokeBrush,
                style = Stroke(width = 4.dp.toPx())
            )

            // Draw area gradient under curve
            val fillPath = Path().apply {
                addPath(curvePath)
                lineTo(currentTipX, startY)
                close()
            }
            val areaBrush = Brush.verticalGradient(
                colors = listOf(
                    if (isCrashed) Color(0x33FF2A4B) else Color(0x22FF2A4B),
                    Color(0x00FF2A4B)
                ),
                startY = currentTipY,
                endY = startY
            )
            drawPath(fillPath, brush = areaBrush)

            // 3. Draw plane / rocket tip
            if (isFlying || (!isCrashed && flightProgress > 0f)) {
                // Outer glow ring
                drawCircle(
                    color = if (hasCashedOut) MultiplierGreen.copy(alpha = 0.4f) else AviatorRed.copy(alpha = 0.4f),
                    radius = 14.dp.toPx(),
                    center = Offset(currentTipX, currentTipY)
                )
                // Center core
                drawCircle(
                    color = if (hasCashedOut) MultiplierGreen else AviatorRed,
                    radius = 7.dp.toPx(),
                    center = Offset(currentTipX, currentTipY)
                )
            } else if (isCrashed) {
                // Draw crash burst
                drawCircle(
                    color = Color.Red.copy(alpha = 0.7f),
                    radius = 18.dp.toPx(),
                    center = Offset(currentTipX, currentTipY)
                )
                drawCircle(
                    color = Color.Yellow,
                    radius = 8.dp.toPx(),
                    center = Offset(currentTipX, currentTipY)
                )
            }
        }

        // Center HUD Multiplier / Status
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                isCrashed -> {
                    Text(
                        text = "FLEW AWAY!\n${String.format(Locale.US, "%.2fx", currentMultiplier)}",
                        color = AviatorRed,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
                hasCashedOut -> {
                    Text(
                        text = "GAGNÉ !\n${String.format(Locale.US, "%.2fx", cashedOutMultiplier)}",
                        color = MultiplierGreen,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
                isFlying -> {
                    Text(
                        text = String.format(Locale.US, "%.2fx", currentMultiplier),
                        color = Color.White,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
                else -> {
                    Text(
                        text = "EN ATTENTE DU TOUR\n1.00x",
                        color = Color(0xFF94A3B8),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}
