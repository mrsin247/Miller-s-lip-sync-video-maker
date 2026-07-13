package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.LipSyncJob
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChatMessage
import com.example.ui.viewmodel.LipSyncViewModel
import com.example.ui.viewmodel.PresetAudio
import com.example.ui.viewmodel.PresetCharacter
import kotlinx.coroutines.launch

private val accentGradient = Brush.horizontalGradient(
    colors = listOf(NeonViolet, NeonCyan)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LipSyncMainScreen(
    viewModel: LipSyncViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val selectedJob by viewModel.selectedJob.collectAsStateWithLifecycle()
    val allJobs by viewModel.allJobs.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(accentGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Hearing,
                                contentDescription = "Sync Logo",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "LIPSYNC STUDIO",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                letterSpacing = 2.sp
                            ),
                            color = Color.White
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BorderSlate)
                            .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(MintGreen)
                            )
                            Text(
                                text = "FREE PIPELINES",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MintGreen
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepSlate,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceSlate,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("bottom_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = currentScreen == "Dashboard" || currentScreen == "Details",
                    onClick = { viewModel.setScreen("Dashboard") },
                    icon = { Icon(imageVector = Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Studio", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonCyan,
                        selectedTextColor = NeonCyan,
                        indicatorColor = BorderSlate,
                        unselectedIconColor = MutedText,
                        unselectedTextColor = MutedText
                    )
                )
                NavigationBarItem(
                    selected = currentScreen == "Create",
                    onClick = { viewModel.setScreen("Create") },
                    icon = { Icon(imageVector = Icons.Default.AddCircle, contentDescription = "Create") },
                    label = { Text("Build", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonCyan,
                        selectedTextColor = NeonCyan,
                        indicatorColor = BorderSlate,
                        unselectedIconColor = MutedText,
                        unselectedTextColor = MutedText
                    )
                )
                NavigationBarItem(
                    selected = currentScreen == "Editor",
                    onClick = { viewModel.setScreen("Editor") },
                    icon = { Icon(imageVector = Icons.Default.Movie, contentDescription = "Editor") },
                    label = { Text("Editor", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonCyan,
                        selectedTextColor = NeonCyan,
                        indicatorColor = BorderSlate,
                        unselectedIconColor = MutedText,
                        unselectedTextColor = MutedText
                    )
                )
                NavigationBarItem(
                    selected = currentScreen == "Presets",
                    onClick = { viewModel.setScreen("Presets") },
                    icon = { Icon(imageVector = Icons.Default.Collections, contentDescription = "Presets") },
                    label = { Text("Assets", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonCyan,
                        selectedTextColor = NeonCyan,
                        indicatorColor = BorderSlate,
                        unselectedIconColor = MutedText,
                        unselectedTextColor = MutedText
                    )
                )
                NavigationBarItem(
                    selected = currentScreen == "Chat",
                    onClick = { viewModel.setScreen("Chat") },
                    icon = { Icon(imageVector = Icons.AutoMirrored.Default.Chat, contentDescription = "Co-pilot") },
                    label = { Text("Co-Pilot", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonCyan,
                        selectedTextColor = NeonCyan,
                        indicatorColor = BorderSlate,
                        unselectedIconColor = MutedText,
                        unselectedTextColor = MutedText
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(DeepSlate)
        ) {
            when (currentScreen) {
                "Dashboard" -> DashboardScreen(
                    viewModel = viewModel,
                    allJobs = allJobs,
                    onNavigateToDetails = { job ->
                        viewModel.selectJob(job)
                        viewModel.setScreen("Details")
                    }
                )
                "Create" -> CreateScreen(viewModel = viewModel)
                "Details" -> selectedJob?.let { job ->
                    DetailsScreen(
                        viewModel = viewModel,
                        job = job,
                        onBack = {
                            viewModel.setScreen("Dashboard")
                        }
                    )
                } ?: run {
                    // Fallback to Dashboard if no selected job
                    viewModel.setScreen("Dashboard")
                }
                "Editor" -> StudioEditorScreen(viewModel = viewModel)
                "Presets" -> PresetsScreen(viewModel = viewModel)
                "Chat" -> ChatScreen(viewModel = viewModel)
            }
        }
    }
}

// ==========================================
// 1. DASHBOARD SCREEN
// ==========================================
@Composable
fun DashboardScreen(
    viewModel: LipSyncViewModel,
    allJobs: List<LipSyncJob>,
    onNavigateToDetails: (LipSyncJob) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, NeonViolet.copy(alpha = 0.4f)),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_hero_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(NeonViolet.copy(alpha = 0.15f), Color.Transparent)
                                ),
                                center = Offset(size.width, 0f),
                                radius = size.width * 0.8f
                            )
                        }
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Magic",
                                tint = NeonGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "CO-LAB & KAGGLER WORKSPACE",
                                color = NeonGold,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        Text(
                            text = "Music Video LipSync",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        Text(
                            text = "Set up, compile and automatically generate ready-to-run Google Colab or Kaggle pipelines. Synchronize custom portrait images with speech and background music entirely using open-weight architectures.",
                            color = MutedText,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        Button(
                            onClick = { viewModel.setScreen("Create") },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .testTag("create_new_pipeline_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add Icon", tint = Color.White)
                                Text("New LipSync Job", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Section Title: Historic Pipelines
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MY PIPELINES (${allJobs.size})",
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    letterSpacing = 1.sp
                )

                if (allJobs.isNotEmpty()) {
                    Text(
                        text = "Tap to open workspace",
                        fontSize = 12.sp,
                        color = MutedText
                    )
                }
            }
        }

        // Empty State Check
        if (allJobs.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(SurfaceCard),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.HearingDisabled,
                            contentDescription = "Empty state icon",
                            tint = MutedText,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Text(
                        text = "No Pipelines Configured",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = "Configure your first portrait and song track to generate a customized notebook deployment script.",
                        textAlign = TextAlign.Center,
                        color = MutedText,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    OutlinedButton(
                        onClick = { viewModel.setScreen("Create") },
                        border = BorderStroke(1.dp, NeonCyan),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Create Portals", color = NeonCyan)
                    }
                }
            }
        } else {
            items(allJobs) { job ->
                PipelineCard(
                    job = job,
                    onTap = { onNavigateToDetails(job) },
                    onDelete = { viewModel.deleteJob(job) }
                )
            }
        }
    }
}

