package io.krasch.openread.geometry.algorithms

import io.krasch.openread.geometry.types.Angle
import io.krasch.openread.geometry.types.AngledRectangle
import io.krasch.openread.geometry.types.Point
import kotlin.math.PI
import kotlin.math.atan2

fun calculateAngle(point1: Point, point2: Point): Angle {
    val diff = point2 - point1

    // minus because of our coordinate system in which y axis is inverted
    val angleRad = -atan2(diff.y, diff.x)
    val angleDeg = angleRad * 180 / PI

    // keep only angles between -90 and 90
    val angleMod = angleDeg % 90

    // 60 degrees clockwise gives same bounding rectangle as rotating 30 degrees counter-clockwise
    // and we are generally only interested in the smallest angle between x axis and the rectangle
    // because we want to do the least amount of rotating to get the text to be horizontal
    return if (angleMod > 45)
        Angle.fromDegree(angleMod - 90)
    else if (angleMod < -45)
        Angle.fromDegree(angleMod + 90)
    else
        Angle.fromDegree(angleMod)
}

fun findEnclosingRectangle(points: List<Point>, angle: Angle): AngledRectangle {
    // rotate by opposite of angle to get rectangle that is parallel to x
    val rotated = points.map { it.rotate(-angle) }

    // find the outmost locations of the parallel rectangle
    val left = rotated.minOf { it.x }
    val right = rotated.maxOf { it.x }
    val top = rotated.minOf { it.y }
    val bottom = rotated.maxOf { it.y }

    val width = right - left
    val height = bottom - top

    // rotate anchor point back to original angle
    val bottomLeft = Point(left, bottom).rotate(angle)

    // all other back-rotations done in constructor of AngledRectangle
    return AngledRectangle(bottomLeft, width, height, angle)
}

fun calculateMinAreaRectangle(hull: List<Point>): AngledRectangle? {
    require(hull.size >= 4) { "Hull must have at least 4 points" }

    // calculate angles between subsequent points on the hull
    // to ensure that also calculating angle between last and first element
    val hull_ = hull + listOf(hull.first())
    val angles = (1 until hull_.size).map { calculateAngle(hull_[it - 1], hull_[it]) }

    // avoid double calculations
    // todo remove duplicate angles for float better
    val anglesDistinct = angles.distinct()

    // calculate enclosing rectangles for all angles observed in the hull
    val rectangles = anglesDistinct
        .map { findEnclosingRectangle(hull, it) }
        .filter { it.width > it.height }

    // return the rectangle with the smallest area
    return rectangles.minByOrNull { it.width * it.height } // todo null check is OK?
}
