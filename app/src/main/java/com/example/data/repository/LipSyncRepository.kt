package com.example.data.repository

import com.example.data.local.LipSyncDao
import com.example.data.model.LipSyncJob
import kotlinx.coroutines.flow.Flow

class LipSyncRepository(private val lipSyncDao: LipSyncDao) {
    val allJobs: Flow<List<LipSyncJob>> = lipSyncDao.getAllJobs()

    suspend fun getJobById(id: Int): LipSyncJob? {
        return lipSyncDao.getJobById(id)
    }

    suspend fun insertJob(job: LipSyncJob): Long {
        return lipSyncDao.insertJob(job)
    }

    suspend fun updateJob(job: LipSyncJob) {
        lipSyncDao.updateJob(job)
    }

    suspend fun deleteJob(job: LipSyncJob) {
        lipSyncDao.deleteJob(job)
    }

    suspend fun deleteJobById(id: Int) {
        lipSyncDao.deleteJobById(id)
    }
}
