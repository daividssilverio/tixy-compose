package com.doivid.tixy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.asDisposableClock
import androidx.compose.animation.core.AnimationClockObserver
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AmbientAnimationClock
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.doivid.tixy.ui.TixyTheme
import java.time.OffsetDateTime
import kotlin.math.absoluteValue
import kotlin.math.sign

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TixyTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = Color.Black, modifier = Modifier.fillMaxSize()) {
                    Frame()
                }
            }
        }
    }
}

@Composable
fun Frame() {
    val clock = AmbientAnimationClock
    val time = remember { mutableStateOf(0f) }
    val startTime = remember { OffsetDateTime.now() }
    val observer = remember {
        object : AnimationClockObserver {
            override fun onAnimationFrame(frameTimeMillis: Long) {
                time.value =
                    (OffsetDateTime.now().toInstant().toEpochMilli() - startTime.toInstant()
                        .toEpochMilli()) / 1000f
            }
        }
    }
    clock.current.asDisposableClock().subscribe(observer)
    Box(modifier = Modifier.fillMaxWidth()) {
        Column {
            for (y in 0 until 16) {
                Row {
                    for (x in 0 until 16) {
                        Dot(
                            modifier = Modifier.size(16.dp),
                            value = tixy(time.value, x * 16 + y, x, y)
                        )
                    }
                }
            }
        }
    }
}


fun tixy(t: Float, i: Int, x: Int, y: Int): Float {
    return y - t * 4
}

@Composable
fun Dot(value: Float, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier, onDraw = {
        drawCircle(
            color = Color.Red.copy(
                alpha = value.absoluteValue.coerceIn(0f, 1f),
                green = value.sign.coerceAtLeast(0f),
                blue = value.sign.coerceAtLeast(0f),
            ),
            radius = (size.height / 2) * value.absoluteValue.coerceIn(0f, 1f)
        )
    })
}

@Preview(showBackground = true)
@Composable
fun DotPreview() {
    TixyTheme {
        Dot(modifier = Modifier.size(16.dp), value = 1f)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TixyTheme {
        Frame()
    }
}