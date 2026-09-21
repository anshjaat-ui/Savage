package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.data.model.RoastIntensity
import com.example.data.model.ScheduleTaskItem
import com.example.data.model.TaskStatus
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.CrimsonBurnTertiary
import com.example.ui.theme.DoneGreen
import com.example.ui.theme.FireOrangePrimary
import com.example.ui.theme.FlameOrangeVariant
import com.example.ui.theme.SaffronSecondary
import com.example.ui.theme.SkippedOrange
import com.example.ui.theme.WarmMutedText
import com.example.ui.theme.WarmWhiteText

enum class InputMode {
    TASK_LIST,
    FREEFORM_RANT
}

data class PresetTemplate(
    val title: String,
    val description: String,
    val tasks: List<ScheduleTaskItem>,
    val freeformText: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScheduleInputSection(
    tasks: List<ScheduleTaskItem>,
    freeformText: String,
    onFreeformChange: (String) -> Unit,
    selectedIntensity: RoastIntensity,
    onIntensityChange: (RoastIntensity) -> Unit,
    onAddTask: (String, TaskStatus, String) -> Unit,
    onRemoveTask: (String) -> Unit,
    onToggleTaskStatus: (String) -> Unit,
    onApplyTemplate: (PresetTemplate) -> Unit,
    onRoastClick: () -> Unit,
    isRoasting: Boolean,
    modifier: Modifier = Modifier
) {
    var currentMode by remember { mutableStateOf(InputMode.TASK_LIST) }
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskDuration by remember { mutableStateOf("") }
    var newTaskStatus by remember { mutableStateOf(TaskStatus.PROCRASTINATED) }

    val templates = remember {
        listOf(
            PresetTemplate(
                title = "Sunday Aalsi Mode 🛌",
                description = "Utha 11 baje, reels 3 hr, fir soya",
                tasks = listOf(
                    ScheduleTaskItem(title = "Subah 7 baje uthna", status = TaskStatus.SKIPPED, timeSpentOrWasted = "Slept till 11 AM"),
                    ScheduleTaskItem(title = "Instagram Reels & Doomscroll", status = TaskStatus.PROCRASTINATED, timeSpentOrWasted = "3.5 hours"),
                    ScheduleTaskItem(title = "Dopahar ka lunch & power nap", status = TaskStatus.DONE, timeSpentOrWasted = "2 hours nap"),
                    ScheduleTaskItem(title = "Gym ya walk jaana", status = TaskStatus.SKIPPED, timeSpentOrWasted = "Bed se nahi utha")
                ),
                freeformText = "Bhai subah 7 baje ka alarm lagaya tha, 11 baje tak snooze karta raha. Fir uthke 3 ghante Instagram pe mindless reels dekhi. Dopahar ko khana khake fir 2 ghante so gaya. Shaam ko gym jaana tha par thoda thak gaya to chai aur pakode kha liye."
            ),
            PresetTemplate(
                title = "WFH Ka Delusion 💻",
                description = "Active on Slack, passive in life",
                tasks = listOf(
                    ScheduleTaskItem(title = "9 AM Standup call", status = TaskStatus.DONE, timeSpentOrWasted = "Said 'working on it'"),
                    ScheduleTaskItem(title = "Feature implementation code", status = TaskStatus.HALF_DONE, timeSpentOrWasted = "Wrote 4 lines"),
                    ScheduleTaskItem(title = "YouTube tutorials (fell into rabbit hole)", status = TaskStatus.PROCRASTINATED, timeSpentOrWasted = "2 hours"),
                    ScheduleTaskItem(title = "Chai break with roommates", status = TaskStatus.DONE, timeSpentOrWasted = "1 hour tapri")
                ),
                freeformText = "Work from home tha aaj. Subah standup mein bola 'working on bug fix', fir YouTube pe 2 ghante podcast aur gaming stream dekha. 4 lines of code likh ke commit kar diya aur laptop mouse jiggler pe laga diya."
            ),
            PresetTemplate(
                title = "Kal Se Pakka Gym 💪",
                description = "Subscription active, body passive",
                tasks = listOf(
                    ScheduleTaskItem(title = "Morning Gym Leg Day", status = TaskStatus.SKIPPED, timeSpentOrWasted = "Bole 'kal karenge'"),
                    ScheduleTaskItem(title = "High protein breakfast", status = TaskStatus.HALF_DONE, timeSpentOrWasted = "Chai and 4 biscuits"),
                    ScheduleTaskItem(title = "Looked at gym reels & workout motivation", status = TaskStatus.PROCRASTINATED, timeSpentOrWasted = "1.5 hours"),
                    ScheduleTaskItem(title = "Evening walk", status = TaskStatus.SKIPPED, timeSpentOrWasted = "Rain excuse")
                ),
                freeformText = "Aaj leg day tha gym mein, par subah aankh khuli to laga rest day hai. Fir phone pe workout reels dekh ke socha kal subah 5 baje jaunga. Nashte mein protein ki jagah 2 samose pel diye."
            ),
            PresetTemplate(
                title = "Productive (Sharma Ji) 🌟",
                description = "Dare the bot to roast actual productivity!",
                tasks = listOf(
                    ScheduleTaskItem(title = "5 AM Morning Run 5km", status = TaskStatus.DONE, timeSpentOrWasted = "30 mins"),
                    ScheduleTaskItem(title = "Finished complete client project", status = TaskStatus.DONE, timeSpentOrWasted = "4 hours deep work"),
                    ScheduleTaskItem(title = "Read 25 pages of Book", status = TaskStatus.DONE, timeSpentOrWasted = "45 mins"),
                    ScheduleTaskItem(title = "Zero social media screen time", status = TaskStatus.DONE, timeSpentOrWasted = "No phone")
                ),
                freeformText = "Subah 5 baje uthke 5km running complete ki. Din mein poora client project finish kiya without distraction. Shaam ko 25 pages book padhi aur mobile bilkul use nahi kiya. Sab goals achieved!"
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CharcoalSurface)
            .border(1.5.dp, CharcoalBorder, RoundedCornerShape(24.dp))
            .padding(18.dp)
            .testTag("schedule_input_section")
    ) {
        // Intensity Selector
        Text(
            text = "BEZZATI KA LEVEL CHUNO",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = AmberGlow
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RoastIntensity.values().forEach { intensity ->
                val isSelected = selectedIntensity == intensity
                val chipColor = when (intensity) {
                    RoastIntensity.THODA_PIGHLO -> Color(0xFFFFB74D)
                    RoastIntensity.DOST_GAALI -> FireOrangePrimary
                    RoastIntensity.MUMMY_KE_TAANE -> CrimsonBurnTertiary
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) chipColor.copy(alpha = 0.25f) else CharcoalSurfaceVariant)
                        .border(
                            1.5.dp,
                            if (isSelected) chipColor else CharcoalBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onIntensityChange(intensity) }
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = intensity.icon,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = intensity.label,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isSelected) chipColor else WarmMutedText
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Preset Templates Row
        Text(
            text = "QUICK FILL TEMPLATES (TYPICAL DIN)",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = WarmMutedText
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            templates.forEach { tmpl ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(CharcoalSurfaceVariant)
                        .border(1.dp, CharcoalBorder, RoundedCornerShape(10.dp))
                        .clickable { onApplyTemplate(tmpl) }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = tmpl.title,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = WarmWhiteText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Tab Row: Tasks vs Freeform
        TabRow(
            selectedTabIndex = currentMode.ordinal,
            containerColor = CharcoalSurfaceVariant,
            contentColor = FireOrangePrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[currentMode.ordinal]),
                    color = FireOrangePrimary,
                    height = 3.dp
                )
            },
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = currentMode == InputMode.TASK_LIST,
                onClick = { currentMode = InputMode.TASK_LIST },
                text = {
                    Text(
                        text = "Task List (${tasks.size})",
                        fontWeight = if (currentMode == InputMode.TASK_LIST) FontWeight.Bold else FontWeight.Normal,
                        color = if (currentMode == InputMode.TASK_LIST) FireOrangePrimary else WarmMutedText
                    )
                }
            )
            Tab(
                selected = currentMode == InputMode.FREEFORM_RANT,
                onClick = { currentMode = InputMode.FREEFORM_RANT },
                text = {
                    Text(
                        text = "Dil Ka Rant (Text)",
                        fontWeight = if (currentMode == InputMode.FREEFORM_RANT) FontWeight.Bold else FontWeight.Normal,
                        color = if (currentMode == InputMode.FREEFORM_RANT) FireOrangePrimary else WarmMutedText
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Mode Content
        if (currentMode == InputMode.TASK_LIST) {
            // Task List Mode
            Column {
                // Add Task Bar
                OutlinedTextField(
                    value = newTaskTitle,
                    onValueChange = { newTaskTitle = it },
                    placeholder = { Text("Task likh... e.g. Gym jaana tha", color = WarmMutedText) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_task_title"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FireOrangePrimary,
                        unfocusedBorderColor = CharcoalBorder,
                        focusedTextColor = WarmWhiteText,
                        unfocusedTextColor = WarmWhiteText
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newTaskDuration,
                        onValueChange = { newTaskDuration = it },
                        placeholder = { Text("Duration: e.g. 2 ghante", color = WarmMutedText) },
                        modifier = Modifier
                            .weight(1.4f)
                            .testTag("input_task_duration"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FireOrangePrimary,
                            unfocusedBorderColor = CharcoalBorder,
                            focusedTextColor = WarmWhiteText,
                            unfocusedTextColor = WarmWhiteText
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            if (newTaskTitle.isNotBlank()) {
                                onAddTask(newTaskTitle.trim(), newTaskStatus, newTaskDuration.trim())
                                newTaskTitle = ""
                                newTaskDuration = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FireOrangePrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_add_task")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Task")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Status selector for new task
                Text(
                    text = "Status: (Tap to select for next task)",
                    style = MaterialTheme.typography.labelSmall,
                    color = WarmMutedText
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TaskStatus.values().forEach { status ->
                        val isSelected = newTaskStatus == status
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(status.badgeColor).copy(alpha = 0.25f) else CharcoalSurfaceVariant)
                                .border(
                                    1.dp,
                                    if (isSelected) Color(status.badgeColor) else CharcoalBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { newTaskStatus = status }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = status.label,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                color = if (isSelected) Color(status.badgeColor) else WarmMutedText
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tasks Display
                if (tasks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CharcoalSurfaceVariant.copy(alpha = 0.5f))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Koi task nahi add kiya! Ya to upar task add kar, ya template choose kar, ya 'Dil Ka Rant' mein seedha likh.",
                            style = MaterialTheme.typography.bodySmall,
                            color = WarmMutedText,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        tasks.forEach { item ->
                            TaskRowItem(
                                item = item,
                                onToggleStatus = { onToggleTaskStatus(item.id) },
                                onRemove = { onRemoveTask(item.id) }
                            )
                        }
                    }
                }
            }
        } else {
            // Freeform Rant Mode
            Column {
                OutlinedTextField(
                    value = freeformText,
                    onValueChange = onFreeformChange,
                    placeholder = {
                        Text(
                            "Subah se kya kaand kiya? Yahan sach likh, AI se kya sharmana...\n" +
                                    "e.g. Subah 9 baje utha, 2 ghante Instagram reels dekh li. Fir gym jaane ka socha par neend aa gayi. Dopahar ko YouTube dekha aur shaam ko chai peeke aalsi banke pada raha...",
                            color = WarmMutedText,
                            lineHeight = 20.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .testTag("input_freeform_text"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FireOrangePrimary,
                        unfocusedBorderColor = CharcoalBorder,
                        focusedTextColor = WarmWhiteText,
                        unfocusedTextColor = WarmWhiteText
                    ),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Tip: Jitni exact details likhoge (e.g. '2 ghante Reels', '4 cup Chai'), bezzati utni hi deadly hogi!",
                    style = MaterialTheme.typography.labelSmall,
                    color = AmberGlow
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Big Roast Action Button
        Button(
            onClick = onRoastClick,
            enabled = !isRoasting && (tasks.isNotEmpty() || freeformText.isNotBlank()),
            colors = ButtonDefaults.buttonColors(
                containerColor = FireOrangePrimary,
                disabledContainerColor = CharcoalSurfaceVariant
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("btn_start_roast"),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = if (!isRoasting && (tasks.isNotEmpty() || freeformText.isNotBlank())) Color.White else WarmMutedText,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isRoasting) "Bezzati Taiyaar Ho Rahi Hai... 🔥" else "BEZZATI KARO BRO 🔥",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = if (!isRoasting && (tasks.isNotEmpty() || freeformText.isNotBlank())) Color.White else WarmMutedText
                )
            }
        }
    }
}

@Composable
fun TaskRowItem(
    item: ScheduleTaskItem,
    onToggleStatus: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CharcoalSurfaceVariant)
            .border(1.dp, CharcoalBorder, RoundedCornerShape(12.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = WarmWhiteText
            )
            if (item.timeSpentOrWasted.isNotBlank()) {
                Text(
                    text = "Time: ${item.timeSpentOrWasted}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AmberGlow
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(item.status.badgeColor).copy(alpha = 0.2f))
                    .clickable { onToggleStatus() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = item.status.label,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(item.status.badgeColor)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove Task",
                    tint = WarmMutedText,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
