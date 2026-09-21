package com.example

import com.example.data.local.RoastDao
import com.example.data.local.RoastEntity
import com.example.data.model.RoastIntensity
import com.example.data.repository.RoastRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeRoastDao : RoastDao {
    private val list = mutableListOf<RoastEntity>()
    override fun getAllRoasts(): Flow<List<RoastEntity>> = flowOf(list)
    override suspend fun insertRoast(roast: RoastEntity): Long {
        list.add(roast)
        return list.size.toLong()
    }
    override suspend fun deleteRoastById(id: Long) {
        list.removeAll { it.id == id }
    }
    override suspend fun updateFavorite(id: Long, isFavorite: Boolean) {}
    override suspend fun clearAllRoasts() { list.clear() }
}

class RoastEngineTest {

    @Test
    fun `test fallback roast generates structured response for procrastination`() = runTest {
        val fakeDao = FakeRoastDao()
        val repo = RoastRepository(fakeDao)

        val input = "Instagram pe 2 ghante reels dekhi aur gym miss kar diya"
        val result = repo.generateRoast(input, RoastIntensity.DOST_GAALI)

        assertNotNull(result)
        assertTrue(result.openingLine.isNotBlank())
        assertTrue(result.specificCallouts.isNotEmpty())
        assertTrue(result.savageComparison.isNotBlank())
        assertTrue(result.closingPunchRealityCheck.isNotBlank())
        assertTrue(result.roastScore in 0..100)
        assertTrue(result.scoreVerdict.isNotBlank())
    }

    @Test
    fun `test productive input generates backhanded compliment`() = runTest {
        val fakeDao = FakeRoastDao()
        val repo = RoastRepository(fakeDao)

        val input = "Finished all project tasks and completed workout"
        val result = repo.generateRoast(input, RoastIntensity.DOST_GAALI)

        assertNotNull(result)
        assertTrue(result.roastScore >= 80)
        assertTrue(result.openingLine.contains("Sharma ji") || result.openingLine.contains("insaan"))
    }
}