@Composable
fun PipelineCard(
    job: LipSyncJob,
    onTap: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onTap)
            .testTag("job_card_${job.id}"),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderSlate),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Coil Portrait Image
            AsyncImage(
                model = job.imageUrl,
                contentDescription = job.characterName,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, BorderSlate, RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            // Mid Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = job.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Model: ${job.selectedModel}",
                        color = MutedText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Box(
                        modifier = Modifier
                            .size(3.dp)
                            .clip(CircleShape)
                            .background(MutedText)
                    )
                    Text(
                        text = job.platform,
                        color = MutedText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Render dynamic status badge
                val badgeColor = when (job.status) {
                    "Completed" -> MintGreen
                    "Running" -> NeonGold
                    else -> NeonCyan
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = job.status.uppercase(),
                        color = badgeColor,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Right Actions: Delete button
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("delete_job_btn_${job.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete Job",
                    tint = Color.Red.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ==========================================
// 2. CREATE SCREEN (BUILD HUB)
// ==========================================
@Composable
fun CreateScreen(viewModel: LipSyncViewModel) {
    val jobNameVal by viewModel.jobName.collectAsStateWithLifecycle()
    val characterNameVal by viewModel.characterName.collectAsStateWithLifecycle()
    val imageUrlVal by viewModel.imageUrl.collectAsStateWithLifecycle()
    val audioUrlVal by viewModel.audioUrl.collectAsStateWithLifecycle()
    val selectedModelVal by viewModel.selectedModel.collectAsStateWithLifecycle()
    val platformVal by viewModel.platform.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "BUILD PIPELINE",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "Specify custom assets or pick templates to assemble the deployment scripts.",
                color = MutedText,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Job Name Input
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Project Label", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                OutlinedTextField(
                    value = jobNameVal,
                    onValueChange = { viewModel.jobName.value = it },
                    placeholder = { Text("e.g., Cyber Samurai Epic Sync", color = MutedText) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_job_name"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderSlate,
                        focusedContainerColor = SurfaceCard,
                        unfocusedContainerColor = SurfaceCard,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
            }
        }

        // Model selection
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Select LipSync Model", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val modelsList = listOf("LivePortrait", "SadTalker", "Wav2Lip")
                    modelsList.forEach { model ->
                        val isSelected = selectedModelVal == model
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) NeonViolet.copy(alpha = 0.25f) else SurfaceCard)
                                .border(
                                    1.dp,
                                    if (isSelected) NeonViolet else BorderSlate,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.selectedModel.value = model }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = model,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isSelected) NeonCyan else Color.White
                                )
                                Text(
                                    text = if (model == "LivePortrait") "Realistic fast" else if (model == "SadTalker") "Expressive Still" else "Lip Match",
                                    fontSize = 9.sp,
                                    color = MutedText
                                )
                            }
                        }
                    }
                }
            }
        }

        // Platform selection
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Deployment Platform", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Google Colab", "Kaggle").forEach { plat ->
                        val isSelected = platformVal == plat
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) NeonCyan.copy(alpha = 0.15f) else SurfaceCard)
                                .border(
                                    1.dp,
                                    if (isSelected) NeonCyan else BorderSlate,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.platform.value = plat }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = plat,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isSelected) NeonCyan else Color.White
                            )
                        }
                    }
                }
            }
        }

        // Portrait Character (Image) Input with preview
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Character Portrait URL", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(
                        text = "Preset Library available",
                        color = NeonGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { viewModel.setScreen("Presets") }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = imageUrlVal,
                        onValueChange = { viewModel.imageUrl.value = it },
                        placeholder = { Text("https://url.to/character_face.jpg", color = MutedText) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_image_url"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = BorderSlate,
                            focusedContainerColor = SurfaceCard,
                            unfocusedContainerColor = SurfaceCard,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )

                    // Coil Dynamic Preview
                    if (imageUrlVal.isNotEmpty()) {
                        AsyncImage(
                            model = imageUrlVal,
                            contentDescription = "Portrait Preview",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, BorderSlate, RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceCard)
                                .border(1.dp, BorderSlate, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Face Placeholder", tint = MutedText)
                        }
                    }
                }
                Text(
                    text = "Provide a high-contrast front facing face portrait for optimal facial landmark alignment.",
                    color = MutedText,
                    fontSize = 11.sp
                )
            }
        }

        // Audio Track URL Input
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Audio Track URL", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                OutlinedTextField(
                    value = audioUrlVal,
                    onValueChange = { viewModel.audioUrl.value = it },
                    placeholder = { Text("https://url.to/song_track.mp3", color = MutedText) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_audio_url"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderSlate,
                        focusedContainerColor = SurfaceCard,
                        unfocusedContainerColor = SurfaceCard,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                )
                Text(
                    text = "Accepts direct MP3, WAV, or AAC song/speech links. Make sure your link bypasses login walls.",
                    color = MutedText,
                    fontSize = 11.sp
                )
            }
        }

        // SMART CHUNKING SECTION (Free Tier Optimization)
        item {
            val enableChunkingVal by viewModel.enableChunking.collectAsStateWithLifecycle()
            val chunkDurationVal by viewModel.chunkDuration.collectAsStateWithLifecycle()
            val cameraMovementPromptVal by viewModel.cameraMovementPrompt.collectAsStateWithLifecycle()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceCard)
                    .border(1.dp, BorderSlate, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Grid4x4,
                            contentDescription = "Chunking",
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "SMART PIPELINE CHUNKING",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Switch(
                        checked = enableChunkingVal,
                        onCheckedChange = { viewModel.enableChunking.value = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NeonCyan,
                            checkedTrackColor = NeonViolet.copy(alpha = 0.5f)
                        )
                    )
                }

                Text(
                    text = "Bypasses CUDA out-of-memory and free tier Colab/Kaggle runtime limits by splitting long audio files into short clips, rendering them in parallel, and stitching them together.",
                    color = MutedText,
                    fontSize = 11.sp
                )

                if (enableChunkingVal) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Split Clip Duration: ${chunkDurationVal}s",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Slider(
                            value = chunkDurationVal.toFloat(),
                            onValueChange = { viewModel.chunkDuration.value = it.toInt() },
                            valueRange = 5f..30f,
                            steps = 4,
                            colors = SliderDefaults.colors(
                                thumbColor = NeonCyan,
                                activeTrackColor = NeonCyan,
                                inactiveTrackColor = BorderSlate
                            )
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Master Camera Motion Prompt",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedTextField(
                            value = cameraMovementPromptVal,
                            onValueChange = { viewModel.cameraMovementPrompt.value = it },
                            placeholder = { Text("e.g. Dynamic slow forward zoom with cyber pan left", color = MutedText) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = BorderSlate,
                                focusedContainerColor = DeepSlate,
                                unfocusedContainerColor = DeepSlate,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Action Buttons: Assemble Pipeline
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    keyboardController?.hide()
                    viewModel.createLipSyncProject {
                        Toast.makeText(context, "Pipeline Assembled Successfully!", Toast.LENGTH_SHORT).show()
                        viewModel.setScreen("Details")
                    }
                },
                enabled = !isGenerating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_pipeline_generation"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
            ) {
                if (isGenerating) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(color = DeepSlate, modifier = Modifier.size(20.dp))
                        Text(
                            text = "COMPILING PIPELINE WITH GEMINI...",
                            color = DeepSlate,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Terminal, contentDescription = "Terminal icon", tint = DeepSlate)
                        Text(
                            text = "ASSEMBLE PIPELINE PORTALS",
                            color = DeepSlate,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. DETAILS / WORKSPACE SCREEN (RUNNER)
// ==========================================
@Composable
fun DetailsScreen(
    viewModel: LipSyncViewModel,
    job: LipSyncJob,
    onBack: () -> Unit
) {
    val terminalLogs by viewModel.terminalLogs.collectAsStateWithLifecycle()
    val runnerProgress by viewModel.runnerProgress.collectAsStateWithLifecycle()
    val isRunnerActive by viewModel.isRunnerActive.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Column {
                    Text(
                        text = job.name.uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Pipeline Details & Notebook Runner",
                        color = MutedText,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Side-by-side Assets Setup
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderSlate),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "INPUT ASSETS",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = job.imageUrl,
                            contentDescription = job.characterName,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, BorderSlate, RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Character: ${job.characterName}",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Model: ${job.selectedModel}",
                                color = MutedText,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Target: ${job.platform}",
                                color = MutedText,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Divider(color = BorderSlate)

                    // Audio URL display
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.MusicNote, contentDescription = "Audio track", tint = NeonGold)
                        Text(
                            text = job.audioUrl,
                            color = Color.White,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Generated Deployment Script Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DEPLOYMENT PYTHON SCRIPT",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )

                    // Copy action
                    Row(
                        modifier = Modifier
                            .clickable {
                                clipboardManager.setText(AnnotatedString(job.generatedCode))
                                Toast.makeText(context, "Copied Python script to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = NeonGold, modifier = Modifier.size(14.dp))
                        Text("COPY CODE", color = NeonGold, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .border(1.dp, BorderSlate, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                        SelectionContainer {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                item {
                                    Text(
                                        text = job.generatedCode,
                                        style = TextStyle(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            color = MutedText
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active simulator / Runner card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, NeonViolet.copy(alpha = 0.3f)),
                colors = CardDefaults.cardColors(containerColor = SurfaceSlate)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Run", tint = MintGreen)
                            Text(
                                text = "LIPSYNC COMPILATION RUNNER",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MintGreen
                            )
                        }

                        if (!isRunnerActive && job.videoUrl == null) {
                            Text(
                                text = "Simulate local compile",
                                color = MutedText,
                                fontSize = 11.sp
                            )
                        }
                    }

                    if (job.videoUrl != null) {
                        // Display completed media container
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, NeonCyan, RoundedCornerShape(12.dp))
                                .background(SurfaceCard),
                            contentAlignment = Alignment.Center
                        ) {
                            // Render character with glowing details as looping video placeholder
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = job.imageUrl,
                                    contentDescription = "Looping portrait preview",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                // Overlay glowing neon grid and playback bar to simulate real playing output
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                            )
                                        )
                                )

                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayCircleFilled,
                                            contentDescription = "Playing",
                                            tint = NeonCyan,
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Column {
                                            Text(
                                                text = "Simulating: Output_Anim.mp4",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "HD 1080p | 30 FPS | Face-Enhanced",
                                                color = MutedText,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    LinearProgressIndicator(
                                        progress = 0.5f,
                                        modifier = Modifier.fillMaxWidth().height(4.dp),
                                        color = NeonCyan,
                                        trackColor = BorderSlate
                                    )
                                }
                            }
                        }

                        // Siri asset guide
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            border = BorderStroke(1.dp, BorderSlate),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(imageVector = Icons.Default.StarBorder, contentDescription = "Siri Action", tint = NeonGold)
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "⚡ Siri Assets Configured!",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "To trigger voice-animated mouth lip sync on iOS, copy this video's reference to Files, open Apple Shortcuts, create an action with 'Play Video' mapped to your sync word, and configure Siri trigger words directly on your device.",
                                        color = MutedText,
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    } else if (isRunnerActive) {
                        // Render logs and progress
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            LinearProgressIndicator(
                                progress = runnerProgress,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = NeonCyan,
                                trackColor = BorderSlate
                            )

                            Text(
                                text = "Compilation Progress: ${(runnerProgress * 100).toInt()}%",
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )

                            // Console container
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Black)
                            ) {
                                val logsListState = rememberLazyListState()
                                LaunchedEffect(terminalLogs.size) {
                                    if (terminalLogs.isNotEmpty()) {
                                        logsListState.animateScrollToItem(terminalLogs.size - 1)
                                    }
                                }

                                LazyColumn(
                                    state = logsListState,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                ) {
                                    items(terminalLogs) { log ->
                                        Text(
                                            text = "> $log",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            color = if (log.contains("Success") || log.contains("Deploying")) MintGreen else Color.White,
                                            modifier = Modifier.padding(vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Inactive, prompt user to simulate run
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Run local simulation to package visual outputs and setup device shortcuts assets.",
                                textAlign = TextAlign.Center,
                                color = MutedText,
                                fontSize = 12.sp
                            )

                            Button(
                                onClick = { viewModel.simulatePipelineRunner(job) },
                                colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Compile", tint = DeepSlate)
                                    Text("Compile & Package Assets", color = DeepSlate, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. PRESETS LIBRARY SCREEN
// ==========================================
@Composable
fun PresetsScreen(viewModel: LipSyncViewModel) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "ASSETS PRESET LIBRARY",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "Tap any preset character or audio song loop to automatically load them into the Build tab.",
                color = MutedText,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Section: Portrait Characters
        item {
            Text(
                text = "PRESET CHARACTERS",
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                letterSpacing = 1.sp
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(viewModel.presetCharacters) { char ->
                    Card(
                        modifier = Modifier
                            .width(180.dp)
                            .clickable {
                                viewModel.selectPresetCharacter(char)
                                Toast
                                    .makeText(
                                        context,
                                        "Loaded ${char.name} Character!",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                                viewModel.setScreen("Create")
                            },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, BorderSlate),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                    ) {
                        Column {
                            AsyncImage(
                                model = char.imageUrl,
                                contentDescription = char.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = char.name,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = char.style,
                                    color = NeonGold,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = char.description,
                                    color = MutedText,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Royalty-Free Audio loops
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "PRESET SONG LOOPS",
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                letterSpacing = 1.sp
            )
        }

        items(viewModel.presetAudioClips) { audio ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.selectPresetAudio(audio)
                        Toast
                            .makeText(context, "Loaded ${audio.title} Song Track!", Toast.LENGTH_SHORT)
                            .show()
                        viewModel.setScreen("Create")
                    },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderSlate),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(NeonGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.MusicNote, contentDescription = "Music", tint = NeonGold)
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = audio.title,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Text(
                                text = audio.duration,
                                color = MutedText,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Text(
                            text = audio.genre,
                            color = NeonCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )

                        Text(
                            text = audio.description,
                            color = MutedText,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 5. CO-PILOT CHAT SCREEN
// ==========================================
@Composable
fun ChatScreen(viewModel: LipSyncViewModel) {
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isChatLoading by viewModel.isChatLoading.collectAsStateWithLifecycle()
    var userQuery by remember { mutableStateFlowOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    val troubleshootingTopics = listOf(
        "Fix CUDA out of memory",
        "Explain SadTalker still option",
        "How do I setup Kaggle keys?",
        "Siri Shortcuts voice sync tutorial"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Chat Screen Title
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "CO-PILOT AI ENGINEER",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "Troubleshoot Colab runs, request parameters optimization, or explain lip sync mechanics.",
                color = MutedText,
                fontSize = 12.sp
            )
        }

        // Horizontal Quick-Topic suggestion chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(troubleshootingTopics) { topic ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceCard)
                        .border(1.dp, BorderSlate, RoundedCornerShape(20.dp))
                        .clickable {
                            viewModel.sendChatMessage(topic)
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = topic,
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Chat Conversation Logs (Expanded viewport)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(1.dp, BorderSlate, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard)
        ) {
            val listState = rememberLazyListState()
            LaunchedEffect(chatMessages.size) {
                if (chatMessages.isNotEmpty()) {
                    listState.animateScrollToItem(chatMessages.size - 1)
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(chatMessages) { msg ->
                    val isAssistant = msg.role == "assistant"
                    val alignment = if (isAssistant) Alignment.Start else Alignment.End
                    val bubbleColor = if (isAssistant) SurfaceSlate else NeonViolet.copy(alpha = 0.25f)
                    val borderStroke = if (isAssistant) BorderStroke(1.dp, BorderSlate) else BorderStroke(1.dp, NeonViolet)

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = alignment
                    ) {
                        Text(
                            text = if (isAssistant) "⚡ PIPELINE ENGINEER" else "YOU",
                            fontSize = 9.sp,
                            color = if (isAssistant) NeonGold else NeonCyan,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 2.dp, start = 4.dp, end = 4.dp)
                        )

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            border = borderStroke,
                            colors = CardDefaults.cardColors(containerColor = bubbleColor),
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Text(
                                text = msg.message,
                                color = Color.White,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }

                if (isChatLoading) {
                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            CircularProgressIndicator(color = NeonCyan, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Assistant is analyzing settings...",
                                color = MutedText,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Input send box
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = userQuery,
                onValueChange = { userQuery = it },
                placeholder = { Text("Ask about Colab GPU issues...", color = MutedText) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("input_chat_box"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = BorderSlate,
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                maxLines = 2,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (userQuery.isNotBlank()) {
                        viewModel.sendChatMessage(userQuery)
                        userQuery = ""
                        keyboardController?.hide()
                    }
                })
            )

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(accentGradient)
                    .clickable {
                        if (userQuery.isNotBlank()) {
                            viewModel.sendChatMessage(userQuery)
                            userQuery = ""
                            keyboardController?.hide()
                        }
                    }
                    .testTag("submit_chat_query"),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send Message", tint = Color.White)
            }
        }
    }
}

// Utility extension for mutableStateOf inside Composables
private fun <T> mutableStateFlowOf(value: T) = mutableStateOf(value)

@Composable
fun StudioEditorScreen(viewModel: LipSyncViewModel) {
    val clipSegments by viewModel.clipSegments.collectAsStateWithLifecycle()
    val textOverlays by viewModel.textOverlays.collectAsStateWithLifecycle()
    val stitchedVideoUrl by viewModel.stitchedVideoUrl.collectAsStateWithLifecycle()
    val isStitching by viewModel.isStitching.collectAsStateWithLifecycle()
    val selectedJob by viewModel.selectedJob.collectAsStateWithLifecycle()

    var showAddOverlayDialog by remember { mutableStateOf(false) }
    var showImportClipDialog by remember { mutableStateOf(false) }

    // Dialog form state variables
    var overlayTextVal by remember { mutableStateOf("") }
    var overlayColorHexVal by remember { mutableStateOf("#D0BCFF") }
    var overlayFontSizeVal by remember { mutableStateOf(20) }
    var overlayPositionVal by remember { mutableStateOf("Center") }
    var overlayAnimationVal by remember { mutableStateOf("Static") }
    var overlayDurationVal by remember { mutableStateOf(5) }

    var importClipNameVal by remember { mutableStateOf("") }
    var importClipDurationVal by remember { mutableStateOf(15) }
    var importClipUrlVal by remember { mutableStateOf("") }

    // Live preview states
    val infiniteTransition = rememberInfiniteTransition(label = "player")
    
    // Wave oscillation for bouncy animations
    val bounceY by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    // Pulse alpha oscillation
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Shift offset for sliding wavy animation
    val shiftX by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shift"
    )

    // Video noise flicker for CRT Effect
    val crtFlicker by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(150),
            repeatMode = RepeatMode.Reverse
        ),
        label = "crt"
    )

    // Scaling action for camera pans
    val zoomScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "zoom"
    )

    // Wobble offset for shake filters
    val shakeOffset by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(80),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shake"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Master Title Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "STUDIO EDITOR",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Stitch generated chunks, apply effects, and overlay kinetic captions.",
                        color = MutedText,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                IconButton(
                    onClick = { viewModel.setScreen("Dashboard") },
                    modifier = Modifier.background(SurfaceCard, CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }

        // Active project selection alert if null
        if (selectedJob == null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, BorderSlate)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.MovieFilter, contentDescription = "Select Project", tint = NeonGold, modifier = Modifier.size(32.dp))
                        Text(
                            text = "No Active LipSync Project Selected",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "To start editing clips, go back to the Studio dashboard, pick an existing pipeline run, and tap on it.",
                            color = MutedText,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { viewModel.setScreen("Dashboard") },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("GO TO STUDIO DASHBOARD", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        } else {
            // Live Preview Player Frame with Applied Filters & Kinetic Caption Overlays
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "STUDIO LIVE MONITOR",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )

                    // Find active clip's filter to demo live
                    val activeFilter = clipSegments.firstOrNull()?.effect ?: "None"

                    // Setup graphical modifiers matching active visual effects
                    val previewModifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black)
                        .border(1.dp, BorderSlate, RoundedCornerShape(20.dp))
                        .drawBehind {
                            if (activeFilter == "Retro Vapor") {
                                // Draw a soft vaporwave purple/gold ambient gradient backing
                                drawRect(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFF3B1E54),
                                            Color(0xFFEE82EE),
                                            Color(0xFFFFC400)
                                        )
                                    ),
                                    alpha = 0.4f
                                )
                            }
                        }

                    Box(
                        modifier = previewModifier,
                        contentAlignment = Alignment.Center
                    ) {
                        // Base Video/Image portrait
                        val previewUrl = selectedJob?.imageUrl ?: "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=500"
                        
                        // Apply transformations matching selected clip filters
                        val scale = if (activeFilter == "Slow Pan Zoom") zoomScale else 1.0f
                        val alpha = if (activeFilter == "CRT Flicker") crtFlicker else 1.0f
                        val offsetX = if (activeFilter == "RGB Shake") shakeOffset else 0f

                        AsyncImage(
                            model = previewUrl,
                            contentDescription = "Live Portrait Preview",
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale,
                                    alpha = alpha,
                                    translationX = offsetX
                                ),
                            contentScale = ContentScale.Crop
                        )

                        // If CRT effect is active, draw Scanlines on top
                        if (activeFilter == "CRT Flicker") {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = List(40) { index ->
                                                if (index % 2 == 0) Color.Transparent else Color.Black.copy(alpha = 0.15f)
                                            }
                                        )
                                    )
                            )
                        }

                        // Render Kinetic Text Captions Overlay on top of player
                        textOverlays.forEach { overlay ->
                            // Custom position offsets
                            val align = when (overlay.position) {
                                "Top" -> Alignment.TopCenter
                                "Bottom" -> Alignment.BottomCenter
                                else -> Alignment.Center
                            }

                            val textColor = try {
                                Color(android.graphics.Color.parseColor(overlay.colorHex))
                            } catch (e: Exception) {
                                NeonViolet
                            }

                            // Dynamic movement modifier
                            val motionModifier = when (overlay.animation) {
                                "Bounce" -> Modifier.graphicsLayer(translationY = bounceY)
                                "Pulse" -> Modifier.graphicsLayer(alpha = pulseAlpha)
                                "Wavy" -> Modifier.graphicsLayer(translationX = shiftX)
                                else -> Modifier
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = align
                            ) {
                                Text(
                                    text = overlay.text,
                                    color = textColor,
                                    fontSize = overlay.fontSize.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.SansSerif,
                                    style = TextStyle(
                                        shadow = androidx.compose.ui.graphics.Shadow(
                                            color = Color.Black,
                                            offset = Offset(2f, 2f),
                                            blurRadius = 4f
                                        )
                                    ),
                                    modifier = motionModifier
                                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Stitched completion overlay or rendering indicator
                        if (isStitching) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.8f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    CircularProgressIndicator(color = NeonCyan, modifier = Modifier.size(36.dp))
                                    Text(
                                        text = "AUTO-STITCHING CLIPS & AUDIO...",
                                        color = NeonCyan,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Combining blocks • Synchronizing wave frequencies • Injecting effects",
                                        color = MutedText,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        // Play/Pause Floating indicators
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.7f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Playing", tint = MintGreen, modifier = Modifier.size(12.dp))
                                Text(
                                    text = "LIVE RENDER PREVIEW",
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MintGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Display active filter tag
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Primary Active FX Filter: $activeFilter",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Stitched Audio Track synced",
                            color = MutedText,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // AUTO-STITCH & INJECT BUTTONS
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.autoStitchClips() },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isStitching
                    ) {
                        Icon(imageVector = Icons.Default.VideoSettings, contentDescription = "Stitch", tint = Purple40)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AUTO-STITCH VIDEO",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            color = Purple40,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    OutlinedButton(
                        onClick = { showImportClipDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.dp, BorderSlate),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.UploadFile, contentDescription = "Import", tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "INJECT CLIP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }
                }
            }

            // TIMELINE TRACK EDITOR
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "VISUAL TIMELINE TRACK (${clipSegments.size} CLIPS)",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )

                    if (clipSegments.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceCard)
                                .border(1.dp, BorderSlate, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Timeline is empty. Inject a video clip or deploy a pipeline.", color = MutedText, fontSize = 12.sp)
                        }
                    } else {
                        // Horizontal/Vertical Scroll track list
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            clipSegments.forEachIndexed { index, clip ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                    border = BorderStroke(1.dp, if (index == 0) NeonCyan.copy(alpha = 0.5f) else BorderSlate)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            // Slice Index indicator
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(if (index == 0) NeonCyan else BorderSlate),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "${index + 1}",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = if (index == 0) Purple40 else Color.White
                                                )
                                            }

                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    Text(
                                                        text = clip.name,
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        modifier = Modifier.widthIn(max = 130.dp)
                                                    )
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(if (clip.isLocal) NeonGold.copy(alpha = 0.2f) else NeonViolet.copy(alpha = 0.2f))
                                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = if (clip.isLocal) "DEVICE" else "GENERATED",
                                                            fontSize = 8.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (clip.isLocal) NeonGold else NeonViolet
                                                        )
                                                    }
                                                }
                                                
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                    modifier = Modifier.padding(top = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "Duration: ${clip.durationSec}s",
                                                        color = MutedText,
                                                        fontSize = 10.sp
                                                    )
                                                    Text(
                                                        text = "•",
                                                        color = BorderSlate,
                                                        fontSize = 10.sp
                                                    )
                                                    Text(
                                                        text = "FX: ${clip.effect}",
                                                        color = NeonCyan,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }

                                        // Clip Controls: reorder, apply effects, delete
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            // Effect Selection Chips/Wheel
                                            IconButton(
                                                onClick = {
                                                    val nextEffect = when (clip.effect) {
                                                        "None" -> "CRT Flicker"
                                                        "CRT Flicker" -> "Slow Pan Zoom"
                                                        "Slow Pan Zoom" -> "RGB Shake"
                                                        "RGB Shake" -> "Retro Vapor"
                                                        else -> "None"
                                                    }
                                                    viewModel.updateClipEffect(clip.id, nextEffect)
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(imageVector = Icons.Default.FilterVintage, contentDescription = "FX", tint = NeonViolet, modifier = Modifier.size(16.dp))
                                            }

                                            // Reorder up
                                            IconButton(
                                                onClick = { viewModel.reorderClip(index, index - 1) },
                                                modifier = Modifier.size(28.dp),
                                                enabled = index > 0
                                            ) {
                                                Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Move Up", tint = if (index > 0) Color.White else BorderSlate, modifier = Modifier.size(16.dp))
                                            }

                                            // Reorder down
                                            IconButton(
                                                onClick = { viewModel.reorderClip(index, index + 1) },
                                                modifier = Modifier.size(28.dp),
                                                enabled = index < clipSegments.size - 1
                                            ) {
                                                Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "Move Down", tint = if (index < clipSegments.size - 1) Color.White else BorderSlate, modifier = Modifier.size(16.dp))
                                            }

                                            // Delete clip
                                            IconButton(
                                                onClick = { viewModel.removeClip(clip.id) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // KINETIC CAPTION DESIGNER OVERLAYS
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "KINETIC CAPTION OVERLAYS",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "+ ADD TEXT",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { showAddOverlayDialog = true }
                                .padding(vertical = 4.dp)
                        )
                    }

                    if (textOverlays.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceCard)
                                .border(1.dp, BorderSlate, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No caption overlays active. Tap '+ ADD TEXT'.", color = MutedText, fontSize = 12.sp)
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            textOverlays.forEach { overlay ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                    border = BorderStroke(1.dp, BorderSlate)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            // Text color dot indicator
                                            val badgeColor = try {
                                                Color(android.graphics.Color.parseColor(overlay.colorHex))
                                            } catch (e: Exception) {
                                                NeonViolet
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(badgeColor)
                                            )

                                            Column {
                                                Text(
                                                    text = "\"${overlay.text}\"",
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp
                                                )
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                    modifier = Modifier.padding(top = 2.dp)
                                                ) {
                                                    Text("Motion: ${overlay.animation}", color = NeonCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                    Text("•", color = BorderSlate, fontSize = 9.sp)
                                                    Text("Size: ${overlay.fontSize}sp", color = MutedText, fontSize = 9.sp)
                                                    Text("•", color = BorderSlate, fontSize = 9.sp)
                                                    Text("Align: ${overlay.position}", color = MutedText, fontSize = 9.sp)
                                                }
                                            }
                                        }

                                        IconButton(
                                            onClick = { viewModel.removeTextOverlay(overlay.id) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 1. ADD CAPTION TEXT DIALOG
    if (showAddOverlayDialog) {
        AlertDialog(
            onDismissRequest = { showAddOverlayDialog = false },
            title = {
                Text(
                    text = "DESIGN KINETIC CAPTION",
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            containerColor = SurfaceSlate,
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = overlayTextVal,
                        onValueChange = { overlayTextVal = it },
                        placeholder = { Text("Overlay Subtitle Text...", color = MutedText) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = BorderSlate,
                            focusedContainerColor = SurfaceCard,
                            unfocusedContainerColor = SurfaceCard
                        ),
                        maxLines = 1
                    )

                    // Color selection dots
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Font Accent Color", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            val hexOptions = listOf("#D0BCFF", "#B1D18A", "#FFC400", "#FFFFFF", "#FF5252")
                            hexOptions.forEach { hex ->
                                val dotColor = Color(android.graphics.Color.parseColor(hex))
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(dotColor)
                                        .border(2.dp, if (overlayColorHexVal == hex) NeonCyan else Color.Transparent, CircleShape)
                                        .clickable { overlayColorHexVal = hex }
                                )
                            }
                        }
                    }

                    // Font Size slide
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Font Size: ${overlayFontSizeVal}sp", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Slider(
                            value = overlayFontSizeVal.toFloat(),
                            onValueChange = { overlayFontSizeVal = it.toInt() },
                            valueRange = 12f..36f,
                            colors = SliderDefaults.colors(activeTrackColor = NeonCyan, thumbColor = NeonCyan)
                        )
                    }

                    // Alignment position
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Screen Anchor Position", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Top", "Center", "Bottom").forEach { pos ->
                                val isSelected = overlayPositionVal == pos
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) NeonCyan.copy(alpha = 0.2f) else SurfaceCard)
                                        .border(1.dp, if (isSelected) NeonCyan else BorderSlate, RoundedCornerShape(8.dp))
                                        .clickable { overlayPositionVal = pos }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(pos, fontSize = 10.sp, color = if (isSelected) NeonCyan else Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Kinetic animation motion
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Kinetic Motion FX", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("Static", "Bounce", "Pulse", "Wavy").forEach { motion ->
                                val isSelected = overlayAnimationVal == motion
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) NeonViolet.copy(alpha = 0.2f) else SurfaceCard)
                                        .border(1.dp, if (isSelected) NeonViolet else BorderSlate, RoundedCornerShape(8.dp))
                                        .clickable { overlayAnimationVal = motion }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(motion, fontSize = 9.sp, color = if (isSelected) NeonCyan else Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (overlayTextVal.isNotBlank()) {
                            viewModel.addTextOverlay(
                                text = overlayTextVal,
                                colorHex = overlayColorHexVal,
                                fontSize = overlayFontSizeVal,
                                position = overlayPositionVal,
                                animation = overlayAnimationVal,
                                duration = overlayDurationVal
                            )
                            overlayTextVal = ""
                            showAddOverlayDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("ADD LAYER", color = Purple40, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddOverlayDialog = false }) {
                    Text("CANCEL", color = Color.White)
                }
            }
        )
    }

    // 2. INJECT DEVICE CUSTOM MEDIA CLIP DIALOG
    if (showImportClipDialog) {
        AlertDialog(
            onDismissRequest = { showImportClipDialog = false },
            title = {
                Text(
                    text = "INJECT LOCAL DEVICE CLIP",
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            containerColor = SurfaceSlate,
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Simulate picking recorded footage from your device storage to stitch together with the pipeline-generated audio layers.",
                        color = MutedText,
                        fontSize = 11.sp
                    )

                    OutlinedTextField(
                        value = importClipNameVal,
                        onValueChange = { importClipNameVal = it },
                        placeholder = { Text("e.g. My Dancing Video 01.mp4", color = MutedText) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = BorderSlate,
                            focusedContainerColor = SurfaceCard,
                            unfocusedContainerColor = SurfaceCard
                        ),
                        maxLines = 1
                    )

                    // Slider for simulated duration
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Clip Trim Duration: ${importClipDurationVal}s", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Slider(
                            value = importClipDurationVal.toFloat(),
                            onValueChange = { importClipDurationVal = it.toInt() },
                            valueRange = 5f..40f,
                            colors = SliderDefaults.colors(activeTrackColor = NeonCyan, thumbColor = NeonCyan)
                        )
                    }

                    // Simulated source selection
                    Text("Select Media Template", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val templates = listOf(
                            "DJ Deck" to "https://assets.mixkit.co/videos/preview/mixkit-hands-of-a-dj-controlling-music-on-sound-mixer-43093-large.mp4",
                            "Cyber Loop" to "https://assets.mixkit.co/videos/preview/mixkit-cyberpunk-woman-with-neon-details-portrait-40032-large.mp4",
                            "Concert" to "https://assets.mixkit.co/videos/preview/mixkit-concert-crowd-raising-hands-under-colorful-stage-lights-42512-large.mp4"
                        )
                        templates.forEach { (label, url) ->
                            val isSelected = importClipUrlVal == url
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) NeonCyan.copy(alpha = 0.2f) else SurfaceCard)
                                    .border(1.dp, if (isSelected) NeonCyan else BorderSlate, RoundedCornerShape(8.dp))
                                    .clickable {
                                        importClipUrlVal = url
                                        if (importClipNameVal.isEmpty()) {
                                            importClipNameVal = "$label Raw Clip"
                                        }
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(label, fontSize = 10.sp, color = if (isSelected) NeonCyan else Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalName = importClipNameVal.ifEmpty { "Custom Media Loop" }
                        val finalUrl = importClipUrlVal.ifEmpty { "https://assets.mixkit.co/videos/preview/mixkit-cyberpunk-woman-with-neon-details-portrait-40032-large.mp4" }
                        viewModel.addLocalClip(finalName, importClipDurationVal, finalUrl)
                        importClipNameVal = ""
                        importClipUrlVal = ""
                        showImportClipDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("INJECT TO TIMELINE", color = Purple40, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportClipDialog = false }) {
                    Text("CANCEL", color = Color.White)
                }
            }
        )
    }
}
