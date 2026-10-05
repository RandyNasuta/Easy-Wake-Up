package com.example.easywakeup.ui.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.easywakeup.ui.theme.Blue20
import com.example.easywakeup.ui.theme.Blue40
import com.example.easywakeup.ui.theme.Blue80
import com.example.easywakeup.ui.theme.EasyWakeUpTheme


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onclick: () -> Unit = {},
    onItemClicked: (Long) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onclick,
                containerColor = Color(0xFFFFB703),
                contentColor = Color(0xFF0B132B)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Tambah Alarm"
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 12.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Blue20,
                            Blue40,
                            Blue80,
                        )
                    )
                )
        ) {

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFFFFB703)
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 16.dp)
                        .fillMaxSize(),
                ) {
                    LazyColumn(
                        modifier = modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        items(
                            uiState.alarms,
                            key = { it.id },
                        ) { alarm ->
                            AlarmCard(
                                modifier = modifier,
                                time = alarm.time,
                                isActive = alarm.isActive,
                                onCheckedChange = { isChecked ->
                                    viewModel.toggleAlarmStatus(alarm, isChecked)
                                },
                                onClick = {
                                    Log.i("HomeScreen", "HomeScreen: alarm id ${alarm.id}")
                                    onItemClicked(alarm.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AlarmCard(
    modifier: Modifier = Modifier,
    time: String,
    isActive: Boolean,
    onCheckedChange: (Boolean) -> Unit = {},
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        onClick = onClick
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Text(
                text = time, color = Color.White, fontSize = 32.sp,
            )

            Switch(
                checked = isActive, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF0B132B), checkedTrackColor = Color(0xFFFFB703)
                )
            )
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    EasyWakeUpTheme {
        HomeScreen()
    }
}