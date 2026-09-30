package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.GameType
import com.example.data.model.MultiplierBadge
import com.example.ui.components.CashoutTargetsGrid
import com.example.ui.components.DualBetStrategyCard
import com.example.ui.components.MultiplierPill
import com.example.ui.components.PredictionSummaryHeader
import com.example.ui.theme.AviatorRed
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.JetXGold
import com.example.ui.theme.MultiplierGreen
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CrashPredictorViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PhotoScanScreen(
    viewModel: CrashPredictorViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedBitmap by viewModel.selectedBitmap.collectAsState()
    val selectedGame by viewModel.selectedGameType.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val prediction by viewModel.predictionResult.collectAsState()
    val editableMultipliers by viewModel.editableMultipliers.collectAsState()
    val saveSuccessMsg by viewModel.saveSuccessMessage.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var newMultiplierText by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(saveSuccessMsg) {
        saveSuccessMsg?.let { snackbarHostState.showSnackbar(it) }
    }

    // Photo Gallery Picker (Android Photo Picker)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, it))
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                }
                viewModel.onImageSelected(bitmap)
            } catch (e: Exception) {
                // handle error
            }
        }
    }

    // Camera Capture Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            viewModel.onImageSelected(it)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Hero Header Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.hero_crash_banner),
                            contentDescription = "Crash Predictor Banner",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    androidx.compose.ui.graphics.Brush.verticalGradient(
                                        listOf(Color(0x55000000), Color(0xCC090D16))
                                    )
                                )
                                .padding(12.dp),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Column {
                                Text(
                                    text = "PRÉDICTEUR CRASH IA",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Aviator • JetX • Fury Flight • Analyse par Photo",
                                    fontSize = 12.sp,
                                    color = CyanNeon
                                )
                            }
                        }
                    }
                }
            }

            // 2. Game Selection Filter Chips
            item {
                Column {
                    Text(
                        text = "JEU CIBLÉ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(GameType.values().toList()) { gType ->
                            FilterChip(
                                selected = selectedGame == gType,
                                onClick = { viewModel.setGameType(gType) },
                                label = { Text(gType.displayName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AviatorRed,
                                    selectedLabelColor = Color.White,
                                    containerColor = SurfaceCard,
                                    labelColor = Color(0xFFCBD5E1)
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = selectedGame == gType,
                                    borderColor = SurfaceCardBorder,
                                    selectedBorderColor = AviatorRed
                                )
                            )
                        }
                    }
                }
            }

            // 3. Photo Import / Capture Action Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            galleryLauncher.launch(
                                androidx.activity.result.PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("import_photo_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = AviatorRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Importer Photo", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { cameraLauncher.launch(null) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("take_photo_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Appareil Photo", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 4. Instant Preset Demos (For quick testing without an image file)
            item {
                Column {
                    Text(
                        text = "OU TESTER AVEC UN EXEMPLE RÉALISTE :",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = JetXGold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(viewModel.presets) { preset ->
                            Card(
                                modifier = Modifier
                                    .width(220.dp)
                                    .clickable { viewModel.loadPreset(preset, autoAnalyze = true) },
                                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = preset.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = preset.description,
                                        fontSize = 10.sp,
                                        color = TextSecondary,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Selected Image Preview (if present)
            if (selectedBitmap != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Capture analysée",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                if (isAnalyzing) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp,
                                            color = CyanNeon
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Analyse IA en cours...", fontSize = 11.sp, color = CyanNeon)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Image(
                                bitmap = selectedBitmap!!.asImageBitmap(),
                                contentDescription = "Capture écran",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            // 6. Extracted Multipliers Bar
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "HISTORIQUE DÉTECTÉ (${editableMultipliers.size} VOLS)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Du plus récent (gauche) au plus ancien (droite)",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                            IconButton(
                                onClick = { showAddDialog = true },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Ajouter cote", tint = CyanNeon)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (editableMultipliers.isEmpty()) {
                            Text(
                                text = "Aucun multiplicateur détecté. Chargez une capture ou utilisez un preset.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                editableMultipliers.forEachIndexed { index, m ->
                                    Box {
                                        MultiplierPill(
                                            badge = MultiplierBadge(m),
                                            onClick = { viewModel.removeMultiplierAt(index) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 7. Prediction Results Section
            if (prediction != null) {
                item {
                    PredictionSummaryHeader(result = prediction!!)
                }

                item {
                    Text(
                        text = "OBJECTIFS D'ENCAISSEMENT CALCULÉS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }

                item {
                    CashoutTargetsGrid(result = prediction!!)
                }

                item {
                    DualBetStrategyCard(strategy = prediction!!.dualBetStrategy)
                }

                // Strategic AI Observations
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Science,
                                    contentDescription = null,
                                    tint = CyanNeon,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "SYNTHÈSE MATHÉMATIQUE & STRATÉGIE",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = prediction!!.strategicAdvice,
                                fontSize = 12.sp,
                                color = Color(0xFFCBD5E1),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                // Save to history button
                item {
                    Button(
                        onClick = { viewModel.saveCurrentPrediction() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_prediction_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Icon(Icons.Default.Bookmark, contentDescription = null, tint = MultiplierGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sauvegarder dans l'Historique", color = Color.White, fontSize = 13.sp)
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 70.dp)
        )
    }

    // Dialog to manually add a multiplier
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Ajouter un multiplicateur", color = Color.White, fontSize = 16.sp) },
            text = {
                Column {
                    Text("Entrez la cote observée (ex: 2.45) :", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newMultiplierText,
                        onValueChange = { newMultiplierText = it },
                        placeholder = { Text("ex: 2.10") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            val v = newMultiplierText.toDoubleOrNull()
                            if (v != null && v >= 1.0) {
                                viewModel.addMultiplierManually(v)
                                newMultiplierText = ""
                                showAddDialog = false
                            }
                        }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = AviatorRed,
                            unfocusedBorderColor = SurfaceCardBorder
                        ),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val v = newMultiplierText.toDoubleOrNull()
                        if (v != null && v >= 1.0) {
                            viewModel.addMultiplierManually(v)
                            newMultiplierText = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AviatorRed)
                ) {
                    Text("Ajouter")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Annuler", color = TextSecondary)
                }
            },
            containerColor = SurfaceCard
        )
    }
}
