package com.barisatalay.jetpackcomposeindication.ui.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BubblingScreen() {
    Box(
        Modifier
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxSize()
            .clickable(
                onClick = {},
                indication = BubblingIndication,
                interactionSource = null,
            )
    ) {
        Text(
            text = "Bubbling Example",
            fontSize = 25.sp,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp)
        )
        Text(
            text = "Click me", fontSize = 12.sp, color = Color.White, modifier = Modifier.align(Alignment.Center)
        )
    }
}

private object BubblingIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return BubblingIndicationNode(interactionSource)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BubblingIndicationNode) return false
        return false
    }

    override fun hashCode(): Int {
        return this.hashCode()
    }
}

private data class Bubble(
    val offset: Offset,
)

private class BubblingIndicationNode(
    private val interactionSource: InteractionSource
) : Modifier.Node(), DrawModifierNode {
    var currentPressPosition: Offset = Offset.Zero

    private val defaultInteraction = PressInteraction.Release(PressInteraction.Press(Offset.Zero))
    private var currentInteraction by mutableStateOf<Interaction>(defaultInteraction)
    val offset = Animatable(0f)
    val bubbles = arrayListOf<Bubble>()
    val bubbleCount = 10

    private fun generateRandomBubble(centerPosition: Offset): List<Bubble> {
        return buildList {
            for (i in 0..bubbleCount) {
                add(
                    Bubble(
                        offset = centerPosition,
                    )
                )
            }
        }
    }

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                currentInteraction = interaction
                when (interaction) {
                    is PressInteraction.Press -> animateToPressed(interaction.pressPosition)
                }
            }
        }
    }

    private suspend fun animateToResting() {
        offset.snapTo(0f)
        bubbles.clear()
    }

    private fun animateToPressed(pressPosition: Offset) = coroutineScope.launch(Main) {
        animateToResting()
        currentPressPosition = pressPosition
        bubbles.addAll(generateRandomBubble(pressPosition))

        invalidateDraw()
        offset.animateTo(
            targetValue = 50f,
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        )
        animateToResting()
    }

    override fun ContentDrawScope.draw() {
        try {
            if (bubbles.isEmpty()) return

            val overrideRadius: Int = offset.value.toInt()
            val bubbleSize = 10
            val angleBetweenChildren = 360 / bubbles.size
            var angle = 0

            bubbles.forEach {
                val alpha = angle * Math.PI / 180

                val x = (it.offset.x + overrideRadius * sin(alpha) - bubbleSize / 2)
                val y = (it.offset.y - overrideRadius * cos(alpha) - bubbleSize / 2)

                angle += angleBetweenChildren

                drawCircle(
                    radius = bubbleSize.toFloat(), color = Color.Cyan, center = it.offset.copy(
                        x = x.toFloat(), y = y.toFloat()
                    )
                )
            }

        } finally {
            this.drawContent()
        }
    }
}