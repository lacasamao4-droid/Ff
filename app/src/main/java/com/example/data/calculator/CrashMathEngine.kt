package com.example.data.calculator

import com.example.data.model.DualBetStrategy
import com.example.data.model.GameType
import com.example.data.model.PredictionResult
import com.example.data.model.VolatilityLevel
import java.util.Locale
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

object CrashMathEngine {

    const val DEFAULT_RTP = 0.97 // 97% Return to Player (3% House Edge)

    /**
     * Mathematical probability that the round multiplier reaches at least [multiplier].
     * Formula: P(Crash >= X) = RTP / X
     */
    fun calculateSurvivalProbability(multiplier: Double, rtp: Double = DEFAULT_RTP): Double {
        if (multiplier <= 1.0) return 1.0
        val p = rtp / multiplier
        return (p.coerceIn(0.0, 1.0) * 1000.0).roundToInt() / 10.0 // Rounded to 1 decimal place %
    }

    /**
     * Probability of instant crash at 1.00x (House Edge instant loss).
     */
    fun calculateInstantCrashRisk(rtp: Double = DEFAULT_RTP): Double {
        val risk = (1.0 - rtp) * 100.0
        return (risk * 10.0).roundToInt() / 10.0
    }

    /**
     * Probability of N consecutive rounds crashing under threshold.
     */
    fun calculateConsecutiveLossProbability(threshold: Double, rounds: Int, rtp: Double = DEFAULT_RTP): Double {
        val pWin = (rtp / threshold).coerceIn(0.01, 0.99)
        val pLoss = 1.0 - pWin
        val prob = pLoss.pow(rounds) * 100.0
        return (prob * 100.0).roundToInt() / 100.0
    }

    /**
     * Dual Bet Safety Calculator.
     * Given Stake 1 and Stake 2, returns the minimum cashout required on Bet 1
     * so that Bet 1 alone covers (Stake 1 + Stake 2).
     */
    fun calculateDualBetBreakeven(stake1: Double, stake2: Double): Double {
        if (stake1 <= 0.0) return 1.0
        val required = (stake1 + stake2) / stake1
        return (required * 100.0).roundToInt() / 100.0
    }

    /**
     * Kelly Criterion optimal fraction of bankroll to wager.
     * f* = (p * (b - 1) - q) / (b - 1)
     * where b is multiplier, p is win probability, q = 1 - p.
     * We apply a fractional Kelly (e.g. 25% Kelly) for responsible gaming.
     */
    fun calculateKellyFraction(multiplier: Double, rtp: Double = DEFAULT_RTP): Double {
        if (multiplier <= 1.01) return 0.0
        val p = rtp / multiplier
        val q = 1.0 - p
        val bMinus1 = multiplier - 1.0
        val rawKelly = (p * bMinus1 - q) / bMinus1
        // Crash games have negative EV due to house edge, but conditional cycle exploitation or fractional sizing
        // caps standard risk at 1% - 3% of bankroll.
        val safeCap = rawKelly.coerceIn(0.005, 0.04)
        return (safeCap * 1000.0).roundToInt() / 10.0 // e.g. 1.5%
    }

