package io.krasch.openread.geometry.algorithms

import io.krasch.openread.assertAlmostEqual
import io.krasch.openread.geometry.types.Angle
import io.krasch.openread.geometry.types.AngledRectangle
import io.krasch.openread.geometry.types.Point
import org.junit.Test

class TestCalculateAngle {

    private fun runTest(angle: Angle) {
        val rect = AngledRectangle(Point(0, 0), 100.0, 50.0, angle)

        assertAlmostEqual(angle, calculateAngle(rect.bottomLeft, rect.bottomRight), tolerance = 0.001)
        assertAlmostEqual(angle, calculateAngle(rect.bottomRight, rect.topRight), tolerance = 0.001)
        assertAlmostEqual(angle, calculateAngle(rect.topRight, rect.topLeft), tolerance = 0.001)
        assertAlmostEqual(angle, calculateAngle(rect.topLeft, rect.bottomLeft), tolerance = 0.001)

        // reverse order
        assertAlmostEqual(angle, calculateAngle(rect.bottomLeft, rect.topLeft), tolerance = 0.001)
        assertAlmostEqual(angle, calculateAngle(rect.topLeft, rect.topRight), tolerance = 0.001)
        assertAlmostEqual(angle, calculateAngle(rect.topRight, rect.bottomRight), tolerance = 0.001)
        assertAlmostEqual(angle, calculateAngle(rect.bottomRight, rect.bottomLeft), tolerance = 0.001)
    }

    @Test
    fun testNoRotation() {
        runTest(Angle.fromDegree(0))
    }

    @Test
    fun testSmallRotationClockwise() {
        runTest(Angle.fromDegree(-0.7))
    }

    @Test
    fun testMediumRotationClockwise() {
        runTest(Angle.fromDegree(-23))
    }

    @Test
    fun testLargeRotationClockwise() {
        runTest(Angle.fromDegree(-44))
    }

    @Test
    fun testSmallRotationCounterClockwise() {
        runTest(Angle.fromDegree(2))
    }

    @Test
    fun testMediumRotationCounterClockwise() {
        runTest(Angle.fromDegree(31))
    }

    @Test
    fun testLargeRotationCounterClockwise() {
        runTest(Angle.fromDegree(43))
    }
}

class TestFindEnclosingRectangle {
    // corners of a horizontal rectangle
    // we will be rotating this rectangle around a bunch during the tests
    val corners = listOf(
        Point(0, 5), // bottom left
        Point(25, 5), // bottom right
        Point(25, 0), // top right
        Point(0, 0) // top left
    )

    // center points for each of the edges of this rectangle, these form a diamond shape
    // the enclosing rectangle of this diamond is always the rectangle defined above,
    // regardless of rotation angle
    val centers = listOf(
        Point(12.5, 5.0), // between bottom left and bottom right
        Point(25.0, 2.5), // between bottom right and top right
        Point(12.5, 0.0), // between top right and top left
        Point(0.0, 2.5) // between top left and bottom left
    )

    @Test
    fun testCornersNotRotated() {
        val angle = Angle.fromDegree(0)
        val actual = findEnclosingRectangle(corners, angle)

        assertAlmostEqual(angle, actual.angleBottom)
        assertAlmostEqual(corners[0], actual.bottomLeft, tolerance = 0.0001)
        assertAlmostEqual(corners[1], actual.bottomRight, tolerance = 0.0001)
        assertAlmostEqual(corners[2], actual.topRight, tolerance = 0.0001)
        assertAlmostEqual(corners[3], actual.topLeft, tolerance = 0.0001)
    }

    @Test
    fun testCentersNotRotated() {
        val angle = Angle.fromDegree(0)
        val actual = findEnclosingRectangle(centers, angle)

        assertAlmostEqual(angle, actual.angleBottom)
        assertAlmostEqual(corners[0], actual.bottomLeft, tolerance = 0.0001)
        assertAlmostEqual(corners[1], actual.bottomRight, tolerance = 0.0001)
        assertAlmostEqual(corners[2], actual.topRight, tolerance = 0.0001)
        assertAlmostEqual(corners[3], actual.topLeft, tolerance = 0.0001)
    }

    @Test
    fun testCornersRotatedClockwise() {
        val angle = Angle.fromDegree(-36.87)

        val cornersRotated = corners.map { it.rotate(angle) }
        val actual = findEnclosingRectangle(cornersRotated, angle)

        assertAlmostEqual(angle, actual.angleBottom)
        assertAlmostEqual(corners[0].rotate(angle), actual.bottomLeft, tolerance = 0.0001)
        assertAlmostEqual(corners[1].rotate(angle), actual.bottomRight, tolerance = 0.0001)
        assertAlmostEqual(corners[2].rotate(angle), actual.topRight, tolerance = 0.0001)
        assertAlmostEqual(corners[3].rotate(angle), actual.topLeft, tolerance = 0.0001)
    }

