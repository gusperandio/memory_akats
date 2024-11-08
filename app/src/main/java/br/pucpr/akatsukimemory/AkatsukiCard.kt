package br.pucpr.akatsukimemory

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

data class CardItem(
    val outside: androidx.compose.ui.graphics.painter.Painter,
    val inside: androidx.compose.ui.graphics.painter.Painter,
    val rotated: MutableState<Boolean>
)

@Composable
fun AkatsukiCard(
    cardItem: CardItem,
    onSound: () -> Unit,

) {
    val modifier: Modifier = Modifier
    val rotation by animateFloatAsState(
        targetValue = if (cardItem.rotated.value) 180f else 0f,
        animationSpec = tween(500)
    )

    val animateFront by animateFloatAsState(
        targetValue = if (!cardItem.rotated.value) 1f else 0f,
        animationSpec = tween(500)
    )

    val animateBack by animateFloatAsState(
        targetValue = if (cardItem.rotated.value) 1f else 0f,
        animationSpec = tween(200)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth(0.4f)
            .padding(16.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 6 * density
            }
            .clickable {
                cardItem.rotated.value = !cardItem.rotated.value
                onSound()
            },

    ) {
        Card(
            modifier = modifier
                .shadow(
                    elevation = 10.dp,
                    spotColor = Color.White,
                    shape = RoundedCornerShape(8.dp))
                .fillMaxWidth().background(Color.Black),
            shape = RoundedCornerShape(15.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            ),
        ) {
            if (!cardItem.rotated.value) {
                Box(modifier = Modifier
                    .height(170.dp)
                    .graphicsLayer {
                        alpha = animateFront
                    }) {
                    Image(
                        painter = cardItem.outside,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black
                                    ),
                                    startY = 300f
                                )
                            )
                    )
                }
            } else {
                Box(modifier = Modifier
                    .height(170.dp)
                    .graphicsLayer {
                        alpha = animateBack
                        rotationY = rotation
                    }) {
                    Image(
                        painter = cardItem.inside,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black
                                    ),
                                    startY = 300f
                                )
                            )
                    )

                }
            }
        }
    }
}