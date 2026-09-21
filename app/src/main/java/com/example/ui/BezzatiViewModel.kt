package com.example.ui

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.RoastDatabase
import com.example.data.local.RoastEntity
import com.example.data.model.RoastIntensity
import com.example.data.model.RoastResult
import com.example.data.model.ScheduleTaskItem
import com.example.data.model.TaskStatus
import com.example.data.repository.RoastRepository
import com.example.ui.components.PresetTemplate
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

class BezzatiViewModel(application: Application) : AndroidViewModel(application) {

    private val database = RoastDatabase.getDatabase(application)
    private val repository = RoastRepository(database.roastDao())

    val historyRoasts: StateFlow<List<RoastEntity>> = repository.allRoasts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _tasks = MutableStateFlow<List<ScheduleTaskItem>>(
        listOf(
            ScheduleTaskItem(
                title = "Subah 7 baje uthna tha",
                status = TaskStatus.SKIPPED,
                timeSpentOrWasted = "Slept till 10:30 AM"
            ),
            ScheduleTaskItem(
                title = "Instagram Reels dekhna",
                status = TaskStatus.PROCRASTINATED,
                timeSpentOrWasted = "2.5 hours"
            ),
            ScheduleTaskItem(
                title = "Pending work / Padhai",
                status = TaskStatus.HALF_DONE,
                timeSpentOrWasted = "15 mins"
            )
        )
    )
    val tasks: StateFlow<List<ScheduleTaskItem>> = _tasks.asStateFlow()

    private val _freeformText = MutableStateFlow("")
    val freeformText: StateFlow<String> = _freeformText.asStateFlow()

    private val _intensity = MutableStateFlow(RoastIntensity.DOST_GAALI)
    val intensity: StateFlow<RoastIntensity> = _intensity.asStateFlow()

    private val _currentRoast = MutableStateFlow<RoastResult?>(null)
    val currentRoast: StateFlow<RoastResult?> = _currentRoast.asStateFlow()

    private val _isRoasting = MutableStateFlow(false)
    val isRoasting: StateFlow<Boolean> = _isRoasting.asStateFlow()

    private val _roastingStepText = MutableStateFlow("")
    val roastingStepText: StateFlow<String> = _roastingStepText.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _showHistoryDialog = MutableStateFlow(false)
    val showHistoryDialog: StateFlow<Boolean> = _showHistoryDialog.asStateFlow()

    private val _showRulesDialog = MutableStateFlow(false)
    val showRulesDialog: StateFlow<Boolean> = _showRulesDialog.asStateFlow()

    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false
    private var roastingJob: Job? = null

    init {
        initTts()
    }

    private fun initTts() {
        tts = TextToSpeech(getApplication()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsInitialized = true
                // Try Indian English or Hindi
                val result = tts?.setLanguage(Locale("hi", "IN"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.setLanguage(Locale.ENGLISH)
                }
            }
        }
    }

    fun setFreeformText(text: String) {
        _freeformText.value = text
    }

    fun setIntensity(intensity: RoastIntensity) {
        _intensity.value = intensity
    }

    fun addTask(title: String, status: TaskStatus, duration: String) {
        _tasks.value = _tasks.value + ScheduleTaskItem(
            title = title,
            status = status,
            timeSpentOrWasted = duration
        )
    }

    fun removeTask(id: String) {
        _tasks.value = _tasks.value.filter { it.id != id }
    }

    fun toggleTaskStatus(id: String) {
        _tasks.value = _tasks.value.map { item ->
            if (item.id == id) {
                val nextStatus = when (item.status) {
                    TaskStatus.PROCRASTINATED -> TaskStatus.SKIPPED
                    TaskStatus.SKIPPED -> TaskStatus.HALF_DONE
                    TaskStatus.HALF_DONE -> TaskStatus.DONE
                    TaskStatus.DONE -> TaskStatus.PROCRASTINATED
                }
                item.copy(status = nextStatus)
            } else {
                item
            }
        }
    }

    fun applyTemplate(template: PresetTemplate) {
        _tasks.value = template.tasks
        _freeformText.value = template.freeformText
    }

    fun startRoast() {
        if (_isRoasting.value) return

        val inputSummary = buildString {
            if (_tasks.value.isNotEmpty()) {
                append("TODOS & ACTIVITIES:\n")
                _tasks.value.forEach {
                    append("- ${it.title}: Status=${it.status.label}")
                    if (it.timeSpentOrWasted.isNotBlank()) {
                        append(" (Time: ${it.timeSpentOrWasted})")
                    }
                    append("\n")
                }
            }
            if (_freeformText.value.isNotBlank()) {
                append("\nUSER'S OWN WORDS:\n")
                append(_freeformText.value)
            }
        }.trim()

        if (inputSummary.isBlank()) return

        stopTts()
        _isRoasting.value = true

        roastingJob = viewModelScope.launch {
            // Humorous loading steps
            val funnySteps = listOf(
                "Chai ka ghoont leke bot activate ho raha hai... ☕",
                "Tere excuses ki autopsy chal rahi hai... 🔍",
                "Dilli traffic jaisi savage comparison dhoondi ja rahi hai... 🚦",
                "Punchline sharp ki ja rahi hai... 💀"
            )

            launch {
                for (step in funnySteps) {
                    _roastingStepText.value = step
                    delay(700)
                }
            }

            val result = repository.generateRoast(inputSummary, _intensity.value)
            _currentRoast.value = result
            _isRoasting.value = false
        }
    }

    fun setCurrentRoast(roast: RoastResult) {
        _currentRoast.value = roast
    }

    fun clearCurrentRoast() {
        stopTts()
        _currentRoast.value = null
    }

    fun toggleSpeak() {
        if (_isSpeaking.value) {
            stopTts()
        } else {
            val roast = _currentRoast.value ?: return
            val speechText = buildString {
                append(roast.openingLine)
                append(". ")
                roast.specificCallouts.forEach {
                    append(it)
                    append(". ")
                }
                append(roast.savageComparison)
                append(". ")
                append(roast.closingPunchRealityCheck)
                append(". Score hai ")
                append(roast.roastScore)
                append(". ")
                append(roast.scoreVerdict)
            }

            if (isTtsInitialized && tts != null) {
                tts?.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, "ROAST_UTTERANCE")
                _isSpeaking.value = true

                // Auto reset speaking after approximate duration or monitor
                viewModelScope.launch {
                    while (tts?.isSpeaking == true) {
                        delay(500)
                    }
                    _isSpeaking.value = false
                }
            }
        }
    }

    fun stopTts() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            // Ignore
        }
        _isSpeaking.value = false
    }

    fun setShowHistoryDialog(show: Boolean) {
        _showHistoryDialog.value = show
    }

    fun setShowRulesDialog(show: Boolean) {
        _showRulesDialog.value = show
    }

    fun deleteRoastFromHistory(id: Long) {
        viewModelScope.launch {
            repository.deleteRoast(id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            // Ignore
        }
    }
}
