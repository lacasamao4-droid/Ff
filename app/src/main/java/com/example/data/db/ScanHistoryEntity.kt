package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val gameName: String,
    val multipliersCsv: String,
    val safeCashout: Double,
    val safeProbability: Double,
    val balancedCashout: Double,
    val balancedProbability: Double,
    val highRiskCashout: Double,
    val volatility: String,
    val confidenceScore: Int,
    val isAiPowered: Boolean,
    val notes: String
)
