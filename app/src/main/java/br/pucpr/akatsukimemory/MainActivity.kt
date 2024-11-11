package br.pucpr.akatsukimemory

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds


class MainActivity : ComponentActivity() {
    private lateinit var soundPool: SoundPool
    private lateinit var mediaPlayer: MediaPlayer
    private var soundTheme: Int = 0
    private var soundFlip: Int = 0
    private var soundYooo: Int = 0
    private var soundWin: Int = 0
    private var points: Int = 0;
    private var okCard: Int = 0;
    private var message: String = "";
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
            val audioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
            SoundPool.Builder().setMaxStreams(3).setAudioAttributes(audioAttributes).build()
        } else {
            SoundPool(3, AudioManager.STREAM_MUSIC, 0)
        }

        soundTheme = soundPool.load(this, R.raw.theme, 1)
        soundFlip = soundPool.load(this, R.raw.flip, 1)
        soundYooo = soundPool.load(this, R.raw.yoooo, 1)
        soundWin = soundPool.load(this, R.raw.win, 1)

        setContent {
            var showModal by remember { mutableStateOf(false) }
            var first by remember { mutableStateOf(true) }
            var time by remember { mutableIntStateOf(0) }
            var restartKey by remember { mutableIntStateOf(0) }

            if (!first) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gray)
                        .padding(top = 32.dp)
                ) {
                    TimerText(
                        customFontFamily, time, onTimeUp = { showModal = true }, key = restartKey
                    )
                    MyGrid(key = restartKey)
                    if (showModal) {
                        FloatingCard(onBack = {
                            showModal = false
                            first = true
                        }, onRestart = {
                            showModal = false
                            restartKey++
                        },
                        points)
                    }
                }
            } else {
                FirstScreen(easyClick = {
                    first = false
                    time = 60
                }, mediumClick = {
                    first = false
                    time = 45
                }, hardClick = {
                    first = false
                    time = 20
                })
            }
        }
    }


    @Composable
    fun FirstScreen(easyClick: () -> Unit, mediumClick: () -> Unit, hardClick: () -> Unit) {
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
                    modifier = Modifier.padding(2.dp),
                    onClick = {
                        easyClick()
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
                    modifier = Modifier.padding(2.dp),
                    onClick = {
                        mediumClick()
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
                    modifier = Modifier.padding(2.dp),
                    onClick = {
                        hardClick()
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

    @Composable
    fun TimerText(
        font: FontFamily = FontFamily.Serif, time: Int, onTimeUp: () -> Unit, key: Int
    ) {
        var ticks by remember(key) { mutableIntStateOf(time) }

        LaunchedEffect(key) {
            while (ticks > 0) {
                delay(1.seconds)
                ticks--

                if (ticks == 0)
                    message = "Tempo Acabou!"

                if (okCard == 6){
                    message = "Shinobi vencedor!!!"
                    okCard = 0
                    ticks = -1
                }
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
                text = "Tempo: $ticks", color = Color.White, fontSize = 32.sp, fontFamily = font
            )
        }
    }

    @Composable
    fun MyGrid(key: Int) {
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

        val shuffledDrawableIds = remember(key) {
            val selectedItems = drawableList.shuffled().take(6)
            val pairedItems = selectedItems + selectedItems
            pairedItems.shuffled()
        }

        var firstSelected by remember { mutableStateOf<Int?>(null) }
        var index by remember { mutableStateOf<Int?>(null) }
        val coroutineScope = rememberCoroutineScope()
        var bonusPoints by remember { mutableStateOf<Int>(10) }
        val cards = shuffledDrawableIds.mapIndexed { indexx, drawableId ->
            val rotated = remember { mutableStateOf(false) }
            CardItem(
                outside = painterResource(id = R.drawable.akatsuki),
                inside = painterResource(id = drawableId),
                rotated = rotated,
                index = indexx,
                id = drawableId
            )
        }

        Box(modifier = Modifier.padding(top = 32.dp)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3), // 3 columns
                modifier = Modifier.padding(0.dp)
            ) {
                items(cards) { item ->
                    AkatsukiCard(item,
                        onSound = { soundPool.play(soundFlip, 1f, 1f, 1, 0, 1f) },
                        onClick = {

                            if (firstSelected == null) {
                                firstSelected = item.id
                                index = item.index
                            } else {
                                if (firstSelected == item.id) {
                                    okCard += 1
                                    points += (1000 - (bonusPoints * 10)) * bonusPoints
                                } else {
                                    if(bonusPoints > 1)
                                        bonusPoints--

                                    coroutineScope.launch {
                                        delay(1000)
                                        cards[index!!].rotated.value = false
                                        item.rotated.value = false
                                    }
                                }
                                firstSelected = null
                            }
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun FloatingCard(onBack: () -> Unit, onRestart: () -> Unit, numbers : Int) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        ) {
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
                    .background(Color.Black)
                    .border(2.dp, red, RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.background(gray),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "TEMPO ACABOU!!!",
                        fontFamily = customFontFamily,
                        color = Color.White,
                        fontSize = 32.sp,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Text(
                            text = "Pontos: $numbers",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontFamily = customFontFamily
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 22.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = onRestart,
                            modifier = Modifier.padding(vertical = 22.dp, horizontal = 6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = red),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Reiniciar",
                                fontFamily = customFontFamily,
                                fontSize = 32.sp,
                                color = Color.White
                            )
                        }

                        Button(
                            onClick = onBack,
                            modifier = Modifier.padding(vertical = 22.dp, horizontal = 6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = red),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Voltar",
                                fontFamily = customFontFamily,
                                fontSize = 32.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}








