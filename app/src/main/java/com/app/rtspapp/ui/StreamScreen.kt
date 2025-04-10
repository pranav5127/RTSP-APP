package com.app.rtspapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.rtspapp.R
import com.app.rtspapp.viewmodel.StreamScreenViewModel

@Composable
fun StreamScreen(
    viewModel: StreamScreenViewModel = hiltViewModel(),
    isInPipMode: Boolean = false,
    onEnterPipMode: () -> Unit = {},
) {
    Scaffold { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            StreamTopBar()
            StreamContent(
                url = viewModel.url.value,
                onUrlChange = { viewModel.onUrlChange(it) },
                isRecording = viewModel.isRecording.value,
                onToggleRecording = {
                    if (viewModel.isRecording.value) viewModel.stopRecording()
                    else viewModel.startRecording()
                },
                onEnterPIP = onEnterPipMode
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreamTopBar(
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = stringResource(id = R.string.app_name))
        },
        modifier = modifier
    )
}

@Composable
fun StreamContent(
    modifier: Modifier = Modifier,
    url: String,
    onUrlChange: (String) -> Unit,
    isRecording: Boolean,
    onToggleRecording: () -> Unit,
    onEnterPIP: () -> Unit,
) {
    Column(modifier = modifier) {
        UrlInputField(
            url = url,
            onUrlChange = onUrlChange,
        )
        Spacer(modifier = Modifier.height(16.dp))

        RSTPPlayerView()

        Spacer(modifier = Modifier.height(8.dp))

        RecordingButton(
            isRecording = isRecording,
            onClick = onToggleRecording
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onEnterPIP,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Text(text = stringResource(R.string.enter_pip_mode))
        }
    }
}

@Composable
fun UrlInputField(
    url: String,
    onUrlChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = url,
        onValueChange = onUrlChange,
        placeholder = {
            Text(text = stringResource(R.string.enter_url))
        },
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(50.dp)
    )
}

@Composable
fun RecordingButton(
    isRecording: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = if (isRecording) {
                stringResource(R.string.stop_recording)
            } else {
                stringResource(R.string.start_recording)
            }
        )
    }
}