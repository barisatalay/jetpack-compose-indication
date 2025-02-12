package com.barisatalay.jetpackcomposeindication.ui.screen

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.LayoutAwareModifierNode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

private const val WAVE_AMPLITUDE_MIN = 5f
private const val WAVE_AMPLITUDE_MAX = 100f

private const val WAVE_LENGTH_MIN = 1f
private const val WAVE_LENGTH_MAX = 10f

private const val FLAKE_CREATE_SPEED = 100L

private const val SNOW_AMPLITUDE_ANIMATION_DURATION = 3_000

private const val SNOW_ACCUMULATE_MULTIPLIER = 1f

@Composable
fun ChristmasButtonScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7D08A))
    ) {
        var valueWaveAmplitude by remember { mutableFloatStateOf(50f) }
        var valueWaveLength by remember { mutableFloatStateOf(3f) }
        var valueSpeed by remember { mutableFloatStateOf(1f) }

        Column(modifier = Modifier.align(Alignment.TopCenter)) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Red,
                text = "Merry Christmas",
                textAlign = TextAlign.Center
            )

            val modifier = Modifier
                .padding(horizontal = 96.dp, vertical = 16.dp)
                .fillMaxWidth()

            val valueAmplitudeRange = WAVE_AMPLITUDE_MIN..WAVE_AMPLITUDE_MAX
            val valueLengthRange = WAVE_LENGTH_MIN..WAVE_LENGTH_MAX
            val valueSpeedRange = 1f..100f

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    text = "Max Wave Amplitude: $valueWaveAmplitude"
                )
                Slider(
                    modifier = modifier,
                    valueRange = valueAmplitudeRange,
                    colors = SliderDefaults.colors(),
                    value = valueWaveAmplitude,
                    onValueChange = { valueWaveAmplitude = it },
                )
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    text = "Max Wave Length: $valueWaveLength"
                )
                Slider(
                    modifier = modifier,
                    valueRange = valueLengthRange,
                    colors = SliderDefaults.colors(),
                    value = valueWaveLength,
                    onValueChange = { valueWaveLength = it },
                )
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    text = "Speed: $valueSpeed"
                )
                Slider(
                    modifier = modifier,
                    valueRange = valueSpeedRange,
                    colors = SliderDefaults.colors(),
                    value = valueSpeed,
                    onValueChange = { valueSpeed = it },
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .fillMaxWidth(fraction = 0.9f)
                .height(350.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Red)
                .clickable(interactionSource = null, indication = ChristmasIndication(
                    speed = valueSpeed,
                    waveMaxAmplitude = valueWaveAmplitude.toInt(),
                    waveMaxLength = valueWaveLength.toInt(),
                    flakeCreateSpeed = FLAKE_CREATE_SPEED,
                ), onClick = { /*TODO*/ }),
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Make It Snow",
                color = Color.White
            )
        }
    }
}

private class ChristmasIndication(
    private val speed: Float,
    private val waveMaxAmplitude: Int,
    private val waveMaxLength: Int,
    private val flakeCreateSpeed: Long,
) : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return ChristmasIndicationNode(interactionSource, speed, waveMaxAmplitude, waveMaxLength, flakeCreateSpeed)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChristmasIndication) return false
        if (speed != other.speed) return false
        if (waveMaxAmplitude != other.waveMaxAmplitude) return false
        if (waveMaxLength != other.waveMaxLength) return false
        return false
    }

    override fun hashCode(): Int {
        var result = speed.hashCode()
        result = 31 * result + waveMaxAmplitude.hashCode()
        result = 31 * result + waveMaxLength.hashCode()
        result = 31 * result + flakeCreateSpeed.hashCode()
        return result
    }
}

data class WaveProperties(
    val amplitude: Int,
    val length: Double,
    val isForward: Boolean,
)

private data class Snow(
    val x: Int,
    val y: Float,
    val radius: Float,
    val speed: Float,
    val waveProperties: WaveProperties,
)

