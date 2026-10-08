package com.example.easywakeup.ui.ring

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.AudioAttributes
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
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.easywakeup.R
import com.example.easywakeup.ui.theme.Blue20
import com.example.easywakeup.ui.theme.Blue40
import com.example.easywakeup.ui.theme.Blue80
import com.example.easywakeup.ui.theme.EasyWakeUpTheme
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.apply
import kotlin.math.floor

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
            mediaPlayer = MediaPlayer().apply {
                val afd = resources.openRawResourceFd(soundResId)
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()

                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
                setAudioAttributes(audioAttributes)

                isLooping = true

                setOnCompletionListener { mp -> mp.start() }

                prepare()
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
    methods: String = "",
    onDismissClicked: () -> Unit = {}
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

    val challengeQueue = remember(methods) {
        methods.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }

    var currentStep by remember { mutableStateOf(0) }

    val onChallengeSuccess: () -> Unit = {
        if (currentStep < challengeQueue.size - 1){
            currentStep++
        } else {
            onDismissClicked()
        }
    }

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

            Log.i("AlarmRingScreen", "Challenges: ${challengeQueue.toString()}")
            if (challengeQueue.isEmpty()) {
                Button(
                    onClick = onDismissClicked,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB703)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Matikan Alarm",
                        color = Color(0xFF0B132B),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                when (challengeQueue[currentStep]) {
                    "Tulis" -> {
                        AlarmWritingChallenge(
                            targetSentence = targetSentence,
                            onChallengeCompleted = onChallengeSuccess
                        )
                    }
                    "Matematika" -> {
                        AlarmCalculatingChallenge(
                            number1 = num1,
                            number2 = num2,
                            onChallengeCompleted = onChallengeSuccess
                        )
                    }
                    "Foto Barang" -> {
                        val objectList = remember {
                            listOf(
                                Pair("Sepatu", "Shoe"),
                                Pair("Kursi", "Chair"),
                                Pair("Botol Minum", "Bottle"),
                                Pair("Tas", "Bag"),
                                Pair("Buku", "Book"),
                                Pair("Gelas / Cangkir", "Cup"),
                                Pair("Pakaian / Baju", "Clothing")
                            ).random()
                        }

                        var errorMessage by remember { mutableStateOf("") }
                        var isAnalyzing by remember { mutableStateOf(false) }

                        Column(modifier = Modifier.fillMaxSize()) {
                            if (errorMessage.isNotEmpty()) {
                                Text(
                                    text = errorMessage,
                                    color = Color.Red,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }

                            if (isAnalyzing) {
                                Text("Proses analisis...", color = Color.White)
                            } else {
                                AlarmPhotoChallenge(
                                    targetObject = objectList.first,
                                    onPhotoCaptured = { bitmap ->
                                        isAnalyzing = true

                                        analyzePhoto(
                                            bitmap = bitmap,
                                            targetObject = objectList.second,
                                            onSuccess = {
                                                isAnalyzing = false
                                                onChallengeSuccess() // Benar! Matikan alarm
                                            },
                                            onFail = { errorMsg ->
                                                Log.e("AlarmRingActivity", "Gagal foto: $errorMsg")
                                                isAnalyzing = false
                                                errorMessage = "Salah! Coba foto dari sudut lain."
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
private fun AlarmWritingChallenge(
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
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFFFFB703),
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                errorBorderColor = Color.Red,
                focusedLabelColor = Color(0xFFFFB703)
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
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
fun AlarmCalculatingChallenge(
    modifier: Modifier = Modifier,
    number1: Int,
    number2: Int,
    onChallengeCompleted: () -> Unit
) {
    var userInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    val operatorChoosen = remember { listOf("+", "-", "*", "/").random() }

    val displayNum1 = remember { number1 }
    val displayNum2 = remember { number2 }

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
                text = "$displayNum1 ${operatorChoosen} $displayNum2",
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
                val result = when (operatorChoosen) {
                    "+" -> (displayNum1 + displayNum2).toDouble()
                    "-" -> (displayNum1 - displayNum2).toDouble()
                    "*" -> (displayNum1 * displayNum2).toDouble()
                    else -> {
                        val division = displayNum1.toDouble() / displayNum2.toDouble()
                        floor(division * 100) / 100.0
                    }
                }

                val cleanUserInput = userInput.trim()
                    .replace(",", ".")
                    .replace("–", "-")
                    .replace("—", "-")

                val userAnswer = cleanUserInput.toDoubleOrNull()

                if (userAnswer != null && kotlin.math.abs(userAnswer - result) < 0.01) {
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
fun AlarmPhotoChallenge(
    modifier: Modifier = Modifier,
    targetObject: String = "Kursi",
    onPhotoCaptured: (Bitmap) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val hasCameraPermission = ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.CAMERA
    ) == android.content.pm.PackageManager.PERMISSION_GRANTED

    if (!hasCameraPermission) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black), contentAlignment = Alignment.Center) {
            Text(
                text = "Izin Kamera belum diberikan!\nBuka kunci HP dan berikan izin pada aplikasi.",
                color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
        return
    }

    var cameraController = remember {
        LifecycleCameraController(context).apply {
            bindToLifecycle(lifecycleOwner)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                PreviewView(context).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    controller = cameraController
                }
            }
        )

        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 40.dp, start = 20.dp, end = 20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Text(
                text = "Foto objek $targetObject",
                color = Color(0xFFFFB703),
                fontSize = 18.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        Button(
            onClick = {
                val mainExecutor = ContextCompat.getMainExecutor(context)
                cameraController.takePicture(
                    mainExecutor,
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(imageProxy: ImageProxy) {
                            super.onCaptureSuccess(imageProxy)
                            val bitmap = imageProxyToBitmap(imageProxy)
                            imageProxy.close()
                            onPhotoCaptured(bitmap)
                        }

                        override fun onError(exception: ImageCaptureException) {
                            super.onError(exception)
                            Log.e("AlarmRingActivity", "Gagal mengambil foto: ${exception.message}")
                        }
                    }
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .size(80.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB703))
        ) {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = "Ambil Foto",
                tint = Color(0xFF0B132B)
            )
        }
    }
}

private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap {
    val planeProxy = imageProxy.planes[0]
    val buffer = planeProxy.buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)

    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

    val matrix = Matrix()
    matrix.postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())

    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

private fun analyzePhoto(
    bitmap: Bitmap,
    targetObject: String,
    onSuccess: () -> Unit,
    onFail: (String) -> Unit
) {
    val TAG = "AlarmRingActivity"
    val image = InputImage.fromBitmap(bitmap, 0)

    val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

    val validKeywords = when (targetObject.lowercase()) {
        "shoe" -> listOf("shoe", "footwear", "sneaker", "boot", "sandal", "foot")
        "chair" -> listOf("chair", "furniture", "stool", "seat", "wood", "armrest")
        "bottle" -> listOf("bottle", "drink", "water bottle", "plastic bottle", "container")
        "bag" -> listOf("bag", "backpack", "handbag", "luggage & bags")
        "book" -> listOf("book", "textbook", "paper", "notebook")
        "cup" -> listOf("cup", "mug", "drinkware", "coffee cup")
        "clothing" -> listOf("clothing", "apparel", "shirt", "t-shirt", "jacket", "sweater", "textile")
        else -> listOf(targetObject)
    }

    labeler.process(image)
        .addOnSuccessListener { labels ->
            val detectedItems = labels.joinToString {
                it.text
            }
            Log.d(TAG, "analyzePhoto: detectedItems $detectedItems")

            val isMatch = labels.any { label ->
                validKeywords.any { keyword -> label.text.equals(keyword, ignoreCase = true) } && label.confidence > 0.35f
            }

            if (isMatch) {
                onSuccess()
            } else {
                onFail("Gambar tidak sesuai, terdeteksi: $detectedItems ")
            }
        }
        .addOnFailureListener { message ->
            Log.e(TAG, "analyzePhoto: error analyze: $message")
            onFail("Gagal menganalisis foto, coba lagi")
        }
}

@Preview
@Composable
private fun AlarmRingScreenPreview() {
    EasyWakeUpTheme {
        AlarmRingScreen()
    }
}