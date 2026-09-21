package com.example.ui.components

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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.PsychologyAlt
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoastResult
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.CrimsonBurnTertiary
import com.example.ui.theme.FireOrangePrimary
import com.example.ui.theme.FlameOrangeVariant
import com.example.ui.theme.SaffronSecondary
import com.example.ui.theme.WarmMutedText
import com.example.ui.theme.WarmWhiteText

@Composable
fun RoastResultCard(
    roast: RoastResult,
    isSpeaking: Boolean,
    onToggleSpeak: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onRoastAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CharcoalSurface)
            .border(1.5.dp, CharcoalBorder, RoundedCornerShape(24.dp))
            .padding(18.dp)
            .testTag("roast_result_card")
    ) {
        // Header with tag & audio button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(FireOrangePrimary.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${roast.intensity.hindiLabel} 🔥",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = FlameOrangeVariant
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (roast.isAIGenerated) Color(0xFF2E7D32).copy(alpha = 0.25f) else Color(0xFF1565C0).copy(alpha = 0.25f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (roast.isAIGenerated) "AI Roast" else "Desi Engine",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = if (roast.isAIGenerated) Color(0xFF81C784) else Color(0xFF90CAF9)
                    )
                }
            }

            // Audio Speak Button
            FilledTonalButton(
                onClick = onToggleSpeak,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (isSpeaking) CrimsonBurnTertiary else CharcoalSurfaceVariant,
                    contentColor = WarmWhiteText
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("btn_roast_listen")
            ) {
                Icon(
                    imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                    contentDescription = if (isSpeaking) "Stop Audio" else "Listen Roast",
                    modifier = Modifier.size(18.dp),
                    tint = if (isSpeaking) Color.White else AmberGlow
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isSpeaking) "Ruko" else "Suno",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isSpeaking) Color.White else AmberGlow
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION 1: Opening Roast Line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            FireOrangePrimary.copy(alpha = 0.15f),
                            CharcoalSurfaceVariant
                        )
                    )
                )
                .border(1.dp, FireOrangePrimary.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = null,
                        tint = FireOrangePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "OPENING ROAST",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = FlameOrangeVariant
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = roast.openingLine,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp
                    ),
                    color = WarmWhiteText,
                    modifier = Modifier.testTag("opening_roast_text")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION 2: Specific Callouts
        Card(
            colors = CardDefaults.cardColors(containerColor = CharcoalSurfaceVariant),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = SaffronSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TERE KAAND (SPECIFIC CALLOUTS)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = AmberGlow
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                roast.specificCallouts.forEachIndexed { index, callout ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "🔥",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 1.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = callout,
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 21.sp),
                            color = WarmWhiteText
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION 3: Savage Comparison
        Card(
            colors = CardDefaults.cardColors(containerColor = CharcoalSurfaceVariant),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PsychologyAlt,
                        contentDescription = null,
                        tint = CrimsonBurnTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SAVAGE COMPARISON",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = CrimsonBurnTertiary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "“${roast.savageComparison}”",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        lineHeight = 22.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    ),
                    color = WarmWhiteText
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION 4: Closing Punch + Reality Check
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1E281F))
                .border(1.dp, Color(0xFF43A047).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TipsAndUpdates,
                        contentDescription = null,
                        tint = Color(0xFF81C784),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "REALITY CHECK + PUNCHLINE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = Color(0xFF81C784)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = roast.closingPunchRealityCheck,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal,
                        lineHeight = 21.sp
                    ),
                    color = Color(0xFFE8F5E9)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION 5: Roast Score & Verdict Gauge
        RoastScoreGauge(
            score = roast.roastScore,
            verdict = roast.scoreVerdict
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Bottom Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onCopy,
                modifier = Modifier
                    .weight(1f)
                    .testTag("btn_copy_roast"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy Roast",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copy")
            }

            OutlinedButton(
                onClick = onShare,
                modifier = Modifier
                    .weight(1f)
                    .testTag("btn_share_roast"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Share")
            }

            Button(
                onClick = onRoastAgain,
                colors = ButtonDefaults.buttonColors(containerColor = FireOrangePrimary),
                modifier = Modifier
                    .weight(1.3f)
                    .testTag("btn_roast_again"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Naya Roast 🔥", color = WarmWhiteText)
            }
        }
    }
}
