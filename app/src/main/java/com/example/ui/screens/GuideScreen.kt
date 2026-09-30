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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AviatorRed
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DangerRed
import com.example.ui.theme.JetXGold
import com.example.ui.theme.MultiplierGreen
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.TextSecondary

@Composable
fun GuideScreen(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = JetXGold, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "GUIDE & JEU RESPONSABLE",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "Fonctionnement du Provably Fair, mythes des hacks et gestion des risques.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Section 1: How Provably Fair Works
        item {
            GuideSectionCard(
                icon = Icons.Default.Lock,
                iconTint = CyanNeon,
                title = "Comment fonctionne le Provably Fair ?",
                content = """
                    Les jeux de crash comme Aviator (Spribe), JetX (Smartsoft) et Fury Flight reposent sur la technologie cryptographique 'Provably Fair'.
                    
                    • Le multiplicateur final d'un tour n'est PAS généré par le casino seul.
                    • Il est le résultat d'un hachage SHA-512 combinant la 'Server Seed' (graine du serveur) et les 'Client Seeds' (graines fournies par les 3 premiers joueurs ayant misé).
                    • Ce résultat est fixé mathématiquement avant le début du vol et est vérifiable a posteriori sur la blockchain ou dans le menu de jeu.
                """.trimIndent()
            )
        }

        // Section 2: Myth Busting - Predictions vs Hacks
        item {
            GuideSectionCard(
                icon = Icons.Default.Warning,
                iconTint = DangerRed,
                title = "Vérité sur les 'Hacks' et 'Signaux 100%'",
                content = """
                    Méfiez-vous impérativement des arnaques promettant des logiciels de piratage ou des prédictions à 100% de réussite.
                    
                    • En raison du chiffrement SHA-512, aucun algorithme externe ne peut prédire avec certitude le chiffre exact à l'avance.
                    • Notre application est un outil d'aide à la décision statistique : elle calcule la distribution de probabilité réelle (P = 97% / cote), détecte les séries froides et calcule l'autocashout optimal pour couvrir vos mises.
                """.trimIndent()
            )
        }

        // Section 3: Dual Bet Golden Rule
        item {
            GuideSectionCard(
                icon = Icons.Default.Casino,
                iconTint = MultiplierGreen,
                title = "La Stratégie de Couverture Double Mise",
                content = """
                    Aviator et JetX offrent 2 panneaux de mise indépendants :
                    
                    1. Mise 1 (60% à 70% de votre mise totale) : Auto-cashout réglé à 1.45x - 1.65x. Dès qu'elle gagne, elle rembourse l'intégralité de vos deux mises combinées !
                    2. Mise 2 (30% à 40%) : Elle devient alors 100% gratuite et sans risque. Vous pouvez la laisser monter vers 3x, 5x ou 10x sans stress.
                """.trimIndent()
            )
        }

        // Section 4: Bankroll Management & Help
        item {
            GuideSectionCard(
                icon = Icons.Default.Shield,
                iconTint = JetXGold,
                title = "Règles d'or de Bankroll & Jeu Responsable",
                content = """
                    • Règle des 2% : Ne misez jamais plus de 2% de votre capital total sur un seul tour.
                    • Fixez un seuil de stop-loss strict : si vous perdez 15% de votre solde, arrêtez la session.
                    • Ne courez jamais après vos pertes (évitez de doubler frénétiquement en Martingale).
                    • Les jeux d'argent doivent rester un divertissement récréatif. Si vous ressentez une perte de contrôle, faites appel aux services d'aide (Joueurs Info Service : 09 74 75 13 13).
                """.trimIndent()
            )
        }
    }
}

@Composable
fun GuideSectionCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconTint.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = content,
                fontSize = 12.sp,
                color = Color(0xFFCBD5E1),
                lineHeight = 18.sp
            )
        }
    }
}
