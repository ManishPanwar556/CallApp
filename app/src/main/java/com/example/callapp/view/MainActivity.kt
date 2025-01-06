package com.example.callapp.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CameraFront
import androidx.compose.material.icons.filled.CameraRear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.callapp.R
import com.example.callapp.model.CallState
import com.example.callapp.model.CallType
import com.example.callapp.view.theme.CallAppTheme
import com.example.callapp.viewmodel.CallViewModel

class MainActivity : ComponentActivity() {
    private val callViewModel by viewModels<CallViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CallAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CallConsole(
                        callViewModel,
                        innerPadding
                    )
                }
            }
        }
    }
}

@Composable
fun CallConsole(callViewModel: CallViewModel, paddingValues: PaddingValues) {
    val callState =
        callViewModel.callState.collectAsState().value
    val mutedState = callViewModel.micState.collectAsState().value
    val cameraFrontState = callViewModel.cameraFrontState.collectAsState().value
    Box(
        modifier = Modifier
            .padding(paddingValues)
            .background(Color.Black)
    ) {
        when (callState) {
            is CallState.Idle -> IdleView(onAudioCall = {
                callViewModel.simulateIncomingCall(
                    CallType.AUDIO
                )
            },
                onVideoCall = { callViewModel.simulateIncomingCall(CallType.VIDEO) })

            is CallState.Ringing -> RingingView(
                onAccept = {
                    callViewModel.acceptCall(callState.callType)
                },
                onReject = { callViewModel.rejectCall() }, callType = callState.callType
            )

            is CallState.InCall -> InCallView(
                onEndCall = { callViewModel.endCall() },
                isVideo = callState.callType == CallType.VIDEO,
                ontoggleMute = { callViewModel.toggleMicState() },
                onSwitchCamera = { callViewModel.toggleFronCameraState() },
                cameraFrontState = cameraFrontState,
                isMuted = mutedState
            )

            is CallState.Ended -> CallEndedView(onReset = { callViewModel.resetCallState() })
        }
    }


}

@Composable
fun IdleView(onAudioCall: () -> Unit, onVideoCall: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Button(onClick = onAudioCall, colors = ButtonDefaults.buttonColors(Color.White)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Mic, contentDescription = "audio call")
                Spacer(modifier = Modifier.width(2.dp))
                Text(color = Color.Black, text = stringResource(R.string.inititate_audio))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onVideoCall, colors = ButtonDefaults.buttonColors(Color.White)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "video call")
                Spacer(modifier = Modifier.width(2.dp))
                Text(color = Color.Black, text = stringResource(R.string.inititate_video))
            }
        }
    }
}

@Composable
fun RingingView(onAccept: () -> Unit, onReject: () -> Unit, callType: CallType) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            if (callType == CallType.AUDIO) stringResource(R.string.incoming_audio) else stringResource(
                R.string.incoming_video
            ), style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = onAccept, colors = ButtonDefaults.buttonColors(Color.Green)) {
                Text(stringResource(R.string.accept))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = onReject, colors = ButtonDefaults.buttonColors(Color.Red)) {
                Text(stringResource(R.string.reject))
            }
        }
    }
}

@Composable
fun InCallView(
    onEndCall: () -> Unit,
    isVideo: Boolean,
    isMuted: Boolean,
    cameraFrontState: Boolean,
    ontoggleMute: () -> Unit,
    onSwitchCamera: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CallFeed(isVideo = isVideo)
        InCallControls(
            isVideo = isVideo,
            onEndCall = onEndCall,
            isMuted = isMuted,
            ontoggleMute = ontoggleMute,
            onSwitchCamera = onSwitchCamera,
            cameraFrontState = cameraFrontState
        )
    }
}

@Composable
fun CallFeed(isVideo: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = if (
                    isVideo) Icons.Filled.Videocam else Icons.Filled.AccountCircle,
                contentDescription = "Profile",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                tint = Color.Gray
            )
            Text(
                if (isVideo) stringResource(R.string.video_call_in_progress) else stringResource(R.string.audio_call_in_progress),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 100.dp)
            )
        }

    }
}


@Composable
fun InCallControls(
    isVideo: Boolean,
    onEndCall: () -> Unit,
    ontoggleMute: () -> Unit,
    onSwitchCamera: () -> Unit,
    isMuted: Boolean,
    cameraFrontState: Boolean
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        if (isVideo) {
            IconButton(onClick = { onSwitchCamera() }) {
                Icon(
                    imageVector = if (cameraFrontState) Icons.Default.CameraFront else Icons.Default.CameraRear, // Use appropriate icon
                    contentDescription = "Toggle Video",
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        IconButton(onClick = ontoggleMute) {
            Icon(
                imageVector = if (isMuted) Icons.Filled.MicOff else Icons.Filled.Mic,
                contentDescription = "Mute",
                modifier = Modifier.size(48.dp)
            )
        }
        IconButton(onClick = onEndCall) {
            Icon(
                imageVector = Icons.Filled.CallEnd,
                contentDescription = "End Call",
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.Red, CircleShape)
                    .padding(8.dp)
            )
        }
    }
}


@Composable
fun CallEndedView(onReset: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(stringResource(R.string.call_ended), style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onReset) { Text(stringResource(R.string.reset)) }
    }
}




