package com.eventory.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eventory.app.data.model.EventCategory

@Composable
fun CategoryTag(
    category: EventCategory,
    modifier: Modifier = Modifier
) {
    val backgroundColor = try {
        Color(android.graphics.Color.parseColor(category.color))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.secondaryContainer
    }
    
    Text(
        text = category.name,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor.copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Medium,
        color = backgroundColor
    )
}

@Composable
fun CategoryChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    
    val textColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    androidx.compose.material3.FilterChip(
        onClick = onClick,
        label = {
            Text(
                text = text,
                color = textColor,
                style = MaterialTheme.typography.labelMedium
            )
        },
        selected = isSelected,
        modifier = modifier
    )
}