    @Test
    fun testCentersRotatedClockwise() {
        val angle = Angle.fromDegree(-36.87)

        val centersRotated = centers.map { it.rotate(angle) }
        val actual = findEnclosingRectangle(centersRotated, angle)

        assertAlmostEqual(angle, actual.angleBottom)
        assertAlmostEqual(corners[0].rotate(angle), actual.bottomLeft, tolerance = 0.0001)
        assertAlmostEqual(corners[1].rotate(angle), actual.bottomRight, tolerance = 0.0001)
        assertAlmostEqual(corners[2].rotate(angle), actual.topRight, tolerance = 0.0001)
        assertAlmostEqual(corners[3].rotate(angle), actual.topLeft, tolerance = 0.0001)
    }

    @Test
    fun testCornersRotatedCounterClockwise() {
        val angle = Angle.fromDegree(36.87)

        val cornersRotated = corners.map { it.rotate(angle) }
        val actual = findEnclosingRectangle(cornersRotated, angle)

        assertAlmostEqual(angle, actual.angleBottom)
        assertAlmostEqual(corners[0].rotate(angle), actual.bottomLeft, tolerance = 0.0001)
        assertAlmostEqual(corners[1].rotate(angle), actual.bottomRight, tolerance = 0.0001)
        assertAlmostEqual(corners[2].rotate(angle), actual.topRight, tolerance = 0.0001)
        assertAlmostEqual(corners[3].rotate(angle), actual.topLeft, tolerance = 0.0001)
    }

    @Test
    fun testCentersRotatedCounterClockwise() {
        val angle = Angle.fromDegree(36.87)

        val centersRotated = centers.map { it.rotate(angle) }
        val actual = findEnclosingRectangle(centersRotated, angle)

        assertAlmostEqual(angle, actual.angleBottom)
        assertAlmostEqual(corners[0].rotate(angle), actual.bottomLeft, tolerance = 0.0001)
        assertAlmostEqual(corners[1].rotate(angle), actual.bottomRight, tolerance = 0.0001)
        assertAlmostEqual(corners[2].rotate(angle), actual.topRight, tolerance = 0.0001)
        assertAlmostEqual(corners[3].rotate(angle), actual.topLeft, tolerance = 0.0001)
    }
}

class TestMinAreaRectangle {
    @Test
    fun testPointsRectangleHorizontal() {
        // points form a rectangle that is horizontal to the x axis
        val hull = listOf(
            Point(0, 5), // bottom left
            Point(25, 5), // bottom right
            Point(25, 0), // top right
            Point(0, 0), // top left
        )

        val expected = AngledRectangle(Point(0, 5), 25.0, 5.0, Angle.fromDegree(0))

        val actual = calculateMinAreaRectangle(hull)!!
        assertAlmostEqual(expected, actual)
    }

    @Test
    fun testPointsRectangleShifted() {
        // points form a rectangle, so there is only one angle in this hull
        val hull = listOf(
            Point(0, 0), // bottom left
            Point(20, 15), // bottom right
            Point(23, 11), // top right
            Point(3, -4), // top left
        )

        val expected = AngledRectangle(Point(0, 0), 25.0, 5.0, Angle.fromDegree(-36.87))

        val actual = calculateMinAreaRectangle(hull)!!
        assertAlmostEqual(expected, actual, 0.001)
    }

    @Test
    fun testPointsParallelogramHorizontal() {
        // points form a parallelogram, so there are two different angles in this hull
        // one of these angles is 0°, because two sides of the parallelogram are parallel to the x axis
        val hull = listOf(
            Point(0, 0), // bottom left
            // need to make sure that this parallelogram is wide enough, so that 0° rect is actually the smallest
            Point(30, 0), // bottom right
            Point(35, -10), // top right
            Point(5, -10), // top left
        )

        val expected = AngledRectangle(Point(0, 0), 35.0, 10.0, Angle.fromDegree(0))

        val actual = calculateMinAreaRectangle(hull)!!
        assertAlmostEqual(expected, actual, 0.001)
    }

    @Test
    fun testPointsParallelogram() {
        val angle = Angle.fromDegree(-10)

        // same parallelogram as above, but now give all points a small rotation
        val hull = listOf(
            Point(0, 0).rotate(angle), // bottom left
            Point(30, 0).rotate(angle), // bottom right
            Point(35, -10).rotate(angle), // top right
            Point(5, -10).rotate(angle), // top left
        )

        val expected = AngledRectangle(Point(0, 0), 35.0, 10.0, angle)

        val actual = calculateMinAreaRectangle(hull)!!
        assertAlmostEqual(expected, actual, 0.001)
    }
}
