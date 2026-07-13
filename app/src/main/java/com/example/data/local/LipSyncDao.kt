package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.data.model.LipSyncJob
import kotlinx.coroutines.flow.Flow

@Dao
interface LipSyncDao {
    @Query("SELECT * FROM lipsync_jobs ORDER BY timestamp DESC")
    fun getAllJobs(): Flow<List<LipSyncJob>>

    @Query("SELECT * FROM lipsync_jobs WHERE id = :id")
    suspend fun getJobById(id: Int): LipSyncJob?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: LipSyncJob): Long

    @Update
    suspend fun updateJob(job: LipSyncJob)

    @Delete
    suspend fun deleteJob(job: LipSyncJob)

    @Query("DELETE FROM lipsync_jobs WHERE id = :id")
    suspend fun deleteJobById(id: Int)
}