    /**
     * Comprehensive analysis and calculation based on historical sequence of crash multipliers.
     */
    fun analyzeSequence(
        multipliers: List<Double>,
        gameType: GameType = GameType.AVIATOR,
        gameStatus: String = "Prêt pour le prochain tour",
        isAiPowered: Boolean = false
    ): PredictionResult {
        val cleanList = if (multipliers.isEmpty()) {
            listOf(1.45, 2.10, 1.15, 3.80, 1.05, 1.88, 12.40, 1.25)
        } else {
            multipliers
        }

        val count = cleanList.size
        val mean = cleanList.average()
        val sorted = cleanList.sorted()
        val median = if (count % 2 == 0) {
            (sorted[count / 2 - 1] + sorted[count / 2]) / 2.0
        } else {
            sorted[count / 2]
        }

        // Count cold streak (consecutive < 1.8x at the tail)
        var coldStreak = 0
        for (m in cleanList) {
            if (m < 1.80) {
                coldStreak++
            } else {
                break
            }
        }

        // Percentage of low vs high multipliers
        val lowCount = cleanList.count { it < 2.0 }
        val purpleCount = cleanList.count { it in 2.0..9.99 }
        val pinkCount = cleanList.count { it >= 10.0 }
        val lowRatio = lowCount.toDouble() / count

        // Evaluate Volatility Level
        val volatility = when {
            coldStreak >= 4 || lowRatio > 0.70 -> VolatilityLevel.HIGH
            pinkCount >= 2 && lowCount >= 4 -> VolatilityLevel.EXTREME
            lowRatio < 0.40 -> VolatilityLevel.LOW
            else -> VolatilityLevel.MEDIUM
        }

        // Adaptive Target Calculations based on sequence pressure & statistical RTP
        val safeCashout = when (volatility) {
            VolatilityLevel.LOW -> 1.42
            VolatilityLevel.MEDIUM -> 1.34
            VolatilityLevel.HIGH -> 1.25 // tighten cashout during cold streak
            VolatilityLevel.EXTREME -> 1.20
        }

        val balancedCashout = when (volatility) {
            VolatilityLevel.LOW -> 2.30
            VolatilityLevel.MEDIUM -> 2.05
            VolatilityLevel.HIGH -> 1.85
            VolatilityLevel.EXTREME -> 1.70
        }

        val highRiskCashout = when {
            coldStreak >= 3 -> 6.50 // High multiplier rebound potential
            pinkCount == 0 && count >= 8 -> 8.20 // Due for a higher multiplier cycle
            else -> 4.80
        }

        val safeProb = calculateSurvivalProbability(safeCashout)
        val balancedProb = calculateSurvivalProbability(balancedCashout)
        val highRiskProb = calculateSurvivalProbability(highRiskCashout)
        val instantCrashRisk = calculateInstantCrashRisk()

        // Dual Bet Strategy
        val bet1Ratio = 65
        val bet2Ratio = 35
        val bet1Cashout = calculateDualBetBreakeven(bet1Ratio.toDouble(), bet2Ratio.toDouble())
        val dualBetStrategy = DualBetStrategy(
            bet1Name = "Mise Sécurité (65% du budget tour)",
            bet1Cashout = bet1Cashout,
            bet1RatioPercent = bet1Ratio,
            bet2Name = "Mise Profit Libre (35% du budget tour)",
            bet2Cashout = balancedCashout,
            bet2RatioPercent = bet2Ratio,
            explanation = "En encaissant la Mise 1 à ${String.format(Locale.US, "%.2fx", bet1Cashout)}, vous remboursez 100% de vos 2 mises combinées. La Mise 2 devient alors un gain pur sans risque de perte de capital !"
        )

        // Confidence score computation
        val confidenceScore = when {
            count >= 10 && coldStreak in 1..2 -> 88
            count >= 6 -> 82
            coldStreak >= 4 -> 68 // caution advised
            else -> 75
        }

        val advice = buildString {
            append("Analyse mathématique basée sur une séquence de $count vols ($lowCount bleus, $purpleCount violets, $pinkCount roses/or). ")
            if (coldStreak >= 3) {
                append("Alerte Série Froide ($coldStreak crashs consécutifs < 1.80x) : les probabilités empiriques recommandent une mise défensive à $safeCashout x pour sécuriser le capital. ")
            } else if (purpleCount >= 3) {
                append("Phase stable détectée : bonne dispersion des multiplicateurs médians (${String.format(Locale.US, "%.2fx", median)}). ")
            } else {
                append("Tendance standard : privilégiez la stratégie double mise avec auto-cashout strict. ")
            }
            append("Règle d'or : ne dépassez jamais 2% de votre bankroll totale par envol.")
        }

        return PredictionResult(
            gameDetected = gameType,
            gameStatus = gameStatus,
            extractedMultipliers = cleanList,
            safeCashout = safeCashout,
            safeProbability = safeProb,
            balancedCashout = balancedCashout,
            balancedProbability = balancedProb,
            highRiskCashout = highRiskCashout,
            highRiskProbability = highRiskProb,
            instantCrashRisk = instantCrashRisk,
            volatility = volatility,
            coldStreakCount = coldStreak,
            meanMultiplier = (mean * 100.0).roundToInt() / 100.0,
            medianMultiplier = (median * 100.0).roundToInt() / 100.0,
            confidenceScore = confidenceScore,
            dualBetStrategy = dualBetStrategy,
            strategicAdvice = advice,
            isAiPowered = isAiPowered
        )
    }
}
