package com.app.rtspapp

import android.annotation.SuppressLint
import android.app.PictureInPictureParams
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.app.rtspapp.ui.StreamScreen
import com.app.rtspapp.ui.theme.RTSPAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var isInPipModeState = mutableStateOf(false)
    private var isFullScreenState = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RTSPAppTheme(dynamicColor = false) {
                StreamScreen(
                    isInPipMode = isInPipModeState.value,
                    isFullScreen = isFullScreenState.value,
                    onEnterPipMode = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val aspectRatio = Rational(16, 9)
                            val pipBuilder = PictureInPictureParams.Builder()
                                .setAspectRatio(aspectRatio)
                                .build()
                            enterPictureInPictureMode(pipBuilder)
                        }
                    },

                )
            }
        }
    }


    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        isInPipModeState.value = isInPictureInPictureMode
    }
}
