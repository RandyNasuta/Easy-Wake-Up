package com.example.easywakeup.ui.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easywakeup.data.model.entity.Alarm
import com.example.easywakeup.data.repository.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class AlarmUiState(
    val selectedHour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
    val selectedMinute: Int = Calendar.getInstance().get(Calendar.MINUTE),
    val selectedSound: String = "Sound 1",
    val isActive: Boolean = true,
    val isPhotoChallengeEnabled: Boolean = false,
    val isMathChallengeEnabled: Boolean = false,
    val isWritingChallengeEnabled: Boolean = false
)

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val repository: AlarmRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlarmUiState())
    val uiState: StateFlow<AlarmUiState> = _uiState.asStateFlow()

    fun updateTime(hour: Int, minute: Int) {
        _uiState.update {
            it.copy(selectedHour = hour, selectedMinute = minute)
        }
    }

    fun setPhotoChallenge(enabled: Boolean) {
        _uiState.update { it.copy(isPhotoChallengeEnabled = enabled) }
    }

    fun setMathChallenge(enabled: Boolean) {
        _uiState.update { it.copy(isMathChallengeEnabled = enabled) }
    }

    fun setWritingChallenge(enabled: Boolean) {
        _uiState.update { it.copy(isWritingChallengeEnabled = enabled) }
    }

    fun setSound(sound: String) {
        _uiState.update { it.copy(selectedSound = sound) }
    }

    fun setActive(enabled: Boolean) {
        _uiState.update { it.copy(isActive = enabled) }
    }

    fun saveAlarm(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            val formattedTime = String.format("%02d:%02d", state.selectedHour, state.selectedMinute)

            val activeChallenges = mutableListOf<String>()
            if (state.isPhotoChallengeEnabled) activeChallenges.add("Foto Barang")
            if (state.isMathChallengeEnabled) activeChallenges.add("Matematika")
            if (state.isWritingChallengeEnabled) activeChallenges.add("Tulis")

            val newAlarm = Alarm(
                time = formattedTime,
                sound = state.selectedSound,
                isActive = state.isActive,
                methodList = activeChallenges.joinToString(", ")
            )

            repository.create(newAlarm)
            onSuccess()
        }
    }
}