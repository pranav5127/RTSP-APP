package com.app.rtspapp.ui

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.ui.PlayerView
import com.app.rtspapp.viewmodel.StreamScreenViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat

@Composable
fun RSTPPlayerView(
    modifier: Modifier = Modifier,
    viewModel: StreamScreenViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val activity = context as Activity
    Box(
        modifier = modifier
            .height(if (viewModel.isFullScreen.value) 400.dp else 300.dp)
            .padding(8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                PlayerView(context).apply {
                    useController = true
                    player = viewModel.exoPlayer
                    setFullscreenButtonClickListener {
                        if (viewModel.isFullScreen.value) {
                            exitFullscreen(activity)
                            viewModel.onToggleFullScreen(false)
                        } else {
                            enterFullscreen(activity)
                            viewModel.onToggleFullScreen(true)

                        }
                    }
                }
            }
        )

    }
}

fun enterFullscreen(activity: Activity) {

    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

        WindowCompat.setDecorFitsSystemWindows(activity.window, false)
        activity.window.insetsController?.apply {
            hide(android.view.WindowInsets.Type.statusBars() or android.view.WindowInsets.Type.navigationBars())
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    } else {
        @Suppress("DEPRECATION")
        activity.window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                )
    }
}

fun exitFullscreen(activity: Activity) {

    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

        WindowCompat.setDecorFitsSystemWindows(activity.window, true)
        activity.window.insetsController?.apply {
            show(android.view.WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        }
    } else {
        @Suppress("DEPRECATION")
        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }
}