private class ChristmasIndicationNode(
    private val interactionSource: InteractionSource,
    private val speed: Float,
    private val waveMaxAmplitude: Int,
    private val waveMaxLength: Int,
    private val flakeCreateSpeed: Long,
) : Modifier.Node(), DrawModifierNode, LayoutAwareModifierNode {
    private val defaultInteraction = PressInteraction.Release(PressInteraction.Press(Offset.Zero))
    private var currentInteraction by mutableStateOf<Interaction>(defaultInteraction)

    private val snowflakes = arrayListOf<Snow>()
    private val offsetY = Animatable(0f)
    private val snowAmplitudeProgress = Animatable(0f)

    private var contentSize: IntSize = IntSize.Zero
    private var remainingHeight = -1f
    private var accumulatedSnowflakes = 0

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                currentInteraction = interaction
                when (interaction) {
                    is PressInteraction.Press -> onPressed()
                    else -> animateToRelease()
                }
            }
        }
    }

    override fun onRemeasured(size: IntSize) {
        super.onRemeasured(size)
        contentSize = size
        assignRemainingHeight()
    }

    private fun onPressed() {
        assignRemainingHeight()
        animateToPressed()
        createSnowflakes()
    }

    private fun assignRemainingHeight() {
        remainingHeight = contentSize.height.toFloat()
    }

    private fun printLog(msg: String) {
        Log.d("Christmas-", msg)
    }

    private fun animateToPressed() {
        printLog("animateToPressed:begin")
        startOffsetYAnimation()
        startSnowAmplitudeAnimation()
    }

    private fun startSnowAmplitudeAnimation() = coroutineScope.launch {
        snowAmplitudeProgress.animateTo(
            targetValue = 1f, animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = SNOW_AMPLITUDE_ANIMATION_DURATION, easing = LinearEasing
                ), repeatMode = RepeatMode.Restart
            )
        )
    }

    private fun startOffsetYAnimation() = coroutineScope.launch {
        offsetY.animateTo(
            targetValue = 10000f,
            animationSpec = tween(durationMillis = 50000, easing = LinearEasing),
        )
    }

    private fun animateToRelease() = coroutineScope.launch {
        offsetY.snapTo(0f)
        offsetY.stop()
        snowflakes.clear()
    }

    private fun createSnowflakes() = coroutineScope.launch {
        printLog("createSnowflakes:begin")
        while (currentInteraction is PressInteraction.Press) {
            val to = if (contentSize.width == 0) Random.nextFloat() else contentSize.width

            snowflakes.add(
                Snow(
                    x = Random.nextDouble(0.0, to.toDouble()).toInt(),
                    y = Random.nextFloat() * 10f * -1,
                    radius = Random.nextFloat() * 2f + 4f, // Snowflake size
                    speed = speed + Random.nextFloat() * 3.2f + 1f,   // Falling speed
                    waveProperties = WaveProperties(
                        length = Math.PI * Random.nextInt(
                            from = 1,
                            until = waveMaxLength,
                        ),
                        amplitude = Random.nextInt(
                            from = 1,
                            until = waveMaxAmplitude,
                        ),
                        isForward = Random.nextBoolean(),
                    ),
                )
            )
            delay(flakeCreateSpeed)
        }
        printLog("createSnowflakes:end")
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        if (currentInteraction is PressInteraction.Press) {
            snowflakes.forEachIndexed { index, snow ->
                val offsetWithSpeed = (offsetY.value % 2) * snow.speed
                var newY = snow.y
                var newX = snow.x
                val isNewStateOut = remainingHeight != -1f && (newY + offsetWithSpeed) >= remainingHeight

                if (isNewStateOut.not()) {
                    newY += offsetWithSpeed

                    newX += calculateWind(snow.waveProperties)
                }

                drawCircle(Color.White, radius = snow.radius, center = Offset(newX.toFloat(), newY))

                drawRect(
                    color = Color.White,
                    topLeft = Offset(
                        x = 0f,
                        y = remainingHeight
                    ),
                    size = Size(
                        width = size.width,
                        height = size.height - remainingHeight
                    )
                )
                if (isNewStateOut.not()) {
                    snowflakes[index] = snow.copy(y = newY)
                } else {
                    accumulatedSnowflakes += 1

                    if (accumulatedSnowflakes % size.width.toInt() == 0) {
                        remainingHeight -= SNOW_ACCUMULATE_MULTIPLIER
                        accumulatedSnowflakes = 0
                    }
                }
            }
        }
    }

    private fun calculateWind(waveProperties: WaveProperties): Int {
        val waveDirection = if (waveProperties.isForward) 1 else -1
        val sin = sin(snowAmplitudeProgress.value * waveProperties.length).toFloat()

        return (waveDirection * waveProperties.amplitude * sin).toInt()
    }
}