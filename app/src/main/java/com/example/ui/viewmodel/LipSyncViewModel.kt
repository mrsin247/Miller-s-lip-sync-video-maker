package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiClient
import com.example.data.local.AppDatabase
import com.example.data.model.LipSyncJob
import com.example.data.repository.LipSyncRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class ChatMessage(val role: String, val message: String)

data class PresetCharacter(
    val name: String,
    val description: String,
    val imageUrl: String,
    val style: String
)

data class PresetAudio(
    val title: String,
    val genre: String,
    val duration: String,
    val audioUrl: String,
    val description: String
)

data class ClipSegment(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val durationSec: Int,
    val sourceUrl: String,
    val isLocal: Boolean,
    val effect: String = "None" // None, CRT Flicker, Slow Pan Zoom, RGB Shake, Retro Vapor
)

data class TextOverlay(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val colorHex: String = "#D0BCFF",
    val fontSize: Int = 20,
    val position: String = "Center", // Top, Center, Bottom
    val animation: String = "Static", // Static, Bounce, Pulse, Wavy
    val durationSec: Int = 4
)

class LipSyncViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LipSyncRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = LipSyncRepository(database.lipSyncDao())
    }

    // List of all lip sync pipeline runs from database
    val allJobs: StateFlow<List<LipSyncJob>> = repository.allJobs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Selection and Navigation states
    private val _selectedJob = MutableStateFlow<LipSyncJob?>(null)
    val selectedJob: StateFlow<LipSyncJob?> = _selectedJob.asStateFlow()

    private val _currentScreen = MutableStateFlow("Dashboard") // Dashboard, Create, Details, Chat, Presets
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    // Form inputs for creating a new pipeline
    val jobName = MutableStateFlow("")
    val characterName = MutableStateFlow("")
    val imageUrl = MutableStateFlow("")
    val audioUrl = MutableStateFlow("")
    val selectedModel = MutableStateFlow("LivePortrait") // LivePortrait, SadTalker, Wav2Lip, AniPortrait
    val platform = MutableStateFlow("Google Colab")      // Google Colab, Kaggle

    // SMART CHUNKING STATES
    val enableChunking = MutableStateFlow(true)
    val chunkDuration = MutableStateFlow(10) // Chop into 10s intervals for T4 GPU free tier limits
    val cameraMovementPrompt = MutableStateFlow("Dynamic slow forward zoom with cyber pan left")

    // TIMELINE & COMPOSER STATES
    private val _clipSegments = MutableStateFlow<List<ClipSegment>>(emptyList())
    val clipSegments: StateFlow<List<ClipSegment>> = _clipSegments.asStateFlow()

    private val _textOverlays = MutableStateFlow<List<TextOverlay>>(emptyList())
    val textOverlays: StateFlow<List<TextOverlay>> = _textOverlays.asStateFlow()

    private val _stitchedVideoUrl = MutableStateFlow<String?>(null)
    val stitchedVideoUrl: StateFlow<String?> = _stitchedVideoUrl.asStateFlow()

    private val _isStitching = MutableStateFlow(false)
    val isStitching: StateFlow<Boolean> = _isStitching.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    // Interactive Terminal Simulation States for the notebook runner
    private val _terminalLogs = MutableStateFlow<List<String>>(emptyList())
    val terminalLogs: StateFlow<List<String>> = _terminalLogs.asStateFlow()

    private val _runnerProgress = MutableStateFlow(0f)
    val runnerProgress: StateFlow<Float> = _runnerProgress.asStateFlow()

    private val _isRunnerActive = MutableStateFlow(false)
    val isRunnerActive: StateFlow<Boolean> = _isRunnerActive.asStateFlow()

    // AI Chat Copilot States
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                role = "assistant",
                message = "Hello! I'm your LipSync Pipeline Assistant. I can help you set up SadTalker, LivePortrait, or Wav2Lip on Google Colab and Kaggle. Ask me anything, or let's troubleshoot your run!"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    // High quality preset characters (royalty free / aesthetic images)
    val presetCharacters = listOf(
        PresetCharacter(
            name = "Cyber Samurai",
            description = "A sleek, glowing digital ronin under dark magenta alley lights.",
            imageUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=500&q=80",
            style = "Cyberpunk Stylized"
        ),
        PresetCharacter(
            name = "Neon Elf",
            description = "High-fantasy ethereal elf equipped with sci-fi bio-luminescent details.",
            imageUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=500&q=80",
            style = "Fantasy Realistic"
        ),
        PresetCharacter(
            name = "Nebula Astronaut",
            description = "Detailed golden visor reflecting bright nebulas, retro-futuristic gear.",
            imageUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=500&q=80",
            style = "Cinematic Sci-Fi"
        ),
        PresetCharacter(
            name = "Digital Sorcerer",
            description = "Mystic visual with glowing amethyst eyes under a heavy tech-hood.",
            imageUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=500&q=80",
            style = "Cyber Magic"
        ),
        PresetCharacter(
            name = "Vaporwave DJ",
            description = "Retro aesthetics, retro neon grids, soft pastels, chrome glasses.",
            imageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=500&q=80",
            style = "80s Retro Futurism"
        )
    )

    // High quality preset audio clips (royalty-free metadata or mock URLs)
    val presetAudioClips = listOf(
        PresetAudio(
            title = "Synthwave Beats",
            genre = "Electronic",
            duration = "0:30",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            description = "Upbeat, energetic retro bass loop and drum snare. Great for energetic movements."
        ),
        PresetAudio(
            title = "Cyberpunk Voiceover",
            genre = "Speech / Narrative",
            duration = "0:15",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            description = "A deep mechanical voice describing the birth of neural networks."
        ),
        PresetAudio(
            title = "Ethereal Ambient Vocals",
            genre = "Ambient Vocal",
            duration = "0:45",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            description = "Slow, ghostly melodic strings paired with ethereal vowel oscillations."
        ),
        PresetAudio(
            title = "Chiptune Funky Jam",
            genre = "Retro Game",
            duration = "0:25",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
            description = "Happy, fast-tempo 8-bit lead. Perfect for stylized animated pixel characters."
        ),
        PresetAudio(
            title = "Industrial Beat Heavy",
            genre = "Industrial Rock",
            duration = "0:35",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
            description = "Raw heavy-metal synthetic drum hits. Intense face expressions and head shakes."
        )
    )

    fun setScreen(screen: String) {
        _currentScreen.value = screen
    }

    fun selectJob(job: LipSyncJob?) {
        _selectedJob.value = job
        if (job != null) {
            // Setup active editing timeline clips when selecting a job
            val initialClips = listOf(
                ClipSegment(name = "${job.characterName} - Slice A", durationSec = 10, sourceUrl = job.imageUrl, isLocal = false, effect = "CRT Flicker"),
                ClipSegment(name = "${job.characterName} - Slice B", durationSec = 10, sourceUrl = job.imageUrl, isLocal = false, effect = "Slow Pan Zoom"),
                ClipSegment(name = "${job.characterName} - Slice C", durationSec = 10, sourceUrl = job.imageUrl, isLocal = false, effect = "RGB Shake")
            )
            _clipSegments.value = initialClips
            _textOverlays.value = listOf(
                TextOverlay(text = "LIPSYNC INITIALIZED", colorHex = "#D0BCFF", position = "Top", animation = "Pulse"),
                TextOverlay(text = "FREE T4 ACCELERATED", colorHex = "#B1D18A", position = "Bottom", animation = "Bounce")
            )
            _stitchedVideoUrl.value = job.videoUrl
        }
    }

    // VISUAL TIMELINE OPERATIONS
    fun addLocalClip(name: String, duration: Int, url: String) {
        val newClip = ClipSegment(
            name = name,
            durationSec = duration,
            sourceUrl = url,
            isLocal = true
        )
        _clipSegments.value = _clipSegments.value + newClip
    }

    fun removeClip(id: String) {
        _clipSegments.value = _clipSegments.value.filter { it.id != id }
    }

    fun reorderClip(fromIndex: Int, toIndex: Int) {
        val list = _clipSegments.value.toMutableList()
        if (fromIndex in list.indices && toIndex in list.indices) {
            val element = list.removeAt(fromIndex)
            list.add(toIndex, element)
            _clipSegments.value = list
        }
    }

    fun updateClipEffect(id: String, newEffect: String) {
        _clipSegments.value = _clipSegments.value.map {
            if (it.id == id) it.copy(effect = newEffect) else it
        }
    }

    // TEXT OVERLAY DESIGNER OPERATIONS
    fun addTextOverlay(text: String, colorHex: String, fontSize: Int, position: String, animation: String, duration: Int) {
        val newOverlay = TextOverlay(
            text = text,
            colorHex = colorHex,
            fontSize = fontSize,
            position = position,
            animation = animation,
            durationSec = duration
        )
        _textOverlays.value = _textOverlays.value + newOverlay
    }

    fun removeTextOverlay(id: String) {
        _textOverlays.value = _textOverlays.value.filter { it.id != id }
    }

    // Auto-Stitch Timeline execution
    fun autoStitchClips() {
        if (_clipSegments.value.isEmpty()) return
        _isStitching.value = true
        viewModelScope.launch {
            kotlinx.coroutines.delay(1800) // Simulate merging and rendering the complete video with text overlay and effects
            // Use a highly styled cyber looping MP4
            _stitchedVideoUrl.value = "https://assets.mixkit.co/videos/preview/mixkit-cyberpunk-woman-with-neon-details-portrait-40032-large.mp4"
            _selectedJob.value?.let { job ->
                val updatedJob = job.copy(videoUrl = _stitchedVideoUrl.value)
                repository.updateJob(updatedJob)
                _selectedJob.value = updatedJob
            }
            _isStitching.value = false
        }
    }

    fun selectPresetCharacter(preset: PresetCharacter) {
        characterName.value = preset.name
        imageUrl.value = preset.imageUrl
        if (jobName.value.isEmpty()) {
            jobName.value = "${preset.name} LipSync"
        }
    }

    fun selectPresetAudio(preset: PresetAudio) {
        audioUrl.value = preset.audioUrl
        if (jobName.value.isEmpty() && characterName.value.isNotEmpty()) {
            jobName.value = "${characterName.value} - ${preset.title}"
        }
    }

    fun clearForm() {
        jobName.value = ""
        characterName.value = ""
        imageUrl.value = ""
        audioUrl.value = ""
        selectedModel.value = "LivePortrait"
        platform.value = "Google Colab"
        cameraMovementPrompt.value = "Dynamic slow forward zoom with cyber pan left"
    }

    // Creates the LipSyncJob and generates the Google Colab / Kaggle pipeline code
    fun createLipSyncProject(onSuccess: () -> Unit) {
        val nameVal = jobName.value.ifEmpty { "LipSync Project ${System.currentTimeMillis() % 1000}" }
        val charNameVal = characterName.value.ifEmpty { "Custom Character" }
        val imgUrlVal = imageUrl.value.ifEmpty { "https://images.unsplash.com/photo-1578632767115-351597cf2477" }
        val audUrlVal = audioUrl.value.ifEmpty { "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3" }
        val modelVal = selectedModel.value
        val platformVal = platform.value
        val chunkingVal = enableChunking.value
        val chunkDurVal = chunkDuration.value
        val motionVal = cameraMovementPrompt.value

        _isGenerating.value = true

        viewModelScope.launch {
            // System instructions defining the Notebook Generator persona with smart chunking capabilities
            val systemInstruction = """
                You are a master Machine Learning Infrastructure Engineer specializing in highly optimized, budget-friendly open-weight pipelines.
                Your job is to output a single, complete, fully working Python Jupyter Notebook script for Google Colab/Kaggle that implements dynamic input audio splitting/chunking to bypass free tier GPU RAM and T4 limits, runs batch lip-sync, and stitches results with motion pans.
                Format the response with highly readable markdown code formatting and concise comments.
            """.trimIndent()

            val prompt = """
                Generate a highly polished, fully functioning Google Colab / Kaggle script for running AI LipSync using the model: $modelVal.
                
                Input portrait image URL: $imgUrlVal
                Input audio file URL: $audUrlVal
                Selected platform environment: $platformVal
                Enable smart audio chunking: $chunkingVal (Interval: $chunkDurVal seconds)
                Camera motion directives: $motionVal
                
                Your script must solve the Free Tier timeout/memory issues by:
                1. Parsing and splitting the master input audio track using FFmpeg into clean $chunkDurVal-second blocks.
                2. Executing batch sequence rendering using $modelVal for each slice with custom camera motion keyframes ($motionVal).
                3. Running GFPGAN on each individual chunk for ultra-crisp faces.
                4. Merging all render video chunks back into a unified high-resolution MP4.
                5. Outputting cell commands to display the completed masterpiece.
                
                Provide only the fully completed, beautiful code block without conversational introduction.
            """.trimIndent()

            val generatedCode = GeminiClient.generateContent(prompt, systemInstruction)

            // Let's create a solid offline fallback code if the API Key is not set or fails
            val finalCode = if (generatedCode.startsWith("API Key not configured") || generatedCode.contains("Error")) {
                getFallbackCode(modelVal, imgUrlVal, audUrlVal, platformVal, chunkingVal, chunkDurVal, motionVal)
            } else {
                generatedCode
            }

            // Create job
            val job = LipSyncJob(
                name = nameVal,
                characterName = charNameVal,
                imageUrl = imgUrlVal,
                audioUrl = audUrlVal,
                selectedModel = modelVal,
                platform = platformVal,
                status = "Ready to Deploy",
                generatedCode = finalCode,
                videoUrl = null
            )

            val insertedId = repository.insertJob(job)
            _selectedJob.value = job.copy(id = insertedId.toInt())
            _isGenerating.value = false
            clearForm()
            onSuccess()
        }
    }

    // Deletes a pipeline run
    fun deleteJob(job: LipSyncJob) {
        viewModelScope.launch {
            repository.deleteJob(job)
            if (_selectedJob.value?.id == job.id) {
                _selectedJob.value = null
            }
        }
    }

    // Deletes job by ID
    fun deleteJobById(id: Int) {
        viewModelScope.launch {
            repository.deleteJobById(id)
            if (_selectedJob.value?.id == id) {
                _selectedJob.value = null
            }
        }
    }

    // Simulates running/building the pipeline in an interactive Console logs environment
    fun simulatePipelineRunner(job: LipSyncJob) {
        if (_isRunnerActive.value) return
        _isRunnerActive.value = true
        _runnerProgress.value = 0f
        _terminalLogs.value = emptyList()

        val logs = listOf(
            "⏳ Initializing Google Colab Runtime (T4 GPU connected)...",
            "🛠️ Installing system requirements: FFmpeg, Cuda 12.1...",
            "📥 Cloning official ${job.selectedModel} repository...",
            "🐍 Setting up virtual Python virtualenv & pip dependencies...",
            "📦 Installing PyTorch, torchvision, torchaudio...",
            "⚙️ DETECTED SMART CHUNKING ACTIVATED: Splitting master audio into 10-second segments...",
            "✂️ FFmpeg split: created Slice_0.mp3, Slice_1.mp3, Slice_2.mp3",
            "🌟 Downloading pre-trained face parsing and landmark models...",
            "💾 Downloading ${job.selectedModel} weights & checkpoint files...",
            "🖼️ Pulling character portrait [${job.characterName}] into local workspace...",
            "🎬 Executing BATCH CHUNKED INFERENCE (Optimal Free Tier Budget)...",
            "🧬 Segment 1/3: Rendering facial expressions [100% complete] + GFPGAN applied",
            "🧬 Segment 2/3: Rendering facial expressions [100% complete] + GFPGAN applied",
            "🧬 Segment 3/3: Rendering facial expressions [100% complete] + GFPGAN applied",
            "🎨 Injecting motion coordinates [${cameraMovementPrompt.value}]...",
            "✨ Concatenating 3 video segments with zero-frame transitions...",
            "🎬 Stitching audio and high-fidelity video track container (FFmpeg)...",
            "🚀 Deploying video to local studio assets directory..."
        )

        viewModelScope.launch {
            val updatedJob = job.copy(status = "Running")
            repository.updateJob(updatedJob)
            _selectedJob.value = updatedJob

            for (i in logs.indices) {
                _terminalLogs.value = _terminalLogs.value + logs[i]
                _runnerProgress.value = (i + 1).toFloat() / logs.size
                kotlinx.coroutines.delay(650)
            }

            // After completion, generate a random sample video link from Unsplash / curated looping high-quality portrait assets!
            val sampleVideo = when(job.selectedModel) {
                "LivePortrait" -> "https://assets.mixkit.co/videos/preview/mixkit-cyberpunk-woman-with-neon-details-portrait-40032-large.mp4"
                "SadTalker" -> "https://assets.mixkit.co/videos/preview/mixkit-young-man-portrait-under-cyberpunk-lights-42261-large.mp4"
                "Wav2Lip" -> "https://assets.mixkit.co/videos/preview/mixkit-woman-smiling-at-camera-portrait-40344-large.mp4"
                else -> "https://assets.mixkit.co/videos/preview/mixkit-astronaut-floating-in-space-42352-large.mp4"
            }

            val finalJob = updatedJob.copy(
                status = "Completed",
                videoUrl = sampleVideo
            )
            repository.updateJob(finalJob)
            _selectedJob.value = finalJob
            _isRunnerActive.value = false
        }
    }

    // Handles the conversational pipeline assistant
    fun sendChatMessage(messageText: String) {
        if (messageText.isBlank()) return

        val userMsg = ChatMessage(role = "user", message = messageText)
        _chatMessages.value = _chatMessages.value + userMsg
        _isChatLoading.value = true

        viewModelScope.launch {
            val systemPrompt = """
                You are a friendly, expert Machine Learning Support Engineer specializing in open-weight visual pipelines (SadTalker, LivePortrait, Wav2Lip, AniPortrait) deployed on free tiers like Google Colab and Kaggle.
                Your response must be highly practical, specific, and clear. Help users fix errors (CUDA memory, file paths, dependency version clashes, FFmpeg audio errors).
                Be concise, inspiring, and design-focused. Encourage them using clever developer jokes occasionally. Don't use heavy corporate speak or list directory names of the Android project itself.
            """.trimIndent()

            val response = GeminiClient.generateContent(messageText, systemPrompt)
            val assistantMsg = ChatMessage(role = "assistant", message = response)
            _chatMessages.value = _chatMessages.value + assistantMsg
            _isChatLoading.value = false
        }
    }

    // Clean generated local fallback in case of no internet or missing key
    private fun getFallbackCode(
        model: String,
        img: String,
        aud: String,
        platform: String,
        chunking: Boolean = true,
        chunkDur: Int = 10,
        motion: String = "Slow Pan"
    ): String {
        return """
# =========================================================================
#  SyncWave LipSync Pipeline - Chunked Multi-Segment Deployment Code
#  Platform: $platform | Model: $model 
#  Character: $img | Audio: $aud
#  Smart Chunking: $chunking ($chunkDur seconds) | Motion: $motion
# =========================================================================

# Step 1: Connect to GPU (Select T4 GPU in Runtime Settings)
!nvidia-smi

# Step 2: Install dependencies
print("Configuring system requirements and FFmpeg chunkers...")
!apt-get install ffmpeg -y
${if (model == "LivePortrait") {
"""!git clone https://github.com/KwaiVGI/LivePortrait.git
%cd LivePortrait
!pip install -r requirements.txt
!pip install gfpgan opencv-python pillow tqdm"""
} else if (model == "SadTalker") {
"""!git clone https://github.com/OpenTalker/SadTalker.git
%cd SadTalker
!pip install -r requirements.txt
!pip install gfpgan"""
} else {
"""!git clone https://github.com/Rudrabha/Wav2Lip.git
%cd Wav2Lip
!pip install -r requirements.txt
!wget "https://iiitaphyd-my.sharepoint.com/:u:/g/personal/radrabha_m_research_iiit_ac_in/Eb3gTvCSTBhBt63id63g9pQBEZgA_X977_6Sw?download=1" -O checkpoints/wav2lip_gan.pth"""
}}

# Step 3: Fetch input character portrait image and voice track
import urllib.request
import os
print("Fetching raw media structures...")
urllib.request.urlretrieve("$img", "character_input.jpg")
urllib.request.urlretrieve("$aud", "audio_input.mp3")

# Step 4: Split audio into dynamic chunks using FFmpeg to bypass T4 Free Tier RAM limits
if $chunking:
    print("Chunking active! Slicing audio into $chunkDur-second segments...")
    os.makedirs("chunks", exist_ok=True)
    !ffmpeg -i audio_input.mp3 -f segment -segment_time $chunkDur -c copy chunks/slice_%03d.mp3
    slices = sorted([f for f in os.listdir("chunks") if f.endswith(".mp3")])
    print(f"Total audio segments generated: {len(slices)}")
else:
    slices = ["audio_input.mp3"]

# Step 5: Render and process each slice individually
print("Processing batch inference on sub-clips with custom camera motion: '$motion'")
rendered_clips = []
for idx, slice_file in enumerate(slices):
    slice_path = os.path.join("chunks", slice_file) if $chunking else slice_file
    output_clip = f"results/clip_{idx:03d}.mp4"
    print(f"Animating chunk {idx+1}/{len(slices)} using model $model...")
    
    # Run lip sync on this chunk
    ${if (model == "LivePortrait") {
    """!python speed_inference.py --source character_input.jpg --driving {slice_path} --output_dir results/ --enhancer gfpgan --motion_profile "$motion""""
    } else if (model == "SadTalker") {
    """!python inference.py --driven_audio {slice_path} --source_image character_input.jpg --result_dir results/ --enhancer gfpgan --still --pose_style "$motion""""
    } else {
    """!python inference.py --checkpoint checkpoints/wav2lip_gan.pth --face character_input.jpg --audio {slice_path} --outfile {output_clip}"""
    }}

# Step 6: Automatically stitch generated videos back into one unified masterpiece
if $chunking:
    print("Auto-stitching generated MP4 slices...")
    with open("stitch_list.txt", "w") as f:
        for r_clip in rendered_clips:
            f.write(f"file '{r_clip}'\n")
    !ffmpeg -f concat -safe 0 -i stitch_list.txt -c copy final_masterpiece.mp4
    print("Stitching complete! final_masterpiece.mp4 is ready!")
else:
    print("Single segment render completed successfully.")

from IPython.display import HTML
print("Masterpiece exported successfully! Ready for Studio Asset re-import.")
        """.trimIndent()
    }
}

class LipSyncViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LipSyncViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LipSyncViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

