package com.example.ui.components

import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.view.Surface
import android.view.TextureView
import androidx.annotation.RawRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.DeFittBlack
import com.example.ui.theme.DeFittBorder
import com.example.ui.theme.DeFittSurfaceCard
import com.example.ui.theme.DeFittWhite
import com.example.ui.theme.NeonGreen

/**
 * Plays a bundled offline 5-second MP4 exercise demonstration video automatically in a continuous loop.
 * Zero network access required.
 */
@Composable
fun ExerciseVideoPlayer(
    @RawRes rawResId: Int?,
    illustrationKey: String,
    exerciseName: String,
    modifier: Modifier = Modifier
) {
    var isVideoRendered by remember(rawResId) { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(4f / 3f)
            .testTag("exercise_video_player"),
        shape = RoundedCornerShape(22.dp),
        color = DeFittSurfaceCard,
        border = BorderStroke(1.5.dp, NeonGreen.copy(alpha = 0.55f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFF0A0F0C))
        ) {
            // Fallback preview shown while surface initializes or in headless test environments
            if (!isVideoRendered || rawResId == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ExerciseIllustration(
                        illustrationKey = illustrationKey,
                        modifier = Modifier.size(120.dp)
                    )
                }
            }

            if (rawResId != null) {
                var activeMediaPlayer by remember(rawResId) { mutableStateOf<MediaPlayer?>(null) }

                DisposableEffect(rawResId) {
                    onDispose {
                        runCatching {
                            activeMediaPlayer?.stop()
                        }
                        runCatching {
                            activeMediaPlayer?.release()
                        }
                        activeMediaPlayer = null
                    }
                }

                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        TextureView(ctx).apply {
                            contentDescription = "Video demostrativo de $exerciseName"
                            surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                                override fun onSurfaceTextureAvailable(
                                    surfaceTexture: SurfaceTexture,
                                    width: Int,
                                    height: Int
                                ) {
                                    runCatching {
                                        val surface = Surface(surfaceTexture)
                                        val player = MediaPlayer()
                                        ctx.resources.openRawResourceFd(rawResId)?.use { afd ->
                                            player.setDataSource(
                                                afd.fileDescriptor,
                                                afd.startOffset,
                                                afd.length
                                            )
                                        }
                                        player.setSurface(surface)
                                        player.isLooping = true
                                        player.setVolume(0f, 0f)
                                        player.setOnPreparedListener { mp ->
                                            isVideoRendered = true
                                            mp.start()
                                        }
                                        player.setOnErrorListener { _, _, _ ->
                                            isVideoRendered = false
                                            true
                                        }
                                        player.prepareAsync()
                                        activeMediaPlayer = player
                                    }
                                }

                                override fun onSurfaceTextureSizeChanged(
                                    surface: SurfaceTexture,
                                    width: Int,
                                    height: Int
                                ) = Unit

                                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                                    runCatching { activeMediaPlayer?.stop() }
                                    runCatching { activeMediaPlayer?.release() }
                                    activeMediaPlayer = null
                                    isVideoRendered = false
                                    return true
                                }

                                override fun onSurfaceTextureUpdated(surface: SurfaceTexture) = Unit
                            }
                        }
                    }
                )
            }

            // Clean offline loop badge overlay
            Surface(
                shape = RoundedCornerShape(50),
                color = DeFittBlack.copy(alpha = 0.78f),
                border = BorderStroke(1.dp, DeFittBorder),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay,
                        contentDescription = null,
                        tint = NeonGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "5s · BUCLE OFFLINE",
                        style = MaterialTheme.typography.labelSmall,
                        color = DeFittWhite
                    )
                }
            }
        }
    }
}
