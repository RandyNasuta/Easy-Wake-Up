package com.example.easywakeup.ui.ring

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.easywakeup.R
import com.example.easywakeup.ui.theme.Blue20
import com.example.easywakeup.ui.theme.Blue40
import com.example.easywakeup.ui.theme.Blue80
import com.example.easywakeup.ui.theme.EasyWakeUpTheme
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AlarmRingActivity : ComponentActivity() {

    private val TAG = "AlarmRingActivity"
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    @RequiresApi(Build.VERSION_CODES.O_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        val soundName = intent.getStringExtra("EXTRA_ALARM_SOUND") ?: "Sound 1"
        val methods = intent.getStringExtra("EXTRA_ALARM_METHODS") ?: ""

        //Putar alarm
        startAlarmAudio(soundName)
        startVibration()

        setContent {
            EasyWakeUpTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    AlarmRingScreen(
                        methods = methods, onDismissClicked = {
                            stopAlarmAndFinish()
                        })
                }
            }
        }
    }

    private fun startAlarmAudio(soundName: String) {
        val soundResId = when (soundName) {
            "Sound 1" -> R.raw.sound_1
            "Sound 2" -> R.raw.sound_2
            "Sound 3" -> R.raw.sound_3
            else -> R.raw.sound_1
        }

        try {
            mediaPlayer = MediaPlayer.create(this, soundResId).apply {
                isLooping = true
                start()
            }
        } catch (e: Exception) {
            Log.e(TAG, "startAlarmAudio: error ${e.message}")
        }
    }

    private fun startVibration() {
        vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION") getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        val pattern = longArrayOf(0, 1000, 1000) // Getar 1 detik, jeda 1 detik
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            @Suppress("DEPRECATION") vibrator?.vibrate(pattern, 0)
        }
    }

    private fun stopAlarmAndFinish() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        vibrator?.cancel()

        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        vibrator?.cancel()
    }
}

@Composable
fun AlarmRingScreen(
    methods: String = "", onDismissClicked: () -> Unit = {}
) {
    val targetSentence = remember {
        listOf(
            "Tak ada rotan, akar pun jadi",
            "Bersakit-sakit dahulu, bersenang-senang kemudian",
            "Sedikit demi sedikit, lama-lama menjadi bukit"
        ).random()
    }

    val currentTime = remember {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    val num1 = remember { (1..100).random() }
    val num2 = remember { (1..100).random() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Blue20, Blue40, Blue80)
                )
            )
            .padding(24.dp), contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Wake Up!",
                color = Color(0xFFFFB703),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "$currentTime",
                color = Color.White,
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            AlarmCalculating(
                number1 = num1,
                number2 = num2,
                onChallengeCompleted = onDismissClicked
            )
//            if (methods.contains("Menulis Kalimat")) {
//                AlarmWriting(
//                    targetSentence = targetSentence,
//                    onChallengeCompleted = onDismissClicked
//                )
//            } else if (methods.contains("Berhitung")) {
//                AlarmCalculating(
//                    number1 = num1,
//                    number2 = num2,
//                    onChallengeCompleted = onDismissClicked
//                )
//            } else if (methods.contains("Foto Barang")) {
//
//            } else {
//                Button(
//                    onClick = onDismissClicked,
//                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB703)),
//                    modifier = Modifier.fillMaxWidth().height(50.dp),
//                    shape = RoundedCornerShape(12.dp)
//                ) {
//                    Text(
//                        text = "Matikan Alarm",
//                        color = Color(0xFF0B132B),
//                        fontSize = 16.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//                }
//            }
        }
    }
}

@Composable
private fun AlarmWriting(
    targetSentence: String,
    onChallengeCompleted: () -> Unit = {},
) {

    var userInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Ketik ulang kalimat di bawah untuk mematikan alarm:",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.15f)
            ),
        ) {
            Text(
                text = "\"$targetSentence\"",
                color = Color(0xFFFFB703),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = userInput,
            onValueChange = {
                userInput = it
                if (isError) isError = false
            },
            label = {
                Text("Ketik di sini...", color = Color.White.copy(alpha = 0.7f))
            },
            isError = isError,
            singleLine = false,
            maxLines = 3,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFFFFB703),
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                errorBorderColor = Color.Red,
                focusedLabelColor = Color(0xFFFFB703)
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        if (isError) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Kalimat belum cocok, coba periksa kembali!",
                color = Color.Red,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (userInput.trim() == targetSentence.trim()) {
                    onChallengeCompleted()
                } else {
                    isError = true
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB703)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Kirim & Matikan Alarm",
                color = Color(0xFF0B132B),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AlarmCalculating(
    modifier: Modifier = Modifier,
    number1: Int,
    number2: Int,
    onChallengeCompleted: () -> Unit
) {
    var userInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    val operatorChoosen = remember { listOf("+", "-", "*", "/").random() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Hitung perhitungan di bawah untuk mematikan alarm (Jika desimal, tulis 2 angka dibelakang koma):",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.15f)
            ),
        ) {
            Text(
                text = "$number1 ${operatorChoosen} $number2",
                color = Color(0xFFFFB703),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = userInput,
            onValueChange = {
                userInput = it
                if (isError) isError = false
            },
            label = {
                Text("Ketik di sini...", color = Color.White.copy(alpha = 0.7f))
            },
            isError = isError,
            singleLine = false,
            maxLines = 3,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFFFFB703),
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                errorBorderColor = Color.Red,
                focusedLabelColor = Color(0xFFFFB703)
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        if (isError) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Hasil perhitungan belum benar, silahkan coba lagi!",
                color = Color.Red,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                val formatDecimal = DecimalFormat("#.##")

                val result = when (operatorChoosen) {
                    "+" -> (number1 + number2).toDouble()
                    "-" -> (number1 - number2).toDouble()
                    "*" -> (number1 * number2).toDouble()
                    else -> {
                        val division = number1.toDouble() / number2.toDouble()
                        formatDecimal.format(division).toDouble()
                    }
                }

                val userAnswer = userInput.trim().toDoubleOrNull()

                if (userAnswer != null && userAnswer == result) {
                    onChallengeCompleted()
                } else {
                    isError = true
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB703)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Kirim & Matikan Alarm",
                color = Color(0xFF0B132B),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview
@Composable
private fun AlarmRingScreenPreview() {
    EasyWakeUpTheme {
        AlarmRingScreen()
    }
}