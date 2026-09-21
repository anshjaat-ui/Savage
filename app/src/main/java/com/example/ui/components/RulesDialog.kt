package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CharcoalBackground
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.FireOrangePrimary
import com.example.ui.theme.WarmMutedText
import com.example.ui.theme.WarmWhiteText

@Composable
fun RulesDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, CharcoalBorder, RoundedCornerShape(24.dp))
                .testTag("rules_dialog"),
            color = CharcoalBackground
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = FireOrangePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Bezzati Bot Ke Niyam 📜",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = WarmWhiteText
                        )
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

                val rules = listOf(
                    "❤️ Dost Wali Bezzati" to "Ye bilkul waisi bezzati hai jaise koi bachpan ka jigri yaar chai ki tapri pe karta hai — no toxic dushmani.",
                    "🛡️ Zero Personal/Body Shaming" to "Personal issues, mental health, ya body-shaming bilkul strictly band hai. Sirf aur sirf time-management aur procrastination ki trolling hogi.",
                    "🎯 Data-Based Attacks" to "Jo task ya number tum likhoge (e.g. 'Instagram 2 ghante'), usi pe sniper target hoga!",
                    "🚀 Reality Check + Motivation" to "Har roast ke end mein halka sa reality check milega taaki hasi-mazaak ke baad sach mein kaam karne ka mann kare.",
                    "🤖 AI + Offline Engine" to "Gemini 3.5 Flash ke saath connected hai, aur offline mein bhi smart desi roast engine active rehta hai."
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    rules.forEach { (heading, detail) ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(CharcoalSurfaceVariant)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = heading,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = AmberGlow
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = detail,
                                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                                color = WarmWhiteText
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = FireOrangePrimary),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Samajh Gaya, Ab Roast Karo 🔥", color = WarmWhiteText)
                }
            }
        }
    }
}
