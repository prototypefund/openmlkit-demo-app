package io.krasch.openread.geometry.types

import kotlin.math.cos
import kotlin.math.sin

data class Point(val x: Double, val y: Double) {
    constructor(x: Int, y: Int) : this(x.toDouble(), y.toDouble())

    fun rotate(angle: Angle): Point {
        // using -angle because our coordinate system has an inverted y axis
        val oppositeAngle = -angle

        val newX = x * cos(oppositeAngle.radian) - y * sin(oppositeAngle.radian)
        val newY = x * sin(oppositeAngle.radian) + y * cos(oppositeAngle.radian)

        return Point(newX, newY)
    }

    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y)
    }

    operator fun minus(other: Point): Point {
        return Point(x - other.x, y - other.y)
    }
}
