package com.barisatalay.jetpackcomposeindication.ui.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
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

@Composable
fun FillableScreen() {
    Box(
        Modifier
            .background(Color.Cyan)
            .fillMaxSize()
            .clickable(
                onClick = {},
                indication = FillableContentIndication,
                interactionSource = null,
            )
    ) {
        Text(
            text = "Fillable Example",
            fontSize = 25.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp)
        )
        Text(
            text = "Click me and hold",
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

private object FillableContentIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return FillableContentIndicationNode(interactionSource)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FillableContentIndicationNode) return false
        return false
    }

    override fun hashCode(): Int {
        return this.hashCode()
    }
}

private class FillableContentIndicationNode(
    private val interactionSource: InteractionSource
) : Modifier.Node(), DrawModifierNode {
    var currentPressPosition: Offset = Offset.Zero

    private val defaultInteraction = PressInteraction.Release(PressInteraction.Press(Offset.Zero))
    private var currentInteraction by mutableStateOf<Interaction>(defaultInteraction)

    private val offsetX = Animatable(0f)

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                currentInteraction = interaction
                when (interaction) {
                    is PressInteraction.Press -> animateToPressed(interaction.pressPosition)
                    is PressInteraction.Cancel -> animateToResting(interaction.press.pressPosition)
                    is PressInteraction.Release -> animateToResting(interaction.press.pressPosition)
                }
            }
        }
    }

    private fun animateToResting(pressPosition: Offset) = coroutineScope.launch {
        currentPressPosition = pressPosition

        if (offsetX.isRunning) offsetX.stop()

        offsetX.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
        )
    }

    private fun animateToPressed(pressPosition: Offset) = coroutineScope.launch {
        currentPressPosition = pressPosition

        offsetX.animateTo(
            targetValue = 10000f,
            animationSpec = tween(durationMillis = 10000, easing = LinearEasing),
        )
    }

    override fun ContentDrawScope.draw() {
        val updatedWidth = if (offsetX.value < size.width)
            offsetX.value
        else size.width

        val alpha = updatedWidth / size.width
        drawRect(
            color = Color.Blue.copy(alpha = alpha),
            topLeft = Offset.Zero,
            size = size.copy(width = updatedWidth)
        )

        this.drawContent()
    }
}