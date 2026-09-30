package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.CrashCalculatorScreen
import com.example.ui.screens.CrashSimulatorScreen
import com.example.ui.screens.GuideScreen
import com.example.ui.screens.PhotoScanScreen
import com.example.ui.screens.ScanHistoryScreen
import com.example.ui.theme.AviatorRed
import com.example.ui.theme.CrashPredictorTheme
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CrashPredictorViewModel

enum class AppScreen(val title: String, val icon: ImageVector, val tag: String) {
    SCANNER("Scanner", Icons.Default.PhotoCamera, "nav_scanner"),
    RADAR("Radar Live", Icons.Default.FlightTakeoff, "nav_radar"),
    CALCULATOR("Calculateur", Icons.Default.Calculate, "nav_calculator"),
    HISTORY("Historique", Icons.Default.History, "nav_history"),
    GUIDE("Guide", Icons.AutoMirrored.Filled.MenuBook, "nav_guide")
}

class MainActivity : ComponentActivity() {

    private val viewModel: CrashPredictorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CrashPredictorTheme {
                MainAppScaffold(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScaffold(viewModel: CrashPredictorViewModel) {
    var currentScreen by rememberSaveable { mutableStateOf(AppScreen.SCANNER) }

    // System BackHandler: pressing back on secondary screen returns to Scanner
    if (currentScreen != AppScreen.SCANNER) {
        BackHandler {
            currentScreen = AppScreen.SCANNER
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF090D16),
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceCard,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                AppScreen.values().forEach { screen ->
                    val isSelected = currentScreen == screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = AviatorRed
                        ),
                        modifier = Modifier.testTag(screen.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF090D16))
        ) {
            when (currentScreen) {
                AppScreen.SCANNER -> PhotoScanScreen(viewModel = viewModel)
                AppScreen.RADAR -> CrashSimulatorScreen(viewModel = viewModel)
                AppScreen.CALCULATOR -> CrashCalculatorScreen(viewModel = viewModel)
                AppScreen.HISTORY -> ScanHistoryScreen(viewModel = viewModel)
                AppScreen.GUIDE -> GuideScreen()
            }
        }
    }
}
