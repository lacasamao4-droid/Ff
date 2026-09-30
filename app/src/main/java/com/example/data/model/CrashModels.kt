package com.example.data.model

enum class GameType(val displayName: String, val subtitle: String) {
    AVIATOR("Aviator", "Spribe - Avion Rouge"),
    JET_X("JetX", "Smartsoft - Fusée Rétro"),
    FURY_FLIGHT("Fury Flight", "Lucky Jet / Astronaute"),
    AUTO_DETECT("Auto Détection", "Détection automatique par IA")
}

enum class MultiplierColorCategory {
    BLUE,      // < 2.00x
    PURPLE,    // 2.00x - 9.99x
    PINK_GOLD  // >= 10.00x
}

data class MultiplierBadge(
    val value: Double
) {
    val category: MultiplierColorCategory
        get() = when {
            value >= 10.0 -> MultiplierColorCategory.PINK_GOLD
            value >= 2.0 -> MultiplierColorCategory.PURPLE
            else -> MultiplierColorCategory.BLUE
        }

    val formatted: String
        get() = String.format(java.util.Locale.US, "%.2fx", value)
}

enum class VolatilityLevel(val label: String, val description: String) {
    LOW("Faible", "Multiplicateurs réguliers autour de 1.4x - 2.5x"),
    MEDIUM("Modérée", "Alternance classique entre séries courtes et pics"),
    HIGH("Élevée", "Série de crashs bas (<1.5x) suivie de pics sporadiques"),
    EXTREME("Extrême", "Instabilité forte, prudence maximale requise")
}

data class DualBetStrategy(
    val bet1Name: String = "Mise Sécurité (Couverture)",
    val bet1Cashout: Double,
    val bet1RatioPercent: Int,
    val bet2Name: String = "Mise Profit (Croissance)",
    val bet2Cashout: Double,
    val bet2RatioPercent: Int,
    val explanation: String
)

data class PredictionResult(
    val gameDetected: GameType,
    val gameStatus: String,
    val extractedMultipliers: List<Double>,
    val safeCashout: Double,
    val safeProbability: Double,
    val balancedCashout: Double,
    val balancedProbability: Double,
    val highRiskCashout: Double,
    val highRiskProbability: Double,
    val instantCrashRisk: Double,
    val volatility: VolatilityLevel,
    val coldStreakCount: Int,
    val meanMultiplier: Double,
    val medianMultiplier: Double,
    val confidenceScore: Int,
    val dualBetStrategy: DualBetStrategy,
    val strategicAdvice: String,
    val isAiPowered: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class PresetDemo(
    val id: String,
    val title: String,
    val gameType: GameType,
    val description: String,
    val sampleMultipliers: List<Double>,
    val currentMultiplier: Double = 1.0
)
