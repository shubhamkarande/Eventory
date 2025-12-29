package com.eventory.ui.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eventory.ui.theme.*

data class InterestItem(
    val id: String,
    val name: String,
    val emoji: String,
    val color: Color
)

val interests = listOf(
    InterestItem("music", "Music", "🎵", MusicColor),
    InterestItem("tech", "Technology", "💻", TechColor),
    InterestItem("sports", "Sports", "⚽", SportsColor),
    InterestItem("art", "Art & Culture", "🎨", ArtColor),
    InterestItem("food", "Food & Drinks", "🍕", FoodColor),
    InterestItem("business", "Business", "💼", BusinessColor),
    InterestItem("health", "Health & Wellness", "🧘", SuccessGreen),
    InterestItem("education", "Education", "📚", SecondaryBlue),
    InterestItem("gaming", "Gaming", "🎮", PrimaryPurple),
    InterestItem("outdoor", "Outdoor", "🏕️", AccentOrange),
    InterestItem("networking", "Networking", "🤝", PrimaryPink),
    InterestItem("charity", "Charity", "❤️", ErrorRed)
)

@Composable
fun InterestSelectionScreen(
    isLoading: Boolean,
    onContinue: (interests: String) -> Unit
) {
    var selectedInterests by remember { mutableStateOf(setOf<String>()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "What interests you?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Select at least 3 categories to personalize your event feed",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(interests) { interest ->
                    InterestCard(
                        interest = interest,
                        isSelected = selectedInterests.contains(interest.id),
                        onClick = {
                            selectedInterests = if (selectedInterests.contains(interest.id)) {
                                selectedInterests - interest.id
                            } else {
                                selectedInterests + interest.id
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onContinue(selectedInterests.joinToString(",")) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = selectedInterests.size >= 3 && !isLoading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryPurple,
                    disabledContainerColor = PrimaryPurple.copy(alpha = 0.5f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Continue (${selectedInterests.size}/3 minimum)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun InterestCard(
    interest: InterestItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) interest.color.copy(alpha = 0.2f) else DarkCard,
        label = "bgColor"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) interest.color else Color.Transparent,
        label = "borderColor"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.2f)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = interest.emoji,
                fontSize = 36.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = interest.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) Color.White else TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
