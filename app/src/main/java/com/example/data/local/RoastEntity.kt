package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roasts")
data class RoastEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val inputSchedule: String,
    val openingLine: String,
    val specificCalloutsRaw: String, // newline-separated callouts
    val savageComparison: String,
    val closingPunch: String,
    val roastScore: Int,
    val scoreVerdict: String,
    val intensity: String,
    val isFavorite: Boolean = false
)
