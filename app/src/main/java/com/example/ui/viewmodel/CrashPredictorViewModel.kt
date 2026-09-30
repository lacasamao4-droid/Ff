package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiCrashAnalyzer
import com.example.data.calculator.CrashMathEngine
import com.example.data.db.AppDatabase
import com.example.data.db.ScanHistoryEntity
import com.example.data.model.GameType
import com.example.data.model.PredictionResult
import com.example.data.model.PresetDemo
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.random.Random

data class SimulatorState(
    val isFlying: Boolean = false,
    val isCrashed: Boolean = false,
    val hasCashedOut: Boolean = false,
    val currentMultiplier: Double = 1.0,
    val crashPoint: Double = 2.45,
    val cashedOutMultiplier: Double = 1.0,
    val flightProgress: Float = 0f,
    val demoBalance: Double = 500.0,
    val currentBet: Double = 10.0,
    val lastProfit: Double = 0.0,
    val autoCashoutTarget: Double = 2.0
)

class CrashPredictorViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val dao = db.scanHistoryDao()

    // History Flow
    val historyList: StateFlow<List<ScanHistoryEntity>> = dao.getAllScans()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Photo & Scanner State
    private val _selectedBitmap = MutableStateFlow<Bitmap?>(null)
    val selectedBitmap: StateFlow<Bitmap?> = _selectedBitmap.asStateFlow()

    private val _selectedGameType = MutableStateFlow(GameType.AUTO_DETECT)
    val selectedGameType: StateFlow<GameType> = _selectedGameType.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _predictionResult = MutableStateFlow<PredictionResult?>(null)
    val predictionResult: StateFlow<PredictionResult?> = _predictionResult.asStateFlow()

    private val _editableMultipliers = MutableStateFlow<List<Double>>(emptyList())
    val editableMultipliers: StateFlow<List<Double>> = _editableMultipliers.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _saveSuccessMessage = MutableStateFlow<String?>(null)
    val saveSuccessMessage: StateFlow<String?> = _saveSuccessMessage.asStateFlow()

    // Simulator State
    private val _simState = MutableStateFlow(SimulatorState())
    val simState: StateFlow<SimulatorState> = _simState.asStateFlow()
    private var flightJob: Job? = null

    // Calculator State
    val calcTargetMultiplier = MutableStateFlow(2.0)
    val calcStake1 = MutableStateFlow(10.0)
    val calcStake2 = MutableStateFlow(5.0)
    val calcBankroll = MutableStateFlow(500.0)
    val calcStreakLossThreshold = MutableStateFlow(1.50)
    val calcStreakLossRounds = MutableStateFlow(4)

    // Preset Demos for quick 1-click test
    val presets = listOf(
        PresetDemo(
            id = "aviator_hot",
            title = "Aviator - Série Équilibrée",
            gameType = GameType.AVIATOR,
            description = "Capture avec cycle de multiplicateurs modérés et violets réguliers.",
            sampleMultipliers = listOf(1.35, 2.40, 1.12, 4.80, 1.05, 2.15, 14.50, 1.88, 1.25)
        ),
        PresetDemo(
            id = "jetx_cold",
            title = "JetX - Série Froide (Crashs Bas)",
            gameType = GameType.JET_X,
            description = "Capture avec 4 crashs bas successifs sous 1.40x. Idéal pour tester le mode sécurité.",
            sampleMultipliers = listOf(1.10, 1.05, 1.22, 1.15, 1.08, 3.20, 1.40, 1.12)
        ),
        PresetDemo(
            id = "fury_high",
            title = "Fury Flight - Haute Volatilité",
            gameType = GameType.FURY_FLIGHT,
            description = "Capture avec gros pics roses (>20x) et alternance rapide.",
            sampleMultipliers = listOf(28.40, 1.03, 1.15, 8.50, 1.20, 1.02, 3.45, 1.90)
        )
    )

    init {
        // Initialize with default analysis from the first preset
        loadPreset(presets.first(), autoAnalyze = true)
    }

    fun setGameType(type: GameType) {
        _selectedGameType.value = type
        if (_predictionResult.value != null) {
            recalculateCurrentSequence()
        }
    }

    fun onImageSelected(bitmap: Bitmap) {
        _selectedBitmap.value = bitmap
        analyzeBitmap(bitmap)
    }

    fun loadPreset(preset: PresetDemo, autoAnalyze: Boolean = true) {
        _selectedGameType.value = preset.gameType
        _editableMultipliers.value = preset.sampleMultipliers
        if (autoAnalyze) {
            val result = CrashMathEngine.analyzeSequence(
                multipliers = preset.sampleMultipliers,
                gameType = preset.gameType,
                gameStatus = "Prêt pour le prochain envol (Preset ${preset.title})",
                isAiPowered = false
            )
            _predictionResult.value = result
        }
    }

    fun analyzeBitmap(bitmap: Bitmap) {
        viewModelScope.launch {
            _isAnalyzing.value = true
            _errorMessage.value = null

            try {
                val result = GeminiCrashAnalyzer.analyzeCrashScreenshot(
                    bitmap = bitmap,
                    preferredGame = _selectedGameType.value
                )
                _predictionResult.value = result
                _editableMultipliers.value = result.extractedMultipliers
                if (result.gameDetected != GameType.AUTO_DETECT) {
                    _selectedGameType.value = result.gameDetected
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erreur d'analyse : ${e.localizedMessage}"
                // Local fallback
                val fallback = GeminiCrashAnalyzer.fallbackAnalysis(_selectedGameType.value, e.localizedMessage ?: "")
                _predictionResult.value = fallback
                _editableMultipliers.value = fallback.extractedMultipliers
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun addMultiplierManually(value: Double) {
        if (value < 1.0) return
        val current = _editableMultipliers.value.toMutableList()
        current.add(0, (value * 100.0).toInt() / 100.0)
        _editableMultipliers.value = current
        recalculateCurrentSequence()
    }

    fun removeMultiplierAt(index: Int) {
        val current = _editableMultipliers.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _editableMultipliers.value = current
            recalculateCurrentSequence()
        }
    }

    fun recalculateCurrentSequence() {
        val list = _editableMultipliers.value
        val result = CrashMathEngine.analyzeSequence(
            multipliers = list,
            gameType = if (_selectedGameType.value == GameType.AUTO_DETECT) GameType.AVIATOR else _selectedGameType.value,
            gameStatus = "Calcul mis à jour manuellement",
            isAiPowered = _predictionResult.value?.isAiPowered ?: false
        )
        _predictionResult.value = result
    }

    fun saveCurrentPrediction() {
        val result = _predictionResult.value ?: return
        viewModelScope.launch {
            val entity = ScanHistoryEntity(
                gameName = result.gameDetected.displayName,
                multipliersCsv = result.extractedMultipliers.joinToString(", ") { String.format(Locale.US, "%.2f", it) },
                safeCashout = result.safeCashout,
                safeProbability = result.safeProbability,
                balancedCashout = result.balancedCashout,
                balancedProbability = result.balancedProbability,
                highRiskCashout = result.highRiskCashout,
                volatility = result.volatility.label,
                confidenceScore = result.confidenceScore,
                isAiPowered = result.isAiPowered,
                notes = result.strategicAdvice.take(200)
            )
            dao.insertScan(entity)
            _saveSuccessMessage.value = "Analyse sauvegardée dans l'historique !"
            delay(3000)
            _saveSuccessMessage.value = null
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            dao.deleteScanById(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            dao.clearAll()
        }
    }

    // --- Simulator Operations ---

    fun setSimulationBet(amount: Double) {
        _simState.value = _simState.value.copy(currentBet = amount.coerceIn(1.0, 1000.0))
    }

    fun setSimulationAutoCashout(target: Double) {
        _simState.value = _simState.value.copy(autoCashoutTarget = target.coerceIn(1.05, 50.0))
    }

    fun startFlight() {
        flightJob?.cancel()

        // Generate Provably Fair crash point based on house edge 3%
        // Uniform R in (0, 1]
        val r = Random.nextDouble(0.001, 1.0)
        // Crash multiplier = 0.97 / (1 - R)
        val generatedCrash = (0.97 / r).coerceIn(1.00, 45.0)
        val roundedCrash = (generatedCrash * 100.0).toInt() / 100.0

        val currentBet = _simState.value.currentBet
        val newBalance = (_simState.value.demoBalance - currentBet).coerceAtLeast(0.0)

        _simState.value = _simState.value.copy(
            isFlying = true,
            isCrashed = false,
            hasCashedOut = false,
            currentMultiplier = 1.0,
            crashPoint = roundedCrash,
            cashedOutMultiplier = 1.0,
            flightProgress = 0f,
            demoBalance = newBalance,
            lastProfit = 0.0
        )

        flightJob = viewModelScope.launch {
            var m = 1.00
            val startTime = System.currentTimeMillis()
            val autoTarget = _simState.value.autoCashoutTarget

            while (m < roundedCrash) {
                delay(60) // tick interval
                val elapsedSec = (System.currentTimeMillis() - startTime) / 1000.0
                // Exponential acceleration typical of crash games
                m = 1.00 + (elapsedSec * 0.45) + (elapsedSec * elapsedSec * 0.08)

                if (m >= roundedCrash) {
                    m = roundedCrash
                    break
                }

                val progress = (m / 10.0).toFloat().coerceIn(0.05f, 0.95f)

                // Check auto cashout
                if (autoTarget > 1.01 && m >= autoTarget && !_simState.value.hasCashedOut) {
                    cashOut(m)
                }

                _simState.value = _simState.value.copy(
                    currentMultiplier = (m * 100.0).toInt() / 100.0,
                    flightProgress = progress
                )
            }

            // CRASH!
            if (!_simState.value.hasCashedOut) {
                _simState.value = _simState.value.copy(
                    isFlying = false,
                    isCrashed = true,
                    currentMultiplier = roundedCrash,
                    flightProgress = 1.0f
                )
            } else {
                _simState.value = _simState.value.copy(
                    isFlying = false,
                    isCrashed = true,
                    currentMultiplier = roundedCrash,
                    flightProgress = 1.0f
                )
            }
        }
    }

    fun cashOut(manualMultiplier: Double? = null) {
        if (!_simState.value.isFlying || _simState.value.hasCashedOut) return

        val cashMult = manualMultiplier ?: _simState.value.currentMultiplier
        val bet = _simState.value.currentBet
        val win = bet * cashMult
        val netProfit = win - bet

        _simState.value = _simState.value.copy(
            hasCashedOut = true,
            cashedOutMultiplier = (cashMult * 100.0).toInt() / 100.0,
            demoBalance = _simState.value.demoBalance + win,
            lastProfit = (netProfit * 100.0).toInt() / 100.0
        )
    }

    fun resetDemoBalance() {
        _simState.value = _simState.value.copy(demoBalance = 500.0)
    }
}
