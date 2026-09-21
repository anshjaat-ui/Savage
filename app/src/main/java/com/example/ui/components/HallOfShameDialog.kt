package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.RoastEntity
import com.example.data.model.RoastIntensity
import com.example.data.model.RoastResult
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CharcoalBackground
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.CrimsonBurnTertiary
import com.example.ui.theme.DoneGreen
import com.example.ui.theme.FireOrangePrimary
import com.example.ui.theme.SaffronSecondary
import com.example.ui.theme.WarmMutedText
import com.example.ui.theme.WarmWhiteText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HallOfShameDialog(
    roasts: List<RoastEntity>,
    onDismiss: () -> Unit,
    onSelectRoast: (RoastResult) -> Unit,
    onDeleteRoast: (Long) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, CharcoalBorder, RoundedCornerShape(24.dp))
                .testTag("hall_of_shame_dialog"),
            color = CharcoalBackground
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = FireOrangePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Hall Of Shame 📜",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = WarmWhiteText
                            )
                            Text(
                                text = "Puraani Bezzatiyan (${roasts.size})",
                                style = MaterialTheme.typography.bodySmall,
                                color = WarmMutedText
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = WarmWhiteText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (roasts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🤷‍♂️", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Abhi tak koi bezzati nahi hui!",
                                style = MaterialTheme.typography.titleMedium,
                                color = WarmWhiteText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Apna schedule daal aur button daba.",
                                style = MaterialTheme.typography.bodySmall,
                                color = WarmMutedText
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(roasts, key = { it.id }) { item ->
                            val scoreColor = when {
                                item.roastScore >= 80 -> DoneGreen
                                item.roastScore >= 50 -> SaffronSecondary
                                item.roastScore >= 30 -> FireOrangePrimary
                                else -> CrimsonBurnTertiary
                            }

                            Card(
                                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val intensity = try {
                                            RoastIntensity.valueOf(item.intensity)
                                        } catch (e: Exception) {
                                            RoastIntensity.DOST_GAALI
                                        }
                                        onSelectRoast(
                                            RoastResult(
                                                openingLine = item.openingLine,
                                                specificCallouts = item.specificCalloutsRaw.split("\n").filter { it.isNotBlank() },
                                                savageComparison = item.savageComparison,
                                                closingPunchRealityCheck = item.closingPunch,
                                                roastScore = item.roastScore,
                                                scoreVerdict = item.scoreVerdict,
                                                intensity = intensity
                                            )
                                        )
                                        onDismiss()
                                    }
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = dateFormat.format(Date(item.timestamp)),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = WarmMutedText
                                        )

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(scoreColor.copy(alpha = 0.2f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "Score: ${item.roastScore}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = scoreColor
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(6.dp))

                                            IconButton(
                                                onClick = { onDeleteRoast(item.id) },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete",
                                                    tint = WarmMutedText,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = item.openingLine,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = WarmWhiteText,
                                        maxLines = 2
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "“${item.scoreVerdict}”",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AmberGlow,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
