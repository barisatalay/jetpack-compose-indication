package com.barisatalay.jetpackcomposeindication.ui.screen

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.createRippleModifierNode
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomRipple1Screen() {
    Box(
        Modifier
            .background(Color.Cyan)
            .fillMaxSize()
            .clickableWithBackgroundColor(Color.Gray) {}
    ) {
        Text(
            text = "Custom Ripple 1 Example",
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

private fun Modifier.clickableWithBackgroundColor(
    color: Color,
    onClick: () -> Unit
): Modifier {
    return this.composed {
        this.clickable(
            enabled = true,
            onClickLabel = null,
            role = null,
            onClick = onClick,
            interactionSource = remember { MutableInteractionSource() },
            indication = customRipple(
                color = color,
                radius = 1000000.dp,
                bounded = true,
                rippleAlpha = RippleAlpha(
                    pressedAlpha = 0.12f,
                    focusedAlpha = 1f,
                    draggedAlpha = 1f,
                    hoveredAlpha = 0f
                )
            )
        )
    }
}

fun Modifier.clickableWithOpaqueText(
    onClick: () -> Unit
): Modifier {
    return this.composed {
        this.clickable(
            enabled = true,
            onClickLabel = null,
            role = null,
            onClick = onClick,
            interactionSource = remember { MutableInteractionSource() },
            indication = customRipple(
                color = Color.White,
                radius = 1000000.dp,
                bounded = true,
                rippleAlpha = RippleAlpha(
                    pressedAlpha = 0.4f,
                    focusedAlpha = 1f,
                    draggedAlpha = 1f,
                    hoveredAlpha = 0f
                )
            )
        )
    }
}

@Stable
private fun customRipple(
    color: ColorProducer,
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
    rippleAlpha: RippleAlpha = CustomAlpha
): IndicationNodeFactory {
    return CustomNodeFactory(bounded, radius, color, rippleAlpha)
}

@Stable
private fun customRipple(
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
    color: Color = Color.Unspecified,
    rippleAlpha: RippleAlpha = CustomAlpha
): IndicationNodeFactory {
    return CustomNodeFactory(bounded, radius, color, rippleAlpha)
}

private class CustomNodeFactory private constructor(
    private val bounded: Boolean,
    private val radius: Dp,
    private val colorProducer: ColorProducer?,
    private val color: Color,
    private val rippleAlpha: RippleAlpha,
) : IndicationNodeFactory {
    constructor(
        bounded: Boolean,
        radius: Dp,
        colorProducer: ColorProducer,
        rippleAlpha: RippleAlpha
    ) : this(bounded, radius, colorProducer, Color.Unspecified, rippleAlpha)

    constructor(
        bounded: Boolean,
        radius: Dp,
        color: Color,
        rippleAlpha: RippleAlpha
    ) : this(bounded, radius, null, color, rippleAlpha)

    override fun create(interactionSource: InteractionSource): DelegatableNode {
        val colorProducer = colorProducer ?: ColorProducer { color }
        return DelegatingRippleNode(
            interactionSource,
            bounded,
            radius,
            colorProducer,
            rippleAlpha
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CustomNodeFactory) return false
        if (bounded != other.bounded) return false
        if (radius != other.radius) return false
        if (colorProducer != other.colorProducer) return false
        return color == other.color
    }

    override fun hashCode(): Int {
        var result = bounded.hashCode()
        result = 31 * result + radius.hashCode()
        result = 31 * result + colorProducer.hashCode()
        result = 31 * result + color.hashCode()
        return result
    }
}

private class DelegatingRippleNode(
    private val interactionSource: InteractionSource,
    private val bounded: Boolean,
    private val radius: Dp,
    private val color: ColorProducer,
    private val rippleAlpha: RippleAlpha,
) : DelegatingNode(), CompositionLocalConsumerModifierNode {
    override fun onAttach() {
        val calculateColor = ColorProducer {
            val userDefinedColor = color()
            if (userDefinedColor.isSpecified) {
                userDefinedColor
            } else {
                currentValueOf(LocalContentColor)
            }
        }
        val calculateRippleAlpha = { rippleAlpha }
        val a = createRippleModifierNode(
            interactionSource,
            bounded,
            radius,
            calculateColor,
            calculateRippleAlpha
        )
        delegate(a)
    }

}

private val CustomAlpha: RippleAlpha = RippleAlpha(
    pressedAlpha = 1f,
    focusedAlpha = 1f,
    draggedAlpha = 1f,
    hoveredAlpha = 1f
)