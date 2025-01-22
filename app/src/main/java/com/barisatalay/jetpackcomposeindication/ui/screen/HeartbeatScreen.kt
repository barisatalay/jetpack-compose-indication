package com.barisatalay.jetpackcomposeindication.ui.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.barisatalay.jetpackcomposeindication.PathConstant
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HeartbeatScreen() {
    Box(
        Modifier
            .background(Color.Cyan)
            .fillMaxSize()
    ) {
        Text(
            text = "Heartbeat Example",
            fontSize = 25.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp)
        )

        Canvas(
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.Center)
                .clickable(indication = PulseIndication, interactionSource = null) {}
        ) {
            val path = PathConstant.heart(width = size.width, height = size.height)
            drawPath(color = Color.Red, path = path, style = Fill)
        }
        Text(
            text = "Click to heart",
            fontSize = 12.sp,
            modifier = Modifier
                .padding(bottom = 100.dp)
                .align(Alignment.BottomCenter)
        )
    }
}

private object PulseIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return PulseIndicationNode(interactionSource)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PulseIndicationNode) return false
        return false
    }

    override fun hashCode(): Int {
        return this.hashCode()
    }
}

private class PulseIndicationNode(
    private val interactionSource: InteractionSource
) : Modifier.Node(), DrawModifierNode {
    private val defaultInteraction = PressInteraction.Release(PressInteraction.Press(Offset.Zero))
    private var currentInteraction by mutableStateOf<Interaction>(defaultInteraction)

    private val animatedScalePercent = Animatable(1f)

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                currentInteraction = interaction

                when (interaction) {
                    is PressInteraction.Press -> animateToPressed()
                }
            }
        }
    }

    private fun animateToPressed() = coroutineScope.launch {
        val duration = 100
        val linearAnimationSpec: AnimationSpec<Float> = tween(
            durationMillis = duration,
            easing = LinearEasing
        )

        val fastOutLinearAnimationSpec: AnimationSpec<Float> = tween(
            durationMillis = duration,
            easing = FastOutLinearInEasing
        )
        repeat(3) {
            animatedScalePercent.animateTo(targetValue = 1.5f, animationSpec = linearAnimationSpec)
            animatedScalePercent.animateTo(targetValue = 1f, animationSpec = linearAnimationSpec)
            animatedScalePercent.animateTo(targetValue = 1.25f, animationSpec = fastOutLinearAnimationSpec)
            animatedScalePercent.animateTo(targetValue = 1f, animationSpec = fastOutLinearAnimationSpec)
            delay(300)
        }
    }

    override fun ContentDrawScope.draw() {
        scale(
            scale = animatedScalePercent.value,
            pivot = center
        ) {
            this@draw.drawContent()
        }
    }
}