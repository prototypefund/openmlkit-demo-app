package io.krasch.openread.geometry.algorithms

import io.krasch.openread.assertEqual
import io.krasch.openread.geometry.types.Point
import org.junit.Test

class TestPointComparison {

    // to make tests more readable
    operator fun Point.compareTo(other: Point): Int {
        return comparePoints(this, other)
    }

    @Test
    fun testSameXSameY() {
        val p1 = Point(5, 5)
        val p2 = Point(5, 5)

        assert(p1 == p2)
    }

    @Test
    fun testSameXDifferentYFirstSmaller() {
        val p1 = Point(5, 5)
        val p2 = Point(5, 10)

        assert(p1 > p2)
    }

    @Test
    fun testSameXDifferentYSecondSmaller() {
        val p1 = Point(5, 20)
        val p2 = Point(5, 10)

        assert(p1 <p2)
    }

    @Test
    fun testDifferentXSameYFirstSmaller() {
        val p1 = Point(10, 10)
        val p2 = Point(30, 10)

        assert(p1 < p2)
    }

    @Test
    fun testDifferentXSameYSecondSmaller() {
        val p1 = Point(50, 10)
        val p2 = Point(30, 10)

        assert(p1 > p2)
    }

    @Test
    fun testDifferentXDifferentYFirstSmaller() {
        val p1 = Point(5, 20)
        val p2 = Point(30, 10)

        assert(p1 < p2)
    }

    @Test
    fun testDifferentXDifferentYSecondSmaller() {
        val p1 = Point(50, 20)
        val p2 = Point(30, 100)

        assert(p1> p2)
    }
}

class TestIsClockwiseTurn {
    @Test
    fun testAllPointsOnOneLineHorizontalRightwards() {

        val isClockwise = isClockwiseTurn(
            Point(0, 0),
            Point(5, 0),
            Point(10, 0)
        )

        assert(!isClockwise)
    }

    @Test
    fun testAllPointsOnOneLineHorizontalLeftwards() {

        val isClockwise = isClockwiseTurn(
            Point(10, 0),
            Point(5, 0),
            Point(0, 0)
        )

        assert(!isClockwise)
    }

    @Test
    fun testAllPointsOnOneLineVerticalUpwards() {

        val isClockwise = isClockwiseTurn(
            Point(10, 0),
            Point(10, -5),
            Point(10, -10)
        )

        assert(!isClockwise)
    }

    @Test
    fun testAllPointsOnOneLineVerticalDownwards() {

        val isClockwise = isClockwiseTurn(
            Point(10, 0),
            Point(10, 5),
            Point(10, 10)
        )

        assert(!isClockwise)
    }

    @Test
    fun testAntiClockwiseUpwards() {

        val isClockwise = isClockwiseTurn(
            Point(0, 0),
            Point(10, 0),
            Point(10, -10)
        )

        assert(!isClockwise)
    }

    @Test
    fun testAntiClockwiseDownwards() {

        val isClockwise = isClockwiseTurn(
            Point(0, 0),
            Point(-10, 0),
            Point(-10, 10)
        )

        assert(!isClockwise)
    }

    @Test
    fun testClockwiseUpwards() {

        val isClockwise = isClockwiseTurn(
            Point(0, 0),
            Point(0, -10),
            Point(10, -10)
        )

        assert(isClockwise)
    }

    @Test
    fun testClockwiseDownwards() {

        val isClockwise = isClockwiseTurn(
            Point(0, 0),
            Point(0, 10),
            Point(-10, 10)
        )

        assert(isClockwise)
    }
}

class TestLowerHull {
    @Test
    fun testTriangle() {
        val points = listOf(
            Point(0, 0),
            Point(5, 10),
            Point(10, 0),
        )

        val expected = listOf(
            Point(0, 0),
            Point(5, 10),
            Point(10, 0),
        )

        val actual = partialHull(points)
        assertEqual(expected, actual)
    }

    @Test
    fun testTriangleWithExtraPointIrrelevantForHull() {
        val points = listOf(
            Point(0, 0),
            Point(5, 10),
            Point(5, 5),
            Point(10, 0),
        )

        val expected = listOf(
            Point(0, 0),
            Point(5, 10),
            Point(10, 0),
        )

        val actual = partialHull(points)
        assertEqual(expected, actual)
    }
}

class TestUpperHull {
    @Test
    fun testTriangle() {
        // must be defined from right to left!
        val points = listOf(
            Point(10, 0),
            Point(5, -10),
            Point(0, 0),
        )

        val expected = listOf(
            Point(10, 0),
            Point(5, -10),
            Point(0, 0),
        )

        val actual = partialHull(points)
        assertEqual(expected, actual)
    }

    @Test
    fun testTriangleWithExtraPointIrrelevantForHull() {
        val points = listOf(
            Point(10, 0),
            Point(5, -5),
            Point(5, -10),
            Point(0, 0),
        )

        val expected = listOf(
            Point(10, 0),
            Point(5, -10),
            Point(0, 0),
        )

        val actual = partialHull(points)
        assertEqual(expected, actual)
    }
}

class TestConvexHull {
    @Test
    fun testDiamond() {
        val points = listOf(
            Point(0, 0),
            Point(5, 5),
            Point(10, 0),
            Point(5, -5)
        )

        val expected = listOf(
            Point(0, 0),
            Point(5, 5),
            Point(10, 0),
            Point(5, -5)
        )

        val actual = calculateConvexHull(points)
        assertEqual(expected, actual)
    }

    @Test
    fun testDiamondUnsorted() {
        val points = listOf(
            Point(10, 0),
            Point(5, -5),
            Point(5, 5),
            Point(0, 0),
        )

        val expected = listOf(
            Point(0, 0),
            Point(5, 5),
            Point(10, 0),
            Point(5, -5)
        )

        val actual = calculateConvexHull(points)
        assertEqual(expected, actual)
    }

    @Test
    fun testDiamondUnsortedWithExtraPointsIrrelevantForHull() {
        val points = listOf(
            Point(2, -1),
            Point(10, 0),
            Point(5, -5),
            Point(5, 5),
            Point(0, 0),
            Point(5, 0),
        )

        val expected = listOf(
            Point(0, 0),
            Point(5, 5),
            Point(10, 0),
            Point(5, -5)
        )

        val actual = calculateConvexHull(points)
        assertEqual(expected, actual)
    }

    @Test
    fun testDiamondUnsortedWithExtraNub() {
        val points = listOf(
            Point(10, 0),
            Point(10, -10), // upper right corner sticks out
            Point(5, -5),
            Point(5, 5),
            Point(0, 0),
        )

        val expected = listOf(
            Point(0, 0),
            Point(5, 5),
            Point(10, 0),
            Point(10, -10),
            Point(5, -5)
        )

        val actual = calculateConvexHull(points)
        assertEqual(expected, actual)
    }
}
