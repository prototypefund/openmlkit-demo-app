package io.krasch.openread.geometry.types

import io.krasch.openread.assertAlmostEqual
import org.junit.Test

class TestAngledRectangle {

    @Test
    fun testRectNoRotation() {
        val rect = AngledRectangle(
            bottomLeft = Point(500, 500),
            width = 100.0,
            height = 50.0,
            angleBottom = Angle.fromDegree(0)
        )

        assertAlmostEqual(Point(600, 500), rect.bottomRight, tolerance = 0.001)
        assertAlmostEqual(Point(600, 450), rect.topRight, tolerance = 0.001)
        assertAlmostEqual(Point(500, 450), rect.topLeft, tolerance = 0.001)

        assertAlmostEqual(500.0, rect.left, tolerance = 0.001)
        assertAlmostEqual(450.0, rect.top, tolerance = 0.001)
        assertAlmostEqual(100.0, rect.boxWidth, tolerance = 0.001)
        assertAlmostEqual(50.0, rect.boxHeight, tolerance = 0.001)
    }

    @Test
    fun testSquareRotatedClockwiseOrigin() {
        // works out nicely so that the triangles formed outside the rotated square
        // are always nice 3-4-5 triangles
        val rect = AngledRectangle(
            bottomLeft = Point(0, 0),
            width = 5.0,
            height = 5.0,
            angleBottom = Angle.fromDegree(-36.87)
        )

        // move 4 to right, 3 downwards
        assertAlmostEqual(Point(4, 3), rect.bottomRight, tolerance = 0.001)
        // move 3 right, 4 upwards
        assertAlmostEqual(Point(7, -1), rect.topRight, tolerance = 0.001)
        // move 4 to left, 3 upwards
        assertAlmostEqual(Point(3, -4), rect.topLeft, tolerance = 0.001)

        assertAlmostEqual(0.0, rect.left, tolerance = 0.001)
        assertAlmostEqual(-4.0, rect.top, tolerance = 0.001)
        assertAlmostEqual(7.0, rect.boxWidth, tolerance = 0.001)
        assertAlmostEqual(7.0, rect.boxHeight, tolerance = 0.001)
    }

    @Test
    fun testRectRotatedClockwise() {
        val rect = AngledRectangle(
            bottomLeft = Point(0, 0),
            width = 25.0, // 15-20-25 triangle
            height = 5.0, // 3-4-5 triangle
            angleBottom = Angle.fromDegree(-36.87)
        )

        // move 20 to right, 15 downwards
        assertAlmostEqual(Point(20, 15), rect.bottomRight, tolerance = 0.001)
        // move 3 to right, 4 upwards
        assertAlmostEqual(Point(23, 11), rect.topRight, tolerance = 0.001)
        // move 20 to left, 15 upwards
        assertAlmostEqual(Point(3, -4), rect.topLeft, tolerance = 0.001)

        assertAlmostEqual(0.0, rect.left, tolerance = 0.001)
        assertAlmostEqual(-4.0, rect.top, tolerance = 0.001)
        assertAlmostEqual(23.0, rect.boxWidth, tolerance = 0.001)
        assertAlmostEqual(19.0, rect.boxHeight, tolerance = 0.001)
    }

    @Test
    fun testRectRotatedClockwiseInFirstQuadrant() {
        val rect = AngledRectangle(
            bottomLeft = Point(7, 8),
            width = 25.0,
            height = 5.0,
            angleBottom = Angle.fromDegree(-36.87)
        )

        assertAlmostEqual(Point(7 + 20, 8 + 15), rect.bottomRight, tolerance = 0.001)
        assertAlmostEqual(Point(7 + 23, 8 + 11), rect.topRight, tolerance = 0.001)
        assertAlmostEqual(Point(7 + 3, 8 - 4), rect.topLeft, tolerance = 0.001)

        assertAlmostEqual(7.0, rect.left, tolerance = 0.001)
        assertAlmostEqual(4.0, rect.top, tolerance = 0.001)
        assertAlmostEqual(23.0, rect.boxWidth, tolerance = 0.001)
        assertAlmostEqual(19.0, rect.boxHeight, tolerance = 0.001)
    }

    @Test
    fun testRectRotatedClockwiseInSecondQuadrant() {
        val rect = AngledRectangle(
            bottomLeft = Point(3, -7),
            width = 25.0,
            height = 5.0,
            angleBottom = Angle.fromDegree(-36.87)
        )

        assertAlmostEqual(Point(3 + 20, -7 + 15), rect.bottomRight, tolerance = 0.001)
        assertAlmostEqual(Point(3 + 23, -7 + 11), rect.topRight, tolerance = 0.001)
        assertAlmostEqual(Point(3 + 3, -7 - 4), rect.topLeft, tolerance = 0.001)

        assertAlmostEqual(3.0, rect.left, tolerance = 0.001)
        assertAlmostEqual(-11.0, rect.top, tolerance = 0.001)
        assertAlmostEqual(23.0, rect.boxWidth, tolerance = 0.001)
        assertAlmostEqual(19.0, rect.boxHeight, tolerance = 0.001)
    }

    @Test
    fun testRectRotatedClockwiseInThirdQuadrant() {
        val rect = AngledRectangle(
            bottomLeft = Point(-20, -6),
            width = 25.0,
            height = 5.0,
            angleBottom = Angle.fromDegree(-36.87)
        )

        assertAlmostEqual(Point(-20 + 20, -6 + 15), rect.bottomRight, tolerance = 0.001)
        assertAlmostEqual(Point(-20 + 23, -6 + 11), rect.topRight, tolerance = 0.001)
        assertAlmostEqual(Point(-20 + 3, -6 - 4), rect.topLeft, tolerance = 0.001)

        assertAlmostEqual(-20.0, rect.left, tolerance = 0.001)
        assertAlmostEqual(-10.0, rect.top, tolerance = 0.001)
        assertAlmostEqual(23.0, rect.boxWidth, tolerance = 0.001)
        assertAlmostEqual(19.0, rect.boxHeight, tolerance = 0.001)
    }

    @Test
    fun testRectRotatedClockwiseInFourthQuadrant() {
        val rect = AngledRectangle(
            bottomLeft = Point(-2, 3),
            width = 25.0,
            height = 5.0,
            angleBottom = Angle.fromDegree(-36.87)
        )

        assertAlmostEqual(Point(-2 + 20, 3 + 15), rect.bottomRight, tolerance = 0.001)
        assertAlmostEqual(Point(-2 + 23, 3 + 11), rect.topRight, tolerance = 0.001)
        assertAlmostEqual(Point(-2 + 3, 3 - 4), rect.topLeft, tolerance = 0.001)

        assertAlmostEqual(-2.0, rect.left, tolerance = 0.001)
        assertAlmostEqual(-1.0, rect.top, tolerance = 0.001)
        assertAlmostEqual(23.0, rect.boxWidth, tolerance = 0.001)
        assertAlmostEqual(19.0, rect.boxHeight, tolerance = 0.001)
    }

    @Test
    fun testSquareRotatedCounterClockwiseOrigin() {
        // works out nicely so that the triangles formed outside the rotated square
        // are always nice 3-4-5 triangles
        val rect = AngledRectangle(
            bottomLeft = Point(0, 0),
            width = 5.0,
            height = 5.0,
            angleBottom = Angle.fromDegree(36.87)
        )

        // move 4 to right, 3 upwards
        assertAlmostEqual(Point(4, -3), rect.bottomRight, tolerance = 0.001)
        // move 3 to left, 4 upwards
        assertAlmostEqual(Point(1, -7), rect.topRight, tolerance = 0.001)
        // move 4 to left, 3 downwards
        assertAlmostEqual(Point(-3, -4), rect.topLeft, tolerance = 0.001)

        assertAlmostEqual(-3.0, rect.left, tolerance = 0.001)
        assertAlmostEqual(-7.0, rect.top, tolerance = 0.001)
        assertAlmostEqual(7.0, rect.boxWidth, tolerance = 0.001)
        assertAlmostEqual(7.0, rect.boxHeight, tolerance = 0.001)
    }

    @Test
    fun testRectRotatedCounterClockwiseOrigin() {
        val rect = AngledRectangle(
            bottomLeft = Point(0, 0),
            width = 25.0, // 15-20-25 triangle
            height = 5.0, // 3-4-5 triangle
            angleBottom = Angle.fromDegree(36.87)
        )

        // move 20 right, 15 upwards
        assertAlmostEqual(Point(20, -15), rect.bottomRight, tolerance = 0.001)
        // move 3 left, 4 upwards
        assertAlmostEqual(Point(17, -19), rect.topRight, tolerance = 0.001)
        // move 20 left, 15 downwards
        assertAlmostEqual(Point(-3, -4), rect.topLeft, tolerance = 0.001)

        assertAlmostEqual(-3.0, rect.left, tolerance = 0.001)
        assertAlmostEqual(-19.0, rect.top, tolerance = 0.001)
        assertAlmostEqual(23.0, rect.boxWidth, tolerance = 0.001)
        assertAlmostEqual(19.0, rect.boxHeight, tolerance = 0.001)
    }

    @Test
    fun testRectRotatedCounterClockwiseInFirstQuadrant() {
        val rect = AngledRectangle(
            bottomLeft = Point(10, 8),
            width = 25.0,
            height = 5.0,
            angleBottom = Angle.fromDegree(36.87)
        )

        assertAlmostEqual(Point(10 + 20, 8 - 15), rect.bottomRight, tolerance = 0.001)
        assertAlmostEqual(Point(10 + 17, 8 - 19), rect.topRight, tolerance = 0.001)
        assertAlmostEqual(Point(10 - 3, 8 - 4), rect.topLeft, tolerance = 0.001)

        assertAlmostEqual(7.0, rect.left, tolerance = 0.001)
        assertAlmostEqual(-11.0, rect.top, tolerance = 0.001)
        assertAlmostEqual(23.0, rect.boxWidth, tolerance = 0.001)
        assertAlmostEqual(19.0, rect.boxHeight, tolerance = 0.001)
    }

    @Test
    fun testRectRotatedCounterClockwiseInSecondQuadrant() {
        val rect = AngledRectangle(
            bottomLeft = Point(19, -11),
            width = 25.0,
            height = 5.0,
            angleBottom = Angle.fromDegree(36.87)
        )

        assertAlmostEqual(Point(19 + 20, -11 - 15), rect.bottomRight, tolerance = 0.001)
        assertAlmostEqual(Point(19 + 17, -11 - 19), rect.topRight, tolerance = 0.001)
        assertAlmostEqual(Point(19 - 3, -11 - 4), rect.topLeft, tolerance = 0.001)

        assertAlmostEqual(16.0, rect.left, tolerance = 0.001)
        assertAlmostEqual(-30.0, rect.top, tolerance = 0.001)
        assertAlmostEqual(23.0, rect.boxWidth, tolerance = 0.001)
        assertAlmostEqual(19.0, rect.boxHeight, tolerance = 0.001)
    }

    @Test
    fun testRectRotatedCounterClockwiseInThirdQuadrant() {
        val rect = AngledRectangle(
            bottomLeft = Point(-300, -400),
            width = 25.0,
            height = 5.0,
            angleBottom = Angle.fromDegree(36.87)
        )

        assertAlmostEqual(Point(-300 + 20, -400 - 15), rect.bottomRight, tolerance = 0.001)
        assertAlmostEqual(Point(-300 + 17, -400 - 19), rect.topRight, tolerance = 0.001)
        assertAlmostEqual(Point(-300 - 3, -400 - 4), rect.topLeft, tolerance = 0.001)

        assertAlmostEqual(-303.0, rect.left, tolerance = 0.001)
        assertAlmostEqual(-419.0, rect.top, tolerance = 0.001)
        assertAlmostEqual(23.0, rect.boxWidth, tolerance = 0.001)
        assertAlmostEqual(19.0, rect.boxHeight, tolerance = 0.001)
    }

    @Test
    fun testRectRotatedCounterClockwiseInFourthQuadrant() {
        val rect = AngledRectangle(
            bottomLeft = Point(-6, 40),
            width = 25.0,
            height = 5.0,
            angleBottom = Angle.fromDegree(36.87)
        )

        assertAlmostEqual(Point(-6 + 20, 40 - 15), rect.bottomRight, tolerance = 0.001)
        assertAlmostEqual(Point(-6 + 17, 40 - 19), rect.topRight, tolerance = 0.001)
        assertAlmostEqual(Point(-6 - 3, 40 - 4), rect.topLeft, tolerance = 0.001)

        assertAlmostEqual(-9.0, rect.left, tolerance = 0.001)
        assertAlmostEqual(21.0, rect.top, tolerance = 0.001)
        assertAlmostEqual(23.0, rect.boxWidth, tolerance = 0.001)
        assertAlmostEqual(19.0, rect.boxHeight, tolerance = 0.001)
    }
}
