package com.app.rtspapp.ui

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

@Composable
fun RSTPPlayerView(
    modifier: Modifier = Modifier,
    viewModel: StreamScreenViewModel = hiltViewModel(),
    onToggleFullScreen: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .height(300.dp)
            .padding(8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize(),
            factory = { context ->
                PlayerView(context).apply {
                    player = viewModel.exoPlayer
                    useController = true
                }
            }
        )

        IconButton(
            onClick = onToggleFullScreen,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Fullscreen,
                contentDescription = "Full Screen",
                tint = Color.White
            )
        }
    }
}
