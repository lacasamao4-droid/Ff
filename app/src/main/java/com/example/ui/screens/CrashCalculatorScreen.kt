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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.theme.AviatorRed
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DangerRed
import com.example.ui.theme.JetXGold
import com.example.ui.theme.MultiplierGreen
import com.example.ui.theme.MultiplierPurple
import com.example.ui.theme.WarningOrange
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CrashPredictorViewModel
import java.util.Locale

@Composable
fun CrashCalculatorScreen(
    viewModel: CrashPredictorViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Cotes & Probas", "Double Mise", "Bankroll & Séries")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = CyanNeon,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CALCULATEUR STATISTIQUE CRASH",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "Formules exactes Provably Fair (RTP 97% • Avantage Maison 3%)",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceCard,
                contentColor = AviatorRed,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = AviatorRed
                    )
                },
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) Color.White else TextSecondary
                            )
                        }
                    )
                }
            }
        }

        when (selectedTab) {
            0 -> {
                // Single Bet Calculator
                item { SingleBetCalculatorSection(viewModel) }
            }
            1 -> {
                // Dual Bet Coverage Optimizer
                item { DualBetOptimizerSection(viewModel) }
            }
            2 -> {
                // Bankroll & Loss Streak Section
                item { BankrollAndStreakSection(viewModel) }
            }
        }
    }
}

