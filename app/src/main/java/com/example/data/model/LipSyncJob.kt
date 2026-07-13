package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lipsync_jobs")
data class LipSyncJob(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val characterName: String,
    val imageUrl: String,
    val audioUrl: String,
    val selectedModel: String, // "LivePortrait", "SadTalker", "Wav2Lip", "AniPortrait"
    val platform: String,      // "Google Colab", "Kaggle"
    val status: String,        // "Created", "Configured", "Generating Code", "Ready", "Running", "Completed"
    val generatedCode: String, // The fully generated Jupyter Notebook contents or Python script
    val videoUrl: String?,      // Simulated preview or downloaded video link
    val timestamp: Long = System.currentTimeMillis()
)
