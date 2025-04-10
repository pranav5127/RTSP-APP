package com.app.rtspapp.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSession
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import androidx.core.net.toUri

@HiltViewModel
class StreamScreenViewModel @Inject constructor(
    val exoPlayer: ExoPlayer,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var url = mutableStateOf("")
        private set

    var isRecording = mutableStateOf(false)
        private set

    private var ffmpegSession: FFmpegSession? = null

    init {
        Log.d("StreamScreenViewModel", "Initializing ViewModel and setting up ExoPlayer listener.")
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                Log.e("StreamScreenViewModel", "Playback error: ${error.message}", error)
            }

            override fun onPlaybackStateChanged(state: Int) {
                val stateStr = when (state) {
                    Player.STATE_BUFFERING -> "BUFFERING"
                    Player.STATE_ENDED -> "ENDED"
                    Player.STATE_IDLE -> "IDLE"
                    Player.STATE_READY -> "READY"
                    else -> "UNKNOWN"
                }
                Log.d("StreamScreenViewModel", "Playback state changed: $stateStr")
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                Log.d("StreamScreenViewModel", "Is playing: $isPlaying")
            }
        })
    }


    fun onUrlChange(newUrl: String) {
        Log.d("StreamScreenViewModel", "▶ onUrlChange() called with URL: $newUrl")

        url.value = newUrl

        if (newUrl.isBlank()) {
            Log.w("StreamScreenViewModel", "⚠ URL is blank. Skipping media item setup.")
            return
        }

        try {
            // Build MediaItem with RTSP MIME type
            val mediaItem = MediaItem.Builder()
                .setUri(newUrl.toUri())
                .setMimeType(MimeTypes.APPLICATION_RTSP)
                .build()

            Log.d("StreamScreenViewModel", "MediaItem prepared:")
            Log.d("StreamScreenViewModel", "   • URI       : ${mediaItem.localConfiguration?.uri}")
            Log.d(
                "StreamScreenViewModel",
                "   • MimeType  : ${mediaItem.localConfiguration?.mimeType}"
            )

            // Set and prepare ExoPlayer
            exoPlayer.setMediaItem(mediaItem)
            Log.d("StreamScreenViewModel", "MediaItem set on ExoPlayer.")

            exoPlayer.prepare()
            Log.d("StreamScreenViewModel", "ExoPlayer prepared.")

            exoPlayer.playWhenReady = true
            Log.d("StreamScreenViewModel", "ExoPlayer set to play when ready = true.")

        } catch (e: Exception) {
            Log.e("StreamScreenViewModel", "Exception occurred while setting RTSP stream:", e)
        }
    }


    fun startRecording() {
        if (url.value.isBlank()) {
            Log.d("StreamScreenViewModel", "startRecording: URL is blank; recording not started.")
            return
        }

        Log.d("StreamScreenViewModel", "startRecording: Starting recording for URL: ${url.value}")
        val outputPath = getOutputFilePath()
        Log.d("StreamScreenViewModel", "startRecording: Output path is $outputPath")
        val command = "-i ${url.value} -c copy $outputPath"
        isRecording.value = true
        Log.d("StreamScreenViewModel", "startRecording: Command for FFmpeg: $command")

        ffmpegSession = FFmpegKit.executeAsync(command) {
            Log.d("StreamScreenViewModel", "FFmpegKit execution completed. Recording stopped.")
            isRecording.value = false
        }
    }

    fun stopRecording() {
        Log.d("StreamScreenViewModel", "stopRecording: Stopping recording, if active.")
        ffmpegSession?.let {
            Log.d(
                "StreamScreenViewModel",
                "stopRecording: Cancelling FFmpegKit session with id: ${it.sessionId}"
            )
            FFmpegKit.cancel(it.sessionId)
        }
        isRecording.value = false
    }

    private fun getOutputFilePath(): String {
        val dir = File(context.getExternalFilesDir(null), "recordings")
        if (!dir.exists()) {
            dir.mkdirs()
            Log.d(
                "StreamScreenViewModel",
                "getOutputFilePath: Created recordings directory at ${dir.absolutePath}"
            )
        }
        val file = File(dir, "recorded_${System.currentTimeMillis()}.mp4")
        Log.d(
            "StreamScreenViewModel",
            "getOutputFilePath: Generated file path: ${file.absolutePath}"
        )
        return file.absolutePath
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("StreamScreenViewModel", "onCleared: Releasing ExoPlayer.")
        exoPlayer.release()
    }
}
