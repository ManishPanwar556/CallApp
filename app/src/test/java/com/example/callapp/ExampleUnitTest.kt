package com.example.callapp

import com.example.callapp.model.CallState
import com.example.callapp.model.CallType
import com.example.callapp.viewmodel.CallViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ExampleUnitTest {

    private val viewModel = CallViewModel()
    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before()
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initiate audio call sets state to Ringing`() {
        viewModel.simulateIncomingCall(CallType.AUDIO)
        assertTrue(viewModel.callState.value is CallState.Ringing)
    }

    @Test
    fun `accept incoming call sets state to InCall`() {
        viewModel.acceptCall(CallType.AUDIO)
        assertTrue(viewModel.callState.value is CallState.InCall)
    }

    @Test
    fun `reject incoming call sets state to Idle`() {
        viewModel.rejectCall()
        assertTrue(viewModel.callState.value is CallState.Idle)
    }

    @Test
    fun `end call sets state to Idle`() = runBlocking {
        viewModel.endCall()
        assertTrue(viewModel.callState.value is CallState.Idle)
    }

    @Test
    fun `toggle mic should toggle mic state`() {
        val micState = viewModel.micState.value
        viewModel.toggleMicState()
        assertTrue(viewModel.micState.value == !micState)
    }

    @Test
    fun `toggle front camera should toggle front camera state`() {
        val cameraState = viewModel.cameraFrontState.value
        viewModel.toggleFronCameraState()
        assertTrue(viewModel.cameraFrontState.value == !cameraState)
    }
}
