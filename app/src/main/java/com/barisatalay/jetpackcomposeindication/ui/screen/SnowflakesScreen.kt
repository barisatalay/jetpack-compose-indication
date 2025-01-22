package com.barisatalay.jetpackcomposeindication.ui.screen

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun SnowflakesScreen() {
    Box(
        Modifier
            .background(Color.Red)
            .fillMaxSize()
            .clickable(
                onClick = {},
                indication = SnowflakesIndication,
                interactionSource = null
            )
    ) {
        Text(
            text = "Snowflakes Example",
            fontSize = 25.sp,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp)
        )
        Text(
            text = "Click me",
            fontSize = 12.sp,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


private object SnowflakesIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return SnowflakesIndicationNode(interactionSource)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SnowflakesIndicationNode) return false
        return false
    }

    override fun hashCode(): Int {
        return this.hashCode()
    }
}

private data class Snowflake(
    var x: Float,
    var y: Float,
    var radius: Float,
    var speed: Float
)

private class SnowflakesIndicationNode(
    private val interactionSource: InteractionSource
) : Modifier.Node(), DrawModifierNode {
    private val offsetY = Animatable(0f)

    private val defaultInteraction = PressInteraction.Release(PressInteraction.Press(Offset.Zero))
    private var currentInteraction by mutableStateOf<Interaction>(defaultInteraction)
    private val snowflakes by lazy { List(100) { generateRandomSnowflake() } }

    fun generateRandomSnowflake(): Snowflake {
        return Snowflake(
            x = Random.nextFloat(),
            y = Random.nextFloat() * 1000f,
            radius = Random.nextFloat() * 2f + 2f, // Snowflake size
            speed = Random.nextFloat() * 1.2f + 1f  // Falling speed
        )
    }

    fun ContentDrawScope.drawSnowflake(snowflake: Snowflake, offsetY: Float) {
        val newY = (snowflake.y + offsetY * snowflake.speed) % size.height
        drawCircle(Color.White, radius = snowflake.radius, center = Offset(snowflake.x * size.width, newY))
    }

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                currentInteraction = interaction
                when (interaction) {
                    is PressInteraction.Press -> animateToPressed()
                    is PressInteraction.Release -> animateToRelease()
                    is PressInteraction.Cancel -> animateToRelease()
                }
            }
        }
    }

    private fun animateToRelease() = coroutineScope.launch {
        offsetY.snapTo(0f)
        offsetY.stop()
    }

    private fun animateToPressed() = coroutineScope.launch {
        offsetY.animateTo(
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(
                animation = tween(5000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    override fun ContentDrawScope.draw() {
        if (currentInteraction is PressInteraction.Press) {
            snowflakes.forEach { snowflake ->
                drawSnowflake(snowflake, offsetY.value % size.height)
            }
        }
        this.drawContent()
    }
}
