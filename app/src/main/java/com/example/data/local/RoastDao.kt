package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RoastDao {
    @Query("SELECT * FROM roasts ORDER BY timestamp DESC")
    fun getAllRoasts(): Flow<List<RoastEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoast(roast: RoastEntity): Long

    @Query("DELETE FROM roasts WHERE id = :id")
    suspend fun deleteRoastById(id: Long)

    @Query("UPDATE roasts SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("DELETE FROM roasts")
    suspend fun clearAllRoasts()
}
