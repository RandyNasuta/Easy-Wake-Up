package com.example.easywakeup.ui.alarm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
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
    val isWritingChallengeEnabled: Boolean = false,
    val alarmId: Long = -1L
)

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val repository: AlarmRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlarmUiState())
    val uiState: StateFlow<AlarmUiState> = _uiState.asStateFlow()

    init {
        val alarmId = savedStateHandle.get<Long>("alarmId") ?: -1L
        setAlarmId(alarmId)

        if (alarmId != -1L) {
            getAlarmById(_uiState.value.alarmId)
        }
    }

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

    fun setAlarmId(id: Long) {
        _uiState.update { it.copy(alarmId = id) }
    }

    fun getAlarmById(id: Long) {
        viewModelScope.launch {
            repository.getAlarmById(id).collect { alarm ->
                if (alarm != null) {
                    _uiState.update {
                        Log.i("AlarmViewModel", "getAlarmById: $alarm")
                        it.copy(
                            selectedHour = alarm.time.split(":")[0].toInt(),
                            selectedMinute = alarm.time.split(":")[1].toInt(),
                            selectedSound = alarm.sound,
                            isActive = alarm.isActive,
                            isPhotoChallengeEnabled = alarm.methodList.contains("Foto Barang"),
                            isMathChallengeEnabled = alarm.methodList.contains("Matematika"),
                            isWritingChallengeEnabled = alarm.methodList.contains("Tulis"),
                            alarmId = alarm.id
                        )
                    }
                }
            }
        }
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