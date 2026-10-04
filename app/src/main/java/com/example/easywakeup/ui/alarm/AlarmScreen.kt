package com.example.easywakeup.ui.alarm

import android.R.attr.contentDescription
import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.PauseCircle
import androidx.compose.material.icons.sharp.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.easywakeup.R
import com.example.easywakeup.ui.theme.Blue20
import com.example.easywakeup.ui.theme.Blue40
import com.example.easywakeup.ui.theme.Blue80
import com.example.easywakeup.ui.theme.EasyWakeUpTheme
import java.util.Calendar

@Composable
fun AlarmScreen(
    modifier: Modifier = Modifier,
    viewModel: AlarmViewModel = hiltViewModel(),
    onSaveSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
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
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Atur Waktu Alarm",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                val timePickerState = rememberTimePickerState(
                    initialHour = uiState.selectedHour,
                    initialMinute = uiState.selectedMinute,
                    is24Hour = true
                )

                LaunchedEffect(timePickerState.hour, timePickerState.minute) {
                    viewModel.updateTime(timePickerState.hour, timePickerState.minute)
                }

                Box(
                    modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center
                ) {
                    TimePicker(
                        state = timePickerState, colors = TimePickerDefaults.colors()
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Cara Mematikan Alarm",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, bottom = 12.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                    ) {
                        ChallengeSwitchItem(
                            title = "Foto Barang", checked = uiState.isPhotoChallengeEnabled, onCheckedChange = {viewModel.setPhotoChallenge(it)})

                        ChallengeSwitchItem(
                            title = "Berhitung", checked = uiState.isMathChallengeEnabled, onCheckedChange = {viewModel.setMathChallenge(it)})

                        ChallengeSwitchItem(
                            title = "Menulis Kalimat", checked = uiState.isWritingChallengeEnabled, onCheckedChange = {viewModel.setWritingChallenge(it)})
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Pilih Suara Alarm",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, bottom = 12.dp)
                )

                AlarmSoundDropDown(
                    selectedSound = uiState.selectedSound, onSoundSelected = { sound ->
                        viewModel.setSound(sound)
                    }, modifier = Modifier
                )

                Spacer(modifier = Modifier.height(36.dp))

                Button(
                    onClick = {
                        viewModel.saveAlarm {
                            onSaveSuccess()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFB703)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "Simpan Alarm",
                        color = Color(0xFF0B132B),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun AlarmSoundDropDown(
    selectedSound: String, onSoundSelected: (String) -> Unit, modifier: Modifier = Modifier
) {

    val soundList = listOf("Sound 1", "Sound 2", "Sound 3")
    var expanded by remember { mutableStateOf(false) }
    var soundId by remember { mutableStateOf(R.raw.sound_1) }
    val mContext = LocalContext.current
    var mpPlayer by remember { mutableStateOf(MediaPlayer.create(mContext, soundId)) }
    var isPlaying by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ){
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = selectedSound, onValueChange = {}, readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.05f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                    focusedBorderColor = Color(0xFFFFB703),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedTrailingIconColor = Color(0xFFFFB703),
                    unfocusedTrailingIconColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        enabled = true
                    )
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color(0xFF1C2541))
            ) {
                soundList.forEach { sound ->
                    DropdownMenuItem(
                        text = { Text(
                            text = sound,
                            color = Color.White
                        ) },
                        onClick = {
                            onSoundSelected(sound)
                            expanded = false

                            mpPlayer.stop()
                            mpPlayer.release()
                            isPlaying = false

                            soundId = when (sound) {
                                "Sound 1" -> R.raw.sound_1
                                "Sound 2" -> R.raw.sound_2
                                "Sound 3" -> R.raw.sound_3
                                else -> R.raw.sound_1
                            }

                            mpPlayer = MediaPlayer.create(mContext, soundId)
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        IconButton(
            modifier = modifier.size(48.dp),
            onClick = {
                if (!isPlaying) {
                    mpPlayer.start()
                    isPlaying = true
                } else {
                    mpPlayer.pause()
                    isPlaying = false
                }
            }
        ) {
            Icon(
                imageVector = if (!isPlaying) Icons.Sharp.PlayCircle else Icons.Sharp.PauseCircle,
                contentDescription = "Putar Suara",
                tint = Color(0xFFFFB703),
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
private fun ChallengeSwitchItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold
        )

        Switch(
            checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF0B132B), checkedTrackColor = Color(0xFFFFB703)
            )
        )
    }
}

@Preview
@Composable
private fun AlarmScreenPreview() {
    EasyWakeUpTheme {
        AlarmScreen()
    }
}