package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MultiplierBadge
import com.example.data.model.MultiplierColorCategory
import com.example.ui.theme.MultiplierBlue
import com.example.ui.theme.MultiplierPink
import com.example.ui.theme.MultiplierPurple

@Composable
fun MultiplierPill(
    badge: MultiplierBadge,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val (bgColor, textColor, borderColor) = when (badge.category) {
        MultiplierColorCategory.PINK_GOLD -> Triple(
            Color(0x33EC4899),
            MultiplierPink,
            MultiplierPink
        )
        MultiplierColorCategory.PURPLE -> Triple(
            Color(0x33A855F7),
            MultiplierPurple,
            MultiplierPurple
        )
        MultiplierColorCategory.BLUE -> Triple(
            Color(0x3338BDF8),
            MultiplierBlue,
            MultiplierBlue
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = badge.formatted,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}
