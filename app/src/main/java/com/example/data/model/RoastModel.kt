package com.example.data.model

data class RoastResult(
    val openingLine: String,
    val specificCallouts: List<String>,
    val savageComparison: String,
    val closingPunchRealityCheck: String,
    val roastScore: Int,
    val scoreVerdict: String,
    val intensity: RoastIntensity = RoastIntensity.DOST_GAALI,
    val isAIGenerated: Boolean = true
)

enum class RoastIntensity(val label: String, val hindiLabel: String, val icon: String) {
    THODA_PIGHLO("Mild", "Thoda Pighlo 🐥", "🐥"),
    DOST_GAALI("Medium", "Dost Ki Bezzati 🔥", "🔥"),
    MUMMY_KE_TAANE("Savage", "Mummy Ke Taane 💀", "💀")
}

data class ScheduleTaskItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val status: TaskStatus = TaskStatus.PROCRASTINATED,
    val timeSpentOrWasted: String = ""
)

enum class TaskStatus(val label: String, val badgeColor: Long) {
    DONE("Pakka Ho Gaya ✅", 0xFF4CAF50),
    SKIPPED("Skip Kar Diya 😴", 0xFFFF9800),
    PROCRASTINATED("Reels Dekh Liya 📱", 0xFFE91E63),
    HALF_DONE("Aadha Adhoora ⌛", 0xFFFFC107)
}
