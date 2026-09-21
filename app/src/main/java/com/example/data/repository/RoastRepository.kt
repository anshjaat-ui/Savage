package com.example.data.repository

import com.example.data.local.RoastDao
import com.example.data.local.RoastEntity
import com.example.data.model.RoastIntensity
import com.example.data.model.RoastResult
import com.example.data.remote.GeminiApiClient
import com.example.data.remote.GeminiContent
import com.example.data.remote.GeminiGenerateRequest
import com.example.data.remote.GeminiGenerationConfig
import com.example.data.remote.GeminiPart
import com.example.data.remote.RoastApiResponse
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONObject

class RoastRepository(
    private val roastDao: RoastDao
) {
    val allRoasts: Flow<List<RoastEntity>> = roastDao.getAllRoasts()

    suspend fun generateRoast(
        userInput: String,
        intensity: RoastIntensity
    ): RoastResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = buildPrompt(userInput, intensity)
                val request = GeminiGenerateRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(GeminiPart(text = prompt))
                        )
                    ),
                    generationConfig = GeminiGenerationConfig(
                        temperature = when (intensity) {
                            RoastIntensity.THODA_PIGHLO -> 0.7f
                            RoastIntensity.DOST_GAALI -> 0.85f
                            RoastIntensity.MUMMY_KE_TAANE -> 0.95f
                        },
                        responseMimeType = "application/json"
                    )
                )

                val response = GeminiApiClient.apiService.generateRoast(apiKey, request)
                val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

                if (!rawText.isNullOrBlank()) {
                    val parsed = parseRoastResponse(rawText, intensity)
                    if (parsed != null) {
                        saveRoastToDb(userInput, parsed, intensity)
                        return@withContext parsed
                    }
                }
            } catch (e: Exception) {
                // If API fails or rate-limits, gracefully fallback
            }
        }

        // Offline / Smart Fallback Engine
        val fallback = generateSmartFallbackRoast(userInput, intensity)
        saveRoastToDb(userInput, fallback, intensity)
        fallback
    }

    private fun parseRoastResponse(jsonString: String, intensity: RoastIntensity): RoastResult? {
        return try {
            // Clean up possible markdown wrappers ```json ... ```
            val cleanJson = jsonString
                .trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val adapter = GeminiApiClient.moshi.adapter(RoastApiResponse::class.java)
            val apiRes = adapter.fromJson(cleanJson)

            if (apiRes?.openingLine != null && apiRes.specificCallouts != null) {
                RoastResult(
                    openingLine = apiRes.openingLine,
                    specificCallouts = apiRes.specificCallouts.filter { it.isNotBlank() },
                    savageComparison = apiRes.savageComparison ?: "Tera schedule dekh ke lagta hai ambition aur reality mein 36 ka aakda hai.",
                    closingPunchRealityCheck = apiRes.closingPunchRealityCheck ?: "Chalo has liya na? Ab mobile side mein rakh aur kaam pe lag ja.",
                    roastScore = (apiRes.roastScore ?: 35).coerceIn(0, 100),
                    scoreVerdict = apiRes.scoreVerdict ?: "Bhai tu insaan hai ya snooze button ka human version?",
                    intensity = intensity,
                    isAIGenerated = true
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun saveRoastToDb(
        userInput: String,
        result: RoastResult,
        intensity: RoastIntensity
    ) {
        try {
            roastDao.insertRoast(
                RoastEntity(
                    inputSchedule = userInput,
                    openingLine = result.openingLine,
                    specificCalloutsRaw = result.specificCallouts.joinToString("\n"),
                    savageComparison = result.savageComparison,
                    closingPunch = result.closingPunchRealityCheck,
                    roastScore = result.roastScore,
                    scoreVerdict = result.scoreVerdict,
                    intensity = intensity.name
                )
            )
        } catch (e: Exception) {
            // Non-fatal database save error
        }
    }

    suspend fun deleteRoast(id: Long) = withContext(Dispatchers.IO) {
        roastDao.deleteRoastById(id)
    }

    suspend fun toggleFavorite(id: Long, currentFav: Boolean) = withContext(Dispatchers.IO) {
        roastDao.updateFavorite(id, !currentFav)
    }

    private fun buildPrompt(userInput: String, intensity: RoastIntensity): String {
        val intensityGuide = when (intensity) {
            RoastIntensity.THODA_PIGHLO -> "Thoda halka aur funny roast (Mild friendly tease, zero hurt)."
            RoastIntensity.DOST_GAALI -> "Proper desi dost wali savage bezzati (Medium spicy, super relatable, sharp punches)."
            RoastIntensity.MUMMY_KE_TAANE -> "Full savage level, jaise mummy subah subah taane marti hain ya mohalle ke chacha judge karte hain."
        }

        return """
            Tum ek savage, witty AI roast-master ho jiska naam hai "Bezzati Bot".
            Tumhara kaam hai user ke din/schedule/todo list ko padhke unko dosti bhare andaaz mein bezzat karna — bilkul waise jaise koi bahut close, thoda toxic-but-loving dost/cousin karta hai.
            
            INTENSITY LEVEL: $intensityGuide
            
            TONE & STYLE:
            - Hinglish mein baat karo (Hindi + English mix), casual aur desi flavor ke saath (jaise 'bhai', 'yaar', 'scene', 'jugaad', 'taane', 'aukaat-se-bahar').
            - Savage but NOT mean-spirited — ye "dost wali gaali" honi chahiye, dushmani wali nahi.
            - Comedy timing important hai — punchline chhoti aur sharp honi chahiye, lecture nahi.
            - Bollywood/desi pop-culture references, memes, aur everyday Indian life ke examples use karo (cutting chai, WhatsApp family group, Sharma ji ka beta, Zomato tracking, mummy ke taane, boss ka mail).
            - Har roast unique lagni chahiye — generic statements bilkul mat do.
            
            USER INPUT SCHEDULE/TODO:
            \"\"\"
            $userInput
            \"\"\"
            
            OUTPUT RULES (MUST STRICTLY FOLLOW):
            1. openingLine: 1-2 lines zinger, seedha point pe vaar karo.
            2. specificCallouts: Array of 2 to 4 bullet strings. MUST explicitly target the exact numbers/tasks/apps the user wrote (e.g., if user said 'Instagram 2 hours' or 'Skipped gym', target that exact activity and duration).
            3. savageComparison: One savage comparison/analogy (e.g. comparing user's productivity to Indian bureaucracy, Dilli traffic, RCB trophy cabinet, etc.).
            4. closingPunchRealityCheck: Closing punch with light reality check, ending on an encouraging yet funny note so user feels motivated.
            5. roastScore: Integer from 0 to 100 (0 = "din poora waste", 100 = "bhai tu insaan hai ya machine").
            6. scoreVerdict: One-liner verdict summarizing their score.
            
            CRITICAL ETHICAL RULES:
            - NEVER roast sensitive personal topics, mental health, family trauma, body-shaming, or financial distress. ONLY roast time-management, laziness, and productivity.
            - If user genuinely accomplished all their goals and had a productive day, give a backhanded compliment (e.g. "Arey wah, aaj to sach mein insaan ban gaya, kal phir se chutti lega kya?").
            
            Return ONLY raw valid JSON (no markdown wrapping) conforming to:
            {
              "openingLine": "string",
              "specificCallouts": ["string", "string", "string"],
              "savageComparison": "string",
              "closingPunchRealityCheck": "string",
              "roastScore": 42,
              "scoreVerdict": "string"
            }
        """.trimIndent()
    }

    private fun generateSmartFallbackRoast(userInput: String, intensity: RoastIntensity): RoastResult {
        val lower = userInput.lowercase()
        val callouts = mutableListOf<String>()

        var lazinessScore = 30

        // Analyze specific activities
        if (lower.contains("instagram") || lower.contains("insta") || lower.contains("reels") || lower.contains("scroll")) {
            val reelsRoast = when (intensity) {
                RoastIntensity.MUMMY_KE_TAANE -> "Instagram pe itni reels dekh li ki ab algorithm bhi bol raha hoga: 'Bhai tu jaake nahaye-dhoye kuch kaam kar le'."
                RoastIntensity.THODA_PIGHLO -> "Reels scroll karne se thumb ki muscle to ban gayi, baaki kaam zero raha."
                else -> "Instagram reels dekhne mein tune itna focus lagaya, jitna NASA wale rocket launch mein nahi lagate."
            }
            callouts.add(reelsRoast)
            lazinessScore -= 15
        }

        if (lower.contains("gym") || lower.contains("workout") || lower.contains("exercise") || lower.contains("running")) {
            val gymRoast = if (lower.contains("skip") || lower.contains("nahi") || lower.contains("kal") || lower.contains("miss")) {
                "Gym ko 'Kal pakka jaunga' bolte bolte gym ka trainer bhi tujhe donation samajh ke bhool chuka hai."
            } else {
                "Gym jaake 20 minute workout aur 40 minute mirror selfie? Gains kam, photoshoot zyada lag raha hai."
            }
            callouts.add(gymRoast)
            lazinessScore -= 10
        }

        if (lower.contains("soya") || lower.contains("sleep") || lower.contains("neend") || lower.contains("utha") || lower.contains("snooze")) {
            val sleepRoast = "Subah alarm bajne ke baad jo 5-5 minute ke 8 snooze dabaye hain na, utne mein Desh ka budget pass ho jata."
            callouts.add(sleepRoast)
            lazinessScore -= 15
        }

        if (lower.contains("youtube") || lower.contains("netflix") || lower.contains("series") || lower.contains("movie") || lower.contains("video")) {
            val entertainmentRoast = "YouTube pe 'How to be productive' wali video dekhne mein 2 ghante barbaad kar diye... ironic level 100!"
            callouts.add(entertainmentRoast)
            lazinessScore -= 12
        }

        if (lower.contains("chai") || lower.contains("coffee") || lower.contains("tapri") || lower.contains("break")) {
            val chaiRoast = "Chai break lene ke liye pehle kaam karna padta hai dost, yahan to break ke beech mein halka sa kaam ghusa diya tune."
            callouts.add(chaiRoast)
        }

        if (lower.contains("study") || lower.contains("padhai") || lower.contains("exam") || lower.contains("book") || lower.contains("notes")) {
            val studyRoast = "Kitab khol ke baithna aur actual padhai karne mein utna hi farak hai jitna Maggi ke advertisement aur real taste mein hota hai."
            callouts.add(studyRoast)
        }

        if (lower.contains("meeting") || lower.contains("office") || lower.contains("boss") || lower.contains("code") || lower.contains("work")) {
            val officeRoast = "Laptop ki screen pe continuous click karte hue pretend karna ki bohot busy ho — Oscar winning acting thi boss."
            callouts.add(officeRoast)
        }

        // If productive words found
        val isProductive = (lower.contains("completed") || lower.contains("done") || lower.contains("finish") || lower.contains("poora")) &&
                !lower.contains("procrastinate") && !lower.contains("skipped")

        if (isProductive) {
            lazinessScore = 88
            val opening = "Arey Sharma ji ke bete, aaj tu sach mein insaan banne ki koshish kar raha hai kya?"
            val callout1 = "Tasks complete karke aise chaud mein ghoom raha hai jaise Reliance ka takeover tune hi kiya ho."
            val callout2 = "Itni productivity dekh ke tere phone ka battery percentage bhi confused hai ki ye phone unlocked kyu nahi tha."
            val comparison = "Tera aaj ka output dekh ke lagta hai galti se WiFi cut ho gaya tha ya mummy ne room mein CCTV lagwa diya."
            val closing = "Backhanded compliment le: Aaj to tune kamaal kar diya, bas kal wapas apne puraane aalsi roop mein mat laut aana!"
            val verdict = "Shabash! 100 mein se 88 le le, kal ka pata nahi."

            return RoastResult(
                openingLine = opening,
                specificCallouts = listOf(callout1, callout2),
                savageComparison = comparison,
                closingPunchRealityCheck = closing,
                roastScore = 88,
                scoreVerdict = verdict,
                intensity = intensity,
                isAIGenerated = false
            )
        }

        // Generic fallback callouts if input had few keywords
        if (callouts.isEmpty()) {
            callouts.add("Schedule dekh ke lag raha hai tera main talent hi procrastination ko art form banana hai.")
            callouts.add("Jo 3 kaam tune soche the, unme se ek bhi theek se nahi hua par excuses ka presentation ready hai.")
            callouts.add("Din bhar mobile haath mein aise chipka tha jaise koi life-support machine ho.")
        }

        val calculatedScore = lazinessScore.coerceIn(12, 55)

        val opening = when (intensity) {
            RoastIntensity.THODA_PIGHLO -> "Bhai tera aaj ka routine dekh ke lagta hai tu bas hawa khane zinda hai!"
            RoastIntensity.MUMMY_KE_TAANE -> "Hai bhagwan! Tera ye din ka hisaab agar teri mummy dekh leti na, to chai mein chini ki jagah namak daal ke deti!"
            else -> "Bhai tera ye schedule padhke mera AI logic bhi 2 second ke liye behosh ho gaya!"
        }

        val comparison = when (intensity) {
            RoastIntensity.MUMMY_KE_TAANE -> "Tera productivity rate aur sarkari daftaar ke 3 baje ke lunch break mein koi antar nahi hai."
            RoastIntensity.THODA_PIGHLO -> "Tera daily momentum 2G network pe YouTube buffer hone jaisa hai."
            else -> "Tera productivity graph aur Dilli ki rush-hour traffic mein koi fark nahi hai — sab fasa hua aur zero progress."
        }

        val closing = "Sun dost, roast to ho gaya mast has bhi liya, ab thoda sa phone side phenk aur kam se kam ek pending kaam khatam kar le. Kal naya din hai, thoda toh izzat bacha le!"

        val verdict = when {
            calculatedScore < 25 -> "Score $calculatedScore/100: Din poora waste, aalsi-pan ki factory chal rahi hai."
            calculatedScore < 50 -> "Score $calculatedScore/100: Thoda sa hila, par aukaat se kam kaam hua."
            else -> "Score $calculatedScore/100: Bach gaya thode margin se, par proud hone ki zarurat nahi."
        }

        return RoastResult(
            openingLine = opening,
            specificCallouts = callouts.take(4),
            savageComparison = comparison,
            closingPunchRealityCheck = closing,
            roastScore = calculatedScore,
            scoreVerdict = verdict,
            intensity = intensity,
            isAIGenerated = false
        )
    }
}