@Composable
fun SingleBetCalculatorSection(viewModel: CrashPredictorViewModel) {
    var multiplierText by remember { mutableStateOf("2.00") }
    var stakeText by remember { mutableStateOf("10.0") }

    val multiplier = multiplierText.toDoubleOrNull() ?: 2.0
    val stake = stakeText.toDoubleOrNull() ?: 10.0

    val prob = CrashMathEngine.calculateSurvivalProbability(multiplier)
    val potentialReturn = stake * multiplier
    val netProfit = potentialReturn - stake
    val breakEvenWinRate = if (multiplier > 0) (100.0 / multiplier) else 0.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "CALCUL DES CHANCES PAR MULTIPLICATEUR",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = CyanNeon
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = multiplierText,
                    onValueChange = { multiplierText = it },
                    label = { Text("Cible (x)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f).testTag("calc_multiplier_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = SurfaceCardBorder
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = stakeText,
                    onValueChange = { stakeText = it },
                    label = { Text("Mise (€)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f).testTag("calc_stake_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = SurfaceCardBorder
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Probability progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Probabilité de dépasser ${String.format(Locale.US, "%.2fx", multiplier)} :",
                    fontSize = 12.sp,
                    color = Color.White
                )
                Text(
                    text = "$prob %",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (prob > 50) MultiplierGreen else JetXGold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { (prob / 100f).toFloat().coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (prob > 50) MultiplierGreen else JetXGold,
                trackColor = Color(0x33FFFFFF)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Metrics table
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CalcResultRow("Gain Brut Potentiel", "${String.format(Locale.US, "%.2f", potentialReturn)} €", Color.White)
                CalcResultRow("Profit Net", "+${String.format(Locale.US, "%.2f", netProfit)} €", MultiplierGreen)
                CalcResultRow("Taux de Réussite Équilibre", "${String.format(Locale.US, "%.1f", breakEvenWinRate)} %", JetXGold)
                CalcResultRow("Risque Crash Instantané (1.00x)", "3.0 %", DangerRed)
            }

            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E2738))
                    .padding(10.dp)
            ) {
                Text(
                    text = "Formule : P(Crash ≥ X) = 97 / X. Pour être rentable sur le long terme à cette cote, votre stratégie doit gagner au moins ${String.format(Locale.US, "%.1f", breakEvenWinRate)}% du temps.",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun DualBetOptimizerSection(viewModel: CrashPredictorViewModel) {
    var stake1Text by remember { mutableStateOf("10.0") }
    var stake2Text by remember { mutableStateOf("5.0") }

    val s1 = stake1Text.toDoubleOrNull() ?: 10.0
    val s2 = stake2Text.toDoubleOrNull() ?: 5.0

    val totalStake = s1 + s2
    val breakevenCashout = CrashMathEngine.calculateDualBetBreakeven(s1, s2)
    val probBreakeven = CrashMathEngine.calculateSurvivalProbability(breakevenCashout)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "OPTIMISEUR DE COUVERTURE DOUBLE MISE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = JetXGold
            )
            Text(
                text = "Aviator et JetX permettent 2 mises simultanées. Calculez le cashout exact pour annuler le risque.",
                fontSize = 11.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = stake1Text,
                    onValueChange = { stake1Text = it },
                    label = { Text("Mise 1 - Sécurité (€)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f).testTag("stake1_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = JetXGold,
                        unfocusedBorderColor = SurfaceCardBorder
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = stake2Text,
                    onValueChange = { stake2Text = it },
                    label = { Text("Mise 2 - Profit (€)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f).testTag("stake2_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = JetXGold,
                        unfocusedBorderColor = SurfaceCardBorder
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Highlight Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF131D2D))
                    .border(1.dp, JetXGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "AUTOCASHOUT CONSEILLÉ POUR MISE 1 :",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = JetXGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = String.format(Locale.US, "%.2fx", breakevenCashout),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = MultiplierGreen
                        )
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Chances de réussite", fontSize = 10.sp, color = TextSecondary)
                            Text(
                                text = "$probBreakeven %",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Explication : En encaissant $s1 € à ${String.format(Locale.US, "%.2fx", breakevenCashout)}, vous encaissez ${String.format(Locale.US, "%.2f", s1 * breakevenCashout)} €, soit exactement vos 2 mises combinées ($totalStake €) ! Dès lors, votre Mise 2 ($s2 €) est 100% sans risque et peut viser 3x, 5x ou 10x.",
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun BankrollAndStreakSection(viewModel: CrashPredictorViewModel) {
    var bankrollText by remember { mutableStateOf("500.0") }
    var streakRoundsText by remember { mutableStateOf("4") }
    var streakThresholdText by remember { mutableStateOf("1.50") }

    val bankroll = bankrollText.toDoubleOrNull() ?: 500.0
    val streakRounds = streakRoundsText.toIntOrNull() ?: 4
    val streakThreshold = streakThresholdText.toDoubleOrNull() ?: 1.50

    val maxSafeStake = (bankroll * 0.02) // 2% rule
    val streakLossProb = CrashMathEngine.calculateConsecutiveLossProbability(streakThreshold, streakRounds)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "GESTION DE BANKROLL & RISQUE MARTINGALE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AviatorRed
            )
            Text(
                text = "Évaluez la probabilité de subir une série noire consécutive et protégez votre capital.",
                fontSize = 11.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = bankrollText,
                onValueChange = { bankrollText = it },
                label = { Text("Capital Total / Bankroll (€)", fontSize = 11.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = AviatorRed,
                    unfocusedBorderColor = SurfaceCardBorder
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Safe bet size box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1E2738))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Mise Max Conseillée (Règle 2%)", fontSize = 11.sp, color = TextSecondary)
                        Text(
                            text = "${String.format(Locale.US, "%.2f", maxSafeStake)} € par tour",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MultiplierGreen
                        )
                    }
                    Icon(Icons.Default.Security, contentDescription = null, tint = MultiplierGreen)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "SIMULATEUR DE SÉRIE NOIRE (PERTES CONSÉCUTIVES)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = WarningOrange
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = streakThresholdText,
                    onValueChange = { streakThresholdText = it },
                    label = { Text("Seuil bas (ex: 1.50x)", fontSize = 10.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = WarningOrange,
                        unfocusedBorderColor = SurfaceCardBorder
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = streakRoundsText,
                    onValueChange = { streakRoundsText = it },
                    label = { Text("Nb de tours d'affilée", fontSize = 10.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = WarningOrange,
                        unfocusedBorderColor = SurfaceCardBorder
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            CalcResultRow(
                label = "Probabilité de $streakRounds crashs sous ${String.format(Locale.US, "%.2fx", streakThreshold)}",
                value = "$streakLossProb %",
                color = WarningOrange
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Attention au piège de la Martingale : même si $streakLossProb% semble faible, cette série arrive inévitablement sur un volume de plusieurs centaines de parties et provoque la liquidation si les mises doublent.",
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
fun CalcResultRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = Color(0xFFCBD5E1))
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            fontFamily = FontFamily.Monospace
        )
    }
}
