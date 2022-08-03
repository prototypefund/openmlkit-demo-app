package io.krasch.openread.geometry.types

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class AngledRectangle(
    val bottomLeft: Point,
    val width: Double,
    val height: Double,
    val angleBottom: Angle
) {

    init {
        require(abs(angleBottom.degree) <= 45) { // todo
            "Only angles between [-45°, 45°] allowed."
        }
    }

    // corner points if rectangle was rotated such that it is horizontal to x axs
    // -height because y axis is inverted in our coordinate system
    private val _unrotatedBottomLeft = bottomLeft.rotate(-angleBottom)
    private val _unrotatedBottomRight = _unrotatedBottomLeft + Point(width, 0.0)
    private val _unrotatedTopRight = _unrotatedBottomLeft + Point(width, -height)
    private val _unrotatedTopLeft = _unrotatedBottomLeft + Point(0.0, -height)

    // actual corner points of the rotated rectangle
    val bottomRight = _unrotatedBottomRight.rotate(angleBottom)
    val topRight = _unrotatedTopRight.rotate(angleBottom)
    val topLeft = _unrotatedTopLeft.rotate(angleBottom)

    val left = min(topLeft.x, bottomLeft.x)
    val top = min(topLeft.y, topRight.y)
    val right = max(topRight.x, bottomRight.x)
    val bottom = max(bottomLeft.y, bottomRight.y)

    val boxWidth = right - left
    val boxHeight = bottom - top

    /*
    fun contains(x: Double, y: Double): Boolean {
        val point = Point(x, y).rotate(-angleBottom)

        // todo can do nicer...
        val left = min(_unrotatedTopLeft.x, _unrotatedBottomLeft.x)
        val top = min(_unrotatedTopLeft.y, _unrotatedTopRight.y)
        val right = max(_unrotatedTopRight.x, _unrotatedBottomRight.x)
        val bottom = max(_unrotatedBottomLeft.y, _unrotatedBottomRight.y)

        return ((point.x >= left) &&
                (point.x <= right) &&
                (point.y >= top) &&
                (point.y <= bottom))
    }*/
}
