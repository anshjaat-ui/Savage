package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.RoastResult
import com.example.ui.BezzatiViewModel
import com.example.ui.components.HallOfShameDialog
import com.example.ui.components.RoastResultCard
import com.example.ui.components.RulesDialog
import com.example.ui.components.ScheduleInputSection
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CharcoalBackground
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.FireOrangePrimary
import com.example.ui.theme.FlameOrangeVariant
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.WarmMutedText
import com.example.ui.theme.WarmWhiteText

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                BezzatiBotApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BezzatiBotApp(
    viewModel: BezzatiViewModel = viewModel()
) {
    val context = LocalContext.current
    val tasks by viewModel.tasks.collectAsState()
    val freeformText by viewModel.freeformText.collectAsState()
    val selectedIntensity by viewModel.intensity.collectAsState()
    val currentRoast by viewModel.currentRoast.collectAsState()
    val isRoasting by viewModel.isRoasting.collectAsState()
    val roastingStepText by viewModel.roastingStepText.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val historyRoasts by viewModel.historyRoasts.collectAsState()
    val showHistoryDialog by viewModel.showHistoryDialog.collectAsState()
    val showRulesDialog by viewModel.showRulesDialog.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CharcoalBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, FireOrangePrimary, CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_bezzati_mascot),
                                contentDescription = "Bezzati Bot Mascot",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Bezzati Bot",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                    color = WarmWhiteText
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "🔥", fontSize = 16.sp)
                            }
                            Text(
                                text = "Tera Dost, Tera Roast Master",
                                style = MaterialTheme.typography.labelSmall,
                                color = FlameOrangeVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.setShowRulesDialog(true) },
                        modifier = Modifier.testTag("btn_show_rules")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Rules & Tone",
                            tint = WarmMutedText
                        )
                    }

                    IconButton(
                        onClick = { viewModel.setShowHistoryDialog(true) },
                        modifier = Modifier.testTag("btn_show_history")
                    ) {
                        BadgedBox(
                            badge = {
                                if (historyRoasts.isNotEmpty()) {
                                    Badge(
                                        containerColor = FireOrangePrimary,
                                        contentColor = Color.White
                                    ) {
                                        Text(text = "${historyRoasts.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Hall of Shame",
                                tint = WarmWhiteText
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CharcoalSurface,
                    titleContentColor = WarmWhiteText
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Banner / Intro Card
                IntroBanner(
                    onRuleClick = { viewModel.setShowRulesDialog(true) }
                )

                Spacer(modifier = Modifier.height(14.dp))

                AnimatedContent(
                    targetState = currentRoast,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "roast_screen_transition"
                ) { roast ->
                    if (roast != null) {
                        RoastResultCard(
                            roast = roast,
                            isSpeaking = isSpeaking,
                            onToggleSpeak = { viewModel.toggleSpeak() },
                            onCopy = {
                                copyRoastToClipboard(context, roast)
                            },
                            onShare = {
                                shareRoast(context, roast)
                            },
                            onRoastAgain = {
                                viewModel.clearCurrentRoast()
                            }
                        )
                    } else {
                        ScheduleInputSection(
                            tasks = tasks,
                            freeformText = freeformText,
                            onFreeformChange = { viewModel.setFreeformText(it) },
                            selectedIntensity = selectedIntensity,
                            onIntensityChange = { viewModel.setIntensity(it) },
                            onAddTask = { title, status, duration ->
                                viewModel.addTask(title, status, duration)
                            },
                            onRemoveTask = { id -> viewModel.removeTask(id) },
                            onToggleTaskStatus = { id -> viewModel.toggleTaskStatus(id) },
                            onApplyTemplate = { tmpl -> viewModel.applyTemplate(tmpl) },
                            onRoastClick = { viewModel.startRoast() },
                            isRoasting = isRoasting
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Roasting Loading Overlay
            AnimatedVisibility(
                visible = isRoasting,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f))
                        .testTag("roasting_loading_overlay"),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(0.9f)
                            .border(1.5.dp, FireOrangePrimary, RoundedCornerShape(24.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = FireOrangePrimary,
                                modifier = Modifier.size(54.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            CircularProgressIndicator(
                                color = FireOrangePrimary,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(36.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Bezzati Cook Ho Rahi Hai...",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = WarmWhiteText
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = roastingStepText.ifBlank { "Excuses ka autopsy chal raha hai..." },
                                style = MaterialTheme.typography.bodyMedium,
                                color = AmberGlow,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

    if (showHistoryDialog) {
        HallOfShameDialog(
            roasts = historyRoasts,
            onDismiss = { viewModel.setShowHistoryDialog(false) },
            onSelectRoast = { selected ->
                viewModel.setCurrentRoast(selected)
            },
            onDeleteRoast = { id ->
                viewModel.deleteRoastFromHistory(id)
            }
        )
    }

    if (showRulesDialog) {
        RulesDialog(onDismiss = { viewModel.setShowRulesDialog(false) })
    }
}

@Composable
fun IntroBanner(onRuleClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CharcoalSurfaceVariant)
            .border(1.dp, CharcoalBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "☕", fontSize = 22.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Dost wali bezzati, dushmani wali nahi!",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = WarmWhiteText
                )
                Text(
                    text = "Apna asli schedule likh, sachai sunke motivate ho.",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmMutedText
                )
            }
        }
    }
}

private fun copyRoastToClipboard(context: Context, roast: RoastResult) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val formatted = formatRoastShareText(roast)
    val clip = ClipData.newPlainText("Bezzati Bot Roast", formatted)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Roast clipboard pe copy ho gaya! 🔥", Toast.LENGTH_SHORT).show()
}

private fun shareRoast(context: Context, roast: RoastResult) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, formatRoastShareText(roast))
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Share Bezzati With Friends")
    context.startActivity(shareIntent)
}

private fun formatRoastShareText(roast: RoastResult): String {
    return buildString {
        append("🔥 BEZZATI BOT ROAST 🔥\n\n")
        append("“${roast.openingLine}”\n\n")
        append("🎯 TERE KAAND:\n")
        roast.specificCallouts.forEach {
            append("• $it\n")
        }
        append("\n💀 SAVAGE ANALOGY:\n“${roast.savageComparison}”\n\n")
        append("💡 REALITY CHECK:\n${roast.closingPunchRealityCheck}\n\n")
        append("📊 ROAST SCORE: ${roast.roastScore}/100\n")
        append("Verdict: ${roast.scoreVerdict}\n\n")
        append("— Roasted with ❤️ by Bezzati Bot 🔥")
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme { Greeting("Android") }
}
