package com.app.rtspapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.ui.PlayerView
import com.app.rtspapp.viewmodel.StreamScreenViewModel

@Composable
fun RSTPPlayerView(
    modifier: Modifier = Modifier,
    viewModel: StreamScreenViewModel = hiltViewModel(),
) {
    AndroidView(
        modifier = modifier
            .height(300.dp)
            .padding(8.dp)
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(16.dp)),
        factory = { context ->
            PlayerView(context).apply {
                player = viewModel.exoPlayer
                useController = true
            }
        }
    )
}
