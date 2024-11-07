package br.pucpr.akatsukimemory

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds


class MainActivity : ComponentActivity() {
    private lateinit var soundPool: SoundPool
    private lateinit var mediaPlayer: MediaPlayer
    private var soundTheme: Int = 0
    private var soundFlip: Int = 0
    private var soundYooo: Int = 0
    private var points: Int = 0;
    private val customFontFamily = FontFamily(
        Font(R.font.protestrevolutionregular, FontWeight.Normal)
    )
    private val gray = Color(android.graphics.Color.parseColor("#1C1C24"))
    private val red = Color(android.graphics.Color.parseColor("#C1392B"))
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        mediaPlayer = MediaPlayer.create(this, R.raw.theme).apply {
            setVolume(0.3f, 0.3f)
            isLooping = true
            start()
        }

        soundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            SoundPool.Builder()
                .setMaxStreams(3)
                .setAudioAttributes(audioAttributes)
                .build()
        } else {
            SoundPool(3, AudioManager.STREAM_MUSIC, 0)
        }

        soundTheme = soundPool.load(this, R.raw.theme, 1)
        soundFlip = soundPool.load(this, R.raw.flip, 1)
        soundYooo = soundPool.load(this, R.raw.yoooo, 1)



        setContent {


            var showModal by remember { mutableStateOf(false) }
            var first by remember { mutableStateOf(true) }
            var time by remember { mutableIntStateOf(0) }


            if (!first) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gray)
                        .padding(top = 32.dp)
                ) {
                    TimerText(customFontFamily, time, onTimeUp = { showModal = true })
                    MyGrid()
                    if (showModal) {
                        FloatingCard(onClose = {
                            showModal = false
                            first = true
                        }) // Pass the close action to the card
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gray)
                        .padding(top = 32.dp)
                ) {
                    RotatingImage()

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 128.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            modifier = Modifier
                                .padding(2.dp),
                            onClick = {
                                first = false
                                time = 60
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = red),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Fácil",
                                fontFamily = customFontFamily,
                                fontSize = 32.sp,
                                color = gray
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            modifier = Modifier
                                .padding(2.dp),
                            onClick = {
                                first = false
                                time = 45
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = red),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Difícil",
                                fontFamily = customFontFamily,
                                fontSize = 32.sp,
                                color = gray
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            modifier = Modifier
                                .padding(2.dp),
                            onClick = {
                                first = false
                                time = 2
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = red),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "TRYHARD!!!!",
                                fontFamily = customFontFamily,
                                fontSize = 32.sp,
                                color = gray
                            )
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun TimerText(
        font: FontFamily = FontFamily.Serif,
        time: Int,
        onTimeUp: () -> Unit
    ) {
        var ticks by remember { mutableIntStateOf(time) }

        LaunchedEffect(Unit) {
            while (ticks > 0) {
                delay(1.seconds)
                ticks--
            }
            onTimeUp()
            soundPool.play(soundYooo, 1f, 1f, 1, 0, 1f)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = "Tempo: $ticks",
                color = Color.White,
                fontSize = 32.sp,
                fontFamily = font
            )
        }
    }


    @Composable
    fun MyGrid() {
        val drawableList = listOf(
            R.drawable.deidara,
            R.drawable.itachi,
            R.drawable.kakuzu,
            R.drawable.kisame,
            R.drawable.konan,
            R.drawable.nagato,
            R.drawable.obito,
            R.drawable.orochimaru,
            R.drawable.sasori,
            R.drawable.sasuke
        )

        val selectedItems = drawableList.shuffled().take(6)
        val pairedItems = selectedItems + selectedItems
        val shuffledItems = pairedItems.shuffled()

        val cards = shuffledItems.map { insideDrawableId ->
            CardItem(
                outside = painterResource(id = R.drawable.akatsuki),
                inside = painterResource(id = insideDrawableId)
            )
        }

        val points by remember { mutableIntStateOf(0) }
        Box(modifier = Modifier.padding(top = 32.dp)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3), // 3 columns
                modifier = Modifier.padding(0.dp)
            ) {
                items(cards) { item ->
                    AkatsukiCard(item, onSound = { soundPool.play(soundFlip, 1f, 1f, 1, 0, 1f) })
                }
            }
        }
    }

    @Composable
    fun FloatingCard(onClose: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = gray, shape = RoundedCornerShape(8.dp))
                .border(2.dp, red, RoundedCornerShape(8.dp))
                .clickable { onClose() }
        ) {
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "ERROU!!!", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Text(
                            text = "Pontos: $points",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontFamily = customFontFamily
                        )
                    }


                    Button(
                        onClick = onClose,
                        modifier = Modifier
                            .padding(2.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = red),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Close",
                            fontFamily = customFontFamily,
                            fontSize = 32.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}







