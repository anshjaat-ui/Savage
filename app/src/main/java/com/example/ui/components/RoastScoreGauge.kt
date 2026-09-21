package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.CrimsonBurnTertiary
import com.example.ui.theme.DoneGreen
import com.example.ui.theme.FireOrangePrimary
import com.example.ui.theme.SaffronSecondary
import com.example.ui.theme.WarmMutedText
import com.example.ui.theme.WarmWhiteText

@Composable
fun RoastScoreGauge(
    score: Int,
    verdict: String,
    modifier: Modifier = Modifier
) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(score) {
        animatedProgress.animateTo(
            targetValue = score / 100f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    val scoreColor = when {
        score >= 80 -> DoneGreen
        score >= 50 -> SaffronSecondary
        score >= 30 -> FireOrangePrimary
        else -> CrimsonBurnTertiary
    }

    val scoreTitle = when {
        score >= 80 -> "Insaan Hai Ya Machine 🤖"
        score >= 50 -> "Thoda Kaam Kiya 🧐"
        score >= 30 -> "Aukaat Se Kam Output 📉"
        else -> "Din Poora Barbaad 💀"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CharcoalSurfaceVariant)
            .border(1.dp, scoreColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = scoreColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ROAST SCORE",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    ),
                    color = scoreColor
                )
            }
            Text(
                text = scoreTitle,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = scoreColor
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Circular Gauge
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(120.dp)
        ) {
            Canvas(modifier = Modifier.size(110.dp)) {
                val strokeWidth = 10.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2
                val centerOffset = Offset(size.width / 2, size.height / 2)

                // Background track
                drawArc(
                    color = Color(0xFF332B27),
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    topLeft = Offset(centerOffset.x - radius, centerOffset.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Foreground active arc
                val sweep = 270f * animatedProgress.value
                val gradientBrush = Brush.sweepGradient(
                    0.0f to CrimsonBurnTertiary,
                    0.3f to FireOrangePrimary,
                    0.7f to SaffronSecondary,
                    1.0f to DoneGreen
                )

                drawArc(
                    brush = gradientBrush,
                    startAngle = 135f,
                    sweepAngle = sweep.coerceAtLeast(1f),
                    useCenter = false,
                    topLeft = Offset(centerOffset.x - radius, centerOffset.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${(animatedProgress.value * 100).toInt()}",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = WarmWhiteText
                )
                Text(
                    text = "/100",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmMutedText
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Verdict Text Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1E1613))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = "💬 \"$verdict\"",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp
                ),
                color = AmberGlow,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().testTag("roast_verdict_text")
            )
        }
    }
}
