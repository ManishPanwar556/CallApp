package com.example.callapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.callapp.model.CallState
import com.example.callapp.model.CallType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CallViewModel : ViewModel() {
    private val _callState = MutableStateFlow<CallState>(CallState.Idle)
    val callState: StateFlow<CallState> = _callState

    private val _micState = MutableStateFlow<Boolean>(false)
    val micState: StateFlow<Boolean> = _micState

    private val _cameraFrontState = MutableStateFlow(false)
    val cameraFrontState: StateFlow<Boolean> = _cameraFrontState

    fun simulateIncomingCall(callType: CallType) {
        _callState.value = CallState.Ringing(callType)
    }

    fun acceptCall(callType: CallType) {
        _callState.value = CallState.InCall(callType)
    }

    fun rejectCall() {
        _callState.value = CallState.Idle
    }

    fun resetCallState() {
        _callState.value = CallState.Idle
    }

    fun toggleMicState() {
        _micState.value = !_micState.value
    }

    fun toggleFronCameraState() {
        _cameraFrontState.value = !_cameraFrontState.value
    }

    fun endCall() {
        viewModelScope.launch(Dispatchers.Main) {
            _callState.value = CallState.Ended
            delay(2000) // Show "Call Ended" screen for 2 seconds
            _callState.value = CallState.Idle
        }
    }
}