package com.app.rtspapp.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSession
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StreamScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
) : ViewModel() {

    val exoPlayer = ExoPlayer.Builder(context).build()
    var url = mutableStateOf("")
        private set

    var isRecording = mutableStateOf(false)
        private set

    private var ffmpegSession: FFmpegSession? = null

    // Updates the media stream when URL is changed
    fun onUrlChange(newUrl: String) {
        url.value = newUrl

        if (newUrl.isBlank()) return

        try {
            exoPlayer.clearMediaItems()
            val mediaItem = MediaItem.Builder()
                .setUri(newUrl.toUri())
                .build()

            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true

        } catch (e: Exception) {
            Log.d("StreamScreenViewModel", "Error setting media item: ${e.message}")
        }
    }

    // Starts recording using an asynchronous FFmpeg command
    fun startRecording() {
        if (isRecording.value) {
            Log.d("StreamScreenViewModel", "Recording already in progress")
            return
        }

        val streamUrl = url.value
        if (streamUrl.isBlank()) {
            Log.d("StreamScreenViewModel", "Stream URL is blank, cannot record")
            return
        }

        val outputPath = getOutputFilePath()
        val command = "-i \"$streamUrl\" -c copy \"$outputPath\""

        Log.d("StreamScreenViewModel", "Starting recording with command: $command")

        // Mark the recording as in progress
        isRecording.value = true

        // Execute FFmpeg command asynchronously
        ffmpegSession = FFmpegKit.executeAsync(command) { session ->
            if (com.arthenica.ffmpegkit.ReturnCode.isSuccess(session.returnCode)) {
                Log.d("StreamScreenViewModel", "Recording completed successfully")
            } else if (com.arthenica.ffmpegkit.ReturnCode.isCancel(session.returnCode)) {
                Log.d("StreamScreenViewModel", "Recording was cancelled")
            } else {
                Log.d("StreamScreenViewModel", "Recording failed with error code: ${session.returnCode}")
            }

            // Reset the state once the session finishes
            isRecording.value = false
            ffmpegSession = null
        }
    }

    // Stops the recording by cancelling the FFmpeg session
    fun stopRecording() {
        if (!isRecording.value) {
            Log.d("StreamScreenViewModel", "No recording in progress to stop")
            return
        }

        ffmpegSession?.let { session ->
            Log.d("StreamScreenViewModel", "Stopping recording, cancelling session with id: ${session.sessionId}")
            FFmpegKit.cancel(session.sessionId)
        }

        // Reset recording state
        isRecording.value = false
        ffmpegSession = null
        Log.d("StreamScreenViewModel", "Recording stopped successfully")
    }

    // Returns the output file path for the recording
    private fun getOutputFilePath(): String {
        val dir = File(context.getExternalFilesDir(null), "recordings")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = File(dir, "recorded_${System.currentTimeMillis()}.mp4")
        return file.absolutePath
    }

    override fun onCleared() {
        super.onCleared()
        stopRecording()
        exoPlayer.release()
    }
}
