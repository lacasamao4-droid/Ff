package com.example

import com.example.data.calculator.CrashMathEngine
import com.example.data.model.GameType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testSurvivalProbabilityCalculation() {
        // P(>= 2.0x) = 0.97 / 2.0 = 48.5%
        val p2 = CrashMathEngine.calculateSurvivalProbability(2.0, 0.97)
        assertEquals(48.5, p2, 0.1)

        // P(>= 1.5x) = 0.97 / 1.5 = 64.7%
        val p15 = CrashMathEngine.calculateSurvivalProbability(1.5, 0.97)
        assertEquals(64.7, p15, 0.1)
    }

    @Test
    fun testDualBetBreakeven() {
        // If Stake 1 is 10 and Stake 2 is 5, total is 15.
        // Breakeven on Bet 1 is (10 + 5) / 10 = 1.50x
        val breakeven = CrashMathEngine.calculateDualBetBreakeven(10.0, 5.0)
        assertEquals(1.50, breakeven, 0.01)
    }

    @Test
    fun testAnalyzeSequence() {
        val multipliers = listOf(1.20, 1.15, 1.05, 3.40, 2.10, 12.50)
        val result = CrashMathEngine.analyzeSequence(multipliers, GameType.AVIATOR)
        assertTrue(result.safeCashout > 1.0)
        assertTrue(result.safeProbability > 50.0)
        assertTrue(result.balancedCashout > result.safeCashout)
    }
}
