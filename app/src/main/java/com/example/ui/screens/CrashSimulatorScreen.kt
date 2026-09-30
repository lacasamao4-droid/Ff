package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.calculator.CrashMathEngine
import com.example.ui.components.FlightRadarAnimation
import com.example.ui.theme.AviatorRed
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.JetXGold
import com.example.ui.theme.MultiplierGreen
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CrashPredictorViewModel
import java.util.Locale

@Composable
fun CrashSimulatorScreen(
    viewModel: CrashPredictorViewModel,
    modifier: Modifier = Modifier
) {
    val simState by viewModel.simState.collectAsState()
    var autoCashoutText by remember { mutableStateOf(simState.autoCashoutTarget.toString()) }

    val liveProbability = if (simState.isFlying) {
        CrashMathEngine.calculateSurvivalProbability(simState.currentMultiplier)
    } else {
        100.0
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title & Virtual Bankroll
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SIMULATEUR RADAR CRASH",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "Testez vos stratégies sans argent réel",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "SOLDE VIRTUEL",
                                fontSize = 9.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "${String.format(Locale.US, "%.2f", simState.demoBalance)} €",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = JetXGold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = { viewModel.resetDemoBalance() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.RestartAlt, contentDescription = "Reset Solde", tint = TextSecondary)
                        }
                    }
                }
            }
        }

        // Live Radar Graph Canvas
        item {
            FlightRadarAnimation(
                currentMultiplier = simState.currentMultiplier,
                isFlying = simState.isFlying,
                isCrashed = simState.isCrashed,
                hasCashedOut = simState.hasCashedOut,
                cashedOutMultiplier = simState.cashedOutMultiplier,
                flightProgress = simState.flightProgress
            )
        }

        // Live Telemetry Bar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Survie Instantanée", fontSize = 10.sp, color = TextSecondary)
                        Text(
                            text = if (simState.isFlying) "$liveProbability%" else "--",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (liveProbability > 50) MultiplierGreen else AviatorRed,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Column {
                        Text("Gain Potentiel", fontSize = 10.sp, color = TextSecondary)
                        val potential = simState.currentBet * simState.currentMultiplier
                        Text(
                            text = "+${String.format(Locale.US, "%.2f", potential - simState.currentBet)} €",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanNeon,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Dernier Résultat", fontSize = 10.sp, color = TextSecondary)
                        Text(
                            text = when {
                                simState.hasCashedOut -> "+${simState.lastProfit} €"
                                simState.isCrashed -> "-${simState.currentBet} €"
                                else -> "0.00 €"
                            },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (simState.hasCashedOut) MultiplierGreen else AviatorRed,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Main Action Button (Takeoff / Cashout)
        item {
            if (simState.isFlying && !simState.hasCashedOut) {
                Button(
                    onClick = { viewModel.cashOut() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .testTag("cashout_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = MultiplierGreen),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ENCAISSER (${String.format(Locale.US, "%.2fx", simState.currentMultiplier)})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            } else {
                Button(
                    onClick = { viewModel.startFlight() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .testTag("takeoff_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = AviatorRed),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.FlightTakeoff, contentDescription = null, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DÉCOLLER (${simState.currentBet.toInt()} €)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // Bet & Auto Cashout Config
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "CONFIGURATION DU TOUR",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Montant de la mise virtuelle :", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))

                    // Preset bet chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(2.0, 5.0, 10.0, 25.0, 50.0).forEach { betAmt ->
                            FilterChip(
                                selected = simState.currentBet == betAmt,
                                onClick = { viewModel.setSimulationBet(betAmt) },
                                label = { Text("${betAmt.toInt()} €", fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AviatorRed,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color(0xFF1E293B),
                                    labelColor = Color(0xFFCBD5E1)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Auto-Cashout Objectif (optionnel) :", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = autoCashoutText,
                        onValueChange = {
                            autoCashoutText = it
                            val d = it.toDoubleOrNull()
                            if (d != null && d >= 1.05) {
                                viewModel.setSimulationAutoCashout(d)
                            }
                        },
                        placeholder = { Text("ex: 1.50 ou 2.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = CyanNeon,
                            unfocusedBorderColor = SurfaceCardBorder
                        ),
                        singleLine = true
                    )
                }
            }
        }
    }
}
