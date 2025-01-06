package com.example.callapp.model

sealed class CallState {
    data object Idle : CallState()
    class Ringing(val callType: CallType) : CallState()
    class InCall(val callType: CallType) : CallState()
    data object Ended : CallState()

}

enum class CallType {
    AUDIO,
    VIDEO
}