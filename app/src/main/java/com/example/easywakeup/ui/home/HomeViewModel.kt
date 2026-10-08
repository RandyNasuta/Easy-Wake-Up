package com.example.easywakeup.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easywakeup.data.model.entity.Alarm
import com.example.easywakeup.data.repository.AlarmRepository
import com.example.easywakeup.utils.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val alarms: List<Alarm> = emptyList(),
    val isLoading: Boolean = false,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AlarmRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val alarmScheduler = AlarmScheduler(context)

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadAlarms()
    }

    fun setLoading(isLoading: Boolean) {
        _uiState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun toggleAlarmStatus(alarm: Alarm, newStatus: Boolean) {
        viewModelScope.launch {
            val updatedAlarm = alarm.copy(isActive = newStatus)
            repository.create(updatedAlarm)
        }
    }

    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            alarmScheduler.cancel(alarm)
            repository.delete(alarm.id)
        }
    }

    private fun loadAlarms() {
        viewModelScope.launch {
            delay(1000)
            repository.allAlarms.collect { alarmList ->
                _uiState.value = HomeUiState(
                    alarms = alarmList,
                    isLoading = false,
                )
            }
        }
    }
}