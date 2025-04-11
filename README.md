# RTSP App Documentation

## Overview

The RTSP App is an Android application that streams video using RTSP URLs and provides additional functionalities such as recording the stream and Picture-in-Picture (PiP) mode. The app leverages modern libraries including Media3’s ExoPlayer for streaming, FFmpegKit for recording, and Dagger Hilt for dependency injection.

## Architecture

The project is structured into three main layers:
- **UI Components**: Built with Jetpack Compose, including custom components for displaying the video, URL input, and control buttons.
- **ViewModel**: Manages the state and logic including URL handling, recording control, and toggling between fullscreen and PiP modes.
- **Application Entry**: The `MainActivity` sets up the UI and handles system events like configuration changes (e.g., entering Picture-in-Picture mode).

## Components

### 1. UI Package

#### a. `RSTPPlayerView`
- **Purpose**: Displays the streaming video.
- **Key Features**:
  - Uses an `AndroidView` to integrate `PlayerView` from Media3.
  - Supports toggling fullscreen mode via a button.
  - Adjusts player dimensions based on the fullscreen state.
- **Fullscreen Methods**:
  - **`enterFullscreen(activity: Activity)`**:  
    Sets the device orientation to landscape, hides system UI elements (status and navigation bars), and enables immersive mode.
  - **`exitFullscreen(activity: Activity)`**:  
    Resets the orientation and shows system UI elements.

#### b. `StreamScreen`
- **Purpose**: The main interface for the streaming functionality.
- **Key Features**:
  - Displays a top app bar (omitted in fullscreen or PiP mode).
  - Contains URL input, video player view, recording control buttons, and a PiP mode trigger.
- **Sub-Components**:
  - **`StreamTopBar`**: Shows the app title.
  - **`StreamContent`**: Organizes the URL input field, player view, and control buttons.
  - **`UrlInputField`**: A text field for entering the RTSP stream URL.

### 2. ViewModel Package

#### `StreamScreenViewModel`
- **Purpose**: Manages the data and logic for the stream.
- **Key Responsibilities**:
  - **URL Handling**:  
    Updates the video player with a new `MediaItem` when the URL changes.
  - **Recording**:  
    Uses FFmpegKit to start and stop recording the stream.
      - Constructs a command to record the stream to a file with a unique timestamp.
      - Manages UI state updates to reflect recording status.
  - **Fullscreen Toggling**:  
    Maintains a state variable that indicates whether the app is in fullscreen mode.
- **Lifecycle Management**:
  - On ViewModel clearance, any in-progress recording is stopped and the ExoPlayer is released.

### 3. Application Entry Point

#### `MainActivity`
- **Purpose**: Hosts the app’s UI and manages configuration changes.
- **Key Features**:
  - Sets up a full-edge-to-edge display.
  - Uses Jetpack Compose with `RTSPAppTheme` to set the UI.
  - Handles the transition into Picture-in-Picture mode:
      - Configures PiP parameters using an aspect ratio (16:9).
      - Adjusts UI state based on whether PiP mode is active.
- **Other Details**:
  - Annotated with `@AndroidEntryPoint` to enable dependency injection with Hilt.

#### `RTSPApp` (Application Class)
- **Purpose**: Initializes Dagger Hilt for dependency management throughout the app.

## Dependencies

- **Media3 ExoPlayer**: For playing RTSP streams.
- **FFmpegKit**: For recording the streaming content.
- **Jetpack Compose**: For building modern Android UI.
- **Dagger Hilt**: For dependency injection.
- **AndroidX Libraries**: For activity and window management, including support for fullscreen and PiP modes.

## Setup & Usage

1. **Import the Project**:  
   Clone or import the project into Android Studio.
2. **Configure Dependencies**:  
   Ensure that all required dependencies (Media3, FFmpegKit, Hilt, etc.) are declared in the Gradle files.
3. **Run the App**:  
   Deploy the app on an Android device or emulator.
4. **Streaming**:  
   Enter a valid RTSP URL in the URL input field; the stream will begin playback automatically.
5. **Recording**:  
   Use the recording toggle button to start or stop recording the stream.
6. **Fullscreen & PiP Mode**:
    - Tap the fullscreen button on the video player to switch to and from fullscreen mode.
    - Use the PiP button to enter Picture-in-Picture mode (requires Android O and above).

## Code Flow Summary

- **User Interaction**:  
  Users enter the stream URL and interact with playback controls.
- **ViewModel Interaction**:  
  The ViewModel processes the URL changes, updates the player, and manages recording commands.
- **UI Rendering**:  
  The Compose UI reflects updated states such as playing stream, recording status, and fullscreen/PiP transitions.
- **System Integration**:  
  The app correctly handles device orientation, system UI behavior, and PiP transitions.

## Conclusion

The RTSP App offers a robust solution for streaming and recording RTSP content by leveraging modern Android components and libraries. Its design ensures a clear separation of UI, business logic, and system interaction, making the app both maintainable and scalable for future development.

*For more details or to contribute, please review the source code in the project repository.*
