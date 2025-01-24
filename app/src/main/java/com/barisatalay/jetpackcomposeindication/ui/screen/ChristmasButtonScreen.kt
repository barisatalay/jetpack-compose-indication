package com.barisatalay.jetpackcomposeindication.ui.screen

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun ChristmasButtonScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center),
            color = Color.Red,
            text = "Merry Christmas"
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(fraction = 0.9f)
                .height(156.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Red)
                .clickable(
                    interactionSource = null,
                    indication = ChristmasIndication(1f),
                    onClick = { /*TODO*/ }
                ),
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
) : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return ChristmasIndicationNode(interactionSource, speed)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChristmasIndication) return false
        if (speed != other.speed) return false
        return false
    }

    override fun hashCode(): Int {
        return 31 * speed.hashCode() + this.hashCode()
    }
}

private data class Snow(
    val x: Float,
    val y: Float,
    val radius: Float,
    val speed: Float,
)

private class ChristmasIndicationNode(
    private val interactionSource: InteractionSource,
    private val speed: Float,
) : Modifier.Node(), DrawModifierNode {
    private val defaultInteraction = PressInteraction.Release(PressInteraction.Press(Offset.Zero))
    private var currentInteraction by mutableStateOf<Interaction>(defaultInteraction)

    private val snowflakes = arrayListOf<Snow>()
    private val offsetY = Animatable(0f)


    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                currentInteraction = interaction
                when (interaction) {
                    is PressInteraction.Press -> onPressed()
                    else -> animateToRelease()
                    // is PressInteraction.Release -> animateToRelease()
                    // is PressInteraction.Cancel -> animateToRelease()
                }
            }
        }
    }

    private fun onPressed() {
        animateToPressed()
        createSnowflakes()
    }

    private fun printLog(msg: String) {
        Log.d("baris-", msg)
    }

    private fun animateToPressed() = coroutineScope.launch {
        printLog("animateToPressed:begin")
        offsetY.animateTo(
            targetValue = 10000f,
            animationSpec = tween(durationMillis = 50000, easing = FastOutLinearInEasing),
        )
    }

    private fun animateToRelease() = coroutineScope.launch {
        offsetY.snapTo(0f)
        offsetY.stop()
        snowflakes.clear()
    }

    private fun createSnowflakes() = coroutineScope.launch {
        printLog("createSnowflakes:begin")
        snowflakes.add(
            Snow(
                y = Random.nextFloat() * 10f * -1,
                x = 0.3f,
                radius = 5f, // Snowflake size
                speed = speed,// Falling speed
            )
        )
        invalidateDraw()
        delay(100)
        printLog("createSnowflakes:end")
    }

    override fun ContentDrawScope.draw() {
        drawContent()

        if (currentInteraction is PressInteraction.Press) {
            snowflakes.forEachIndexed { index, snow ->
                val offset = offsetY.value % 2
                val newY = snow.y + offset * snow.speed
                printLog("drawCircle: x=${snow.x * size.width}, y=$newY")
                if (newY < size.height) {
                    snowflakes[index] = snow.copy(y = newY)
                    drawCircle(Color.White, radius = snow.radius, center = Offset(snow.x * size.width, newY))
                }
            }
        }
    }
}