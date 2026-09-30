package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DualBetStrategy
import com.example.data.model.PredictionResult
import com.example.data.model.VolatilityLevel
import com.example.ui.theme.AviatorRed
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DangerRed
import com.example.ui.theme.JetXGold
import com.example.ui.theme.MultiplierBlue
import com.example.ui.theme.MultiplierGreen
import com.example.ui.theme.MultiplierPurple
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.WarningOrange
import java.util.Locale

@Composable
fun PredictionSummaryHeader(
    result: PredictionResult,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (result.isAiPowered) Icons.Default.AutoAwesome else Icons.AutoMirrored.Filled.ShowChart,
                        contentDescription = null,
                        tint = if (result.isAiPowered) CyanNeon else AviatorRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = result.gameDetected.displayName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }

                // AI / Heuristic Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (result.isAiPowered) Color(0x3300E5FF) else Color(0x33FBBF24))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (result.isAiPowered) "IA Gemini 3.5 Flash" else "Modèle Mathématique",
                        color = if (result.isAiPowered) CyanNeon else JetXGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4 Mini Indicators (Confiance, Volatilité, Moyenne, Risque 1.00x)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MiniMetric(
                    label = "Indice Confiance",
                    value = "${result.confidenceScore}%",
                    tint = if (result.confidenceScore >= 80) MultiplierGreen else WarningOrange
                )
                MiniMetric(
                    label = "Volatilité",
                    value = result.volatility.label,
                    tint = when (result.volatility) {
                        VolatilityLevel.LOW -> MultiplierBlue
                        VolatilityLevel.MEDIUM -> JetXGold
                        VolatilityLevel.HIGH -> WarningOrange
                        VolatilityLevel.EXTREME -> DangerRed
                    }
                )
                MiniMetric(
                    label = "Médiane Séquence",
                    value = String.format(Locale.US, "%.2fx", result.medianMultiplier),
                    tint = MultiplierPurple
                )
                MiniMetric(
                    label = "Risque 1.00x",
                    value = "${result.instantCrashRisk}%",
                    tint = DangerRed
                )
            }
        }
    }
}

@Composable
fun MiniMetric(
    label: String,
    value: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color(0xFF94A3B8)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = tint,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun CashoutTargetsGrid(
    result: PredictionResult,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TargetCashoutCard(
            title = "CIBLE SÉCURISÉE (SAFE)",
            multiplier = result.safeCashout,
            probability = result.safeProbability,
            badgeColor = MultiplierGreen,
            icon = Icons.Default.Security,
            description = "Taux de réussite élevé. Idéal pour capitaliser avec constance."
        )

        TargetCashoutCard(
            title = "CIBLE ÉQUILIBRÉE (MODÉRÉE)",
            multiplier = result.balancedCashout,
            probability = result.balancedProbability,
            badgeColor = CyanNeon,
            icon = Icons.Default.Speed,
            description = "Ratio gain/risque optimal calculé sur la dispersion de la séquence."
        )

        TargetCashoutCard(
            title = "CIBLE AUDACIEUSE (HAUTE COTE)",
            multiplier = result.highRiskCashout,
            probability = result.highRiskProbability,
            badgeColor = AviatorRed,
            icon = Icons.Default.Warning,
            description = "Probabilité plus faible. À jouer en mise secondaire (petite fraction)."
        )
    }
}

@Composable
fun TargetCashoutCard(
    title: String,
    multiplier: Double,
    probability: Double,
    badgeColor: Color,
    icon: ImageVector,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(badgeColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFCBD5E1)
                    )
                }

                Text(
                    text = String.format(Locale.US, "%.2fx", multiplier),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = badgeColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Probability progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Probabilité mathématique P(≥ ${String.format(Locale.US, "%.2fx", multiplier)})",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
                Text(
                    text = "$probability%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { (probability / 100f).toFloat().coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = badgeColor,
                trackColor = Color(0x33FFFFFF)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = description,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}

@Composable
fun DualBetStrategyCard(
    strategy: DualBetStrategy,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2A)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ShowChart,
                    contentDescription = null,
                    tint = JetXGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "STRATÉGIE DOUBLE MISE RECOMMANDÉE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = JetXGold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Bet 1 Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E2738))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "MISE 1 (${strategy.bet1RatioPercent}%)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MultiplierGreen
                        )
                        Text(
                            text = "Auto-Cashout",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = String.format(Locale.US, "%.2fx", strategy.bet1Cashout),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White
                        )
                        Text(
                            text = "Couvre 100% des 2 mises",
                            fontSize = 9.sp,
                            color = MultiplierGreen
                        )
                    }
                }

                // Bet 2 Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E2738))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "MISE 2 (${strategy.bet2RatioPercent}%)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanNeon
                        )
                        Text(
                            text = "Cible Profit",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = String.format(Locale.US, "%.2fx", strategy.bet2Cashout),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White
                        )
                        Text(
                            text = "Gain libre sans risque",
                            fontSize = 9.sp,
                            color = CyanNeon
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = strategy.explanation,
                fontSize = 11.sp,
                color = Color(0xFFCBD5E1),
                lineHeight = 16.sp
            )
        }
    }
}
