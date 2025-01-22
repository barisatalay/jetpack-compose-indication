package com.barisatalay.jetpackcomposeindication.ui.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun Shake1Screen() {
    Box(
        Modifier
            .background(Color.Cyan)
            .fillMaxSize()
            .clickable(
                onClick = {},
                interactionSource = remember { MutableInteractionSource() },
                indication = customShakeIndication(
                    strength = Strength.Strong,
                    direction = Direction.LEFT_THEN_RIGHT,
                    animationDuration = 41
                )
            )
    ) {
        Text(
            text = "Shake 1 Example \n\n\nLeft then Right",
            textAlign = TextAlign.Center,
            fontSize = 25.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp)
        )
        Text(
            text = "Click me",
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Stable
fun customShakeIndication(
    strength: Strength,
    direction: Direction,
    animationDuration: Int? = null
): IndicationNodeFactory {
    return ShakeIndication(strength, direction, animationDuration)
}

private class ShakeIndication(
    private val strength: Strength,
    private val direction: Direction,
    private val animationDuration: Int? = null
) : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return ShakeIndicationNode(interactionSource, strength, direction, animationDuration)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ShakeIndication) return false
        if (strength != other.strength) return false
        return animationDuration == other.animationDuration
    }

    override fun hashCode(): Int {
        var result = strength.hashCode()
        result = 31 * result + direction.hashCode()
        result = 31 * result + animationDuration.hashCode()
        return result
    }
}

private class ShakeIndicationNode(
    private val interactionSource: InteractionSource,
    private val strength: Strength,
    private val direction: Direction,
    private val animationDuration: Int? = null,
) : Modifier.Node(), DrawModifierNode {
    val offsetX = Animatable(0f)

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> {
                        animateToPressed()
                    }
                    else -> animateToRelease()
                }
            }
        }
    }

    private suspend fun animateToRelease() {
        offsetX.snapTo(0f)
        offsetX.stop()
    }

    private suspend fun animateToPressed() {
        shake(animationDuration ?: 50)
    }

    suspend fun shake(animationDuration: Int = 50) {
        val shakeAnimationSpec: AnimationSpec<Float> = tween(animationDuration)
        when (direction) {
            Direction.LEFT -> shakeToLeft(shakeAnimationSpec)
            Direction.RIGHT -> shakeToRight(shakeAnimationSpec)
            Direction.LEFT_THEN_RIGHT -> shakeToLeftThenRight(shakeAnimationSpec)
            Direction.RIGHT_THEN_LEFT -> shakeToRightThenLeft(shakeAnimationSpec)
        }
    }

    private suspend fun shakeToLeft(shakeAnimationSpec: AnimationSpec<Float>) {
        repeat(3) {
            offsetX.animateTo(-strength.value, shakeAnimationSpec)
            offsetX.animateTo(0f, shakeAnimationSpec)
        }
    }

    private suspend fun shakeToRight(shakeAnimationSpec: AnimationSpec<Float>) {
        repeat(3) {
            offsetX.animateTo(strength.value, shakeAnimationSpec)
            offsetX.animateTo(0f, shakeAnimationSpec)
        }
    }

    private suspend fun shakeToRightThenLeft(shakeAnimationSpec: AnimationSpec<Float>) {
        repeat(3) {
            offsetX.animateTo(strength.value, shakeAnimationSpec)
            offsetX.animateTo(0f, shakeAnimationSpec)
            offsetX.animateTo(-strength.value / 2, shakeAnimationSpec)
            offsetX.animateTo(0f, shakeAnimationSpec)
        }
    }

    private suspend fun shakeToLeftThenRight(shakeAnimationSpec: AnimationSpec<Float>) {
        repeat(3) {
            offsetX.animateTo(-strength.value, shakeAnimationSpec)
            offsetX.animateTo(0f, shakeAnimationSpec)
            offsetX.animateTo(strength.value / 2, shakeAnimationSpec)
            offsetX.animateTo(0f, shakeAnimationSpec)
        }
    }

    override fun ContentDrawScope.draw() {
        translate(left = offsetX.value) {
            this@draw.drawContent()
        }
    }
}


sealed class Strength(val value: Float) {
    data object Normal : Strength(17f)
    data object Strong : Strength(40f)
    data class Custom(val strength: Float) : Strength(strength)
}

enum class Direction {
    LEFT, RIGHT, LEFT_THEN_RIGHT, RIGHT_THEN_LEFT
}