package io.krasch.openread.geometry.types

import kotlin.math.PI

data class Angle(val radian: Double) {
    val degree = radianToDegree(radian)

    companion object {
        fun radianToDegree(radian: Double) = radian * 180 / PI
        fun degreeToRadian(degree: Double) = degree * PI / 180
        fun fromRadian(radian: Number) = Angle(radian.toDouble())
        fun fromDegree(degree: Number) = Angle(degreeToRadian(degree.toDouble()))
    }

    operator fun unaryMinus(): Angle {
        return fromDegree(-this.degree)
    }

    operator fun compareTo(other: Angle): Int {
        return this.degree.compareTo(other.degree)
    }

    override fun toString(): String {
        return "$degree°"
    }
}
