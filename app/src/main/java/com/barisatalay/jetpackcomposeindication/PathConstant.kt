package com.barisatalay.jetpackcomposeindication

import androidx.compose.ui.graphics.Path

object PathConstant {
    fun heart(
        width: Float,
        height: Float,
    ): Path {
        val path = Path()
        path.moveTo(0f, height / 3)

        path.cubicTo(0f, 0f, width / 2, 0f, width / 2, height / 3)
        path.cubicTo(width / 2, 0f, width, 0f, width, height / 3)

        path.cubicTo(
            width,
            height * 2 / 3,
            width / 2,
            height,
            width / 2,
            height
        )
        path.cubicTo(
            0f,
            height * 2 / 3,
            0f,
            height / 2,
            0f,
            height / 3
        )
        return path
    }
}