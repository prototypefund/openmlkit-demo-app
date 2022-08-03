package io.krasch.openread

import io.krasch.openread.geometry.types.Angle
import io.krasch.openread.geometry.types.AngledRectangle
import io.krasch.openread.geometry.types.Point
import kotlin.math.abs

const val DEFAULT_TOLERANCE = 0.000001

fun assertAlmostEqual(expected: Number, actual: Number, tolerance: Double = DEFAULT_TOLERANCE) {
    val diff = abs(expected.toDouble() - actual.toDouble())
    val errorMessage = { "expected: $expected, actual: $actual" }
    assert(diff < tolerance, errorMessage)
}

fun assertAlmostEqual(expected: Point, actual: Point, tolerance: Double = DEFAULT_TOLERANCE) {
    val diffX = abs(expected.x - actual.x)
    val diffY = abs(expected.y - actual.y)

    val errorMessage = {
        "expected: [${expected.x} ${expected.y}], " +
            "actual: [${actual.x} ${actual.y}]"
    }

    assert(diffX < tolerance, errorMessage)
    assert(diffY < tolerance, errorMessage)
}

fun assertAlmostEqual(
    expected: AngledRectangle,
    actual: AngledRectangle,
    tolerance: Double = DEFAULT_TOLERANCE
) {
    assertAlmostEqual(expected.topLeft, actual.topLeft, tolerance)
    assertAlmostEqual(expected.bottomLeft, actual.bottomLeft, tolerance)
    assertAlmostEqual(expected.bottomRight, actual.bottomRight, tolerance)
    assertAlmostEqual(expected.topRight, actual.topRight, tolerance)
    assertAlmostEqual(expected.left, actual.left, tolerance)
    assertAlmostEqual(expected.top, actual.top, tolerance)
    assertAlmostEqual(expected.width, actual.width, tolerance)
    assertAlmostEqual(expected.height, actual.height, tolerance)
    assertAlmostEqual(expected.boxWidth, actual.boxWidth, tolerance)
    assertAlmostEqual(expected.boxHeight, actual.boxHeight, tolerance)
}

fun assertAlmostEqual(expected: Angle, actual: Angle, tolerance: Double = DEFAULT_TOLERANCE) {
    assertAlmostEqual(expected.degree, actual.degree, tolerance)
}

fun assertEqual(expected: List<Point>, actual: List<Point>) {
    val message = { "Expected: $expected\nActual:   $actual" }
    assert(expected == actual, message)
}
