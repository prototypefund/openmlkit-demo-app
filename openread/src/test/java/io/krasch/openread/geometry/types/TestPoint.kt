package io.krasch.openread.geometry.types

import io.krasch.openread.assertAlmostEqual
import org.junit.Test

/*
   rotation clockwise: angle < 0
   rotation counter clockwise: angle > 0
 */

class TestPoint {
    @Test
    fun testOrigin() {
        val point = Point(0, 0)

        assertAlmostEqual(point, point.rotate(Angle.fromDegree(0)))
        assertAlmostEqual(point, point.rotate(Angle.fromDegree(90)))
        assertAlmostEqual(point, point.rotate(Angle.fromDegree(-17)))
    }

    @Test
    fun testOnlyXVectorClockwise() {
        val point = Point(10, 0)

        assertAlmostEqual(point, point.rotate(Angle.fromDegree(0)))
        assertAlmostEqual(Point(7.0710678118655, 7.0710678118655), point.rotate(Angle.fromDegree(-45)))
        assertAlmostEqual(Point(0, 10), point.rotate(Angle.fromDegree(-90)))
        assertAlmostEqual(Point(-10, 0), point.rotate(Angle.fromDegree(-180)))
        assertAlmostEqual(Point(0, -10), point.rotate(Angle.fromDegree(-270)))
    }

    @Test
    fun testOnlyXVectorCounterClockwise() {
        val point = Point(10, 0)

        assertAlmostEqual(point, point.rotate(Angle.fromDegree(0.0)))
        assertAlmostEqual(Point(7.0710678118655, -7.0710678118655), point.rotate(Angle.fromDegree(45)))
        assertAlmostEqual(Point(0, -10), point.rotate(Angle.fromDegree(90)))
        assertAlmostEqual(Point(-10, 0), point.rotate(Angle.fromDegree(180)))
        assertAlmostEqual(Point(0, 10), point.rotate(Angle.fromDegree(270)))
    }

    @Test
    fun testOnlyYVectorClockwise() {
        val point = Point(0, 10)

        assertAlmostEqual(point, point.rotate(Angle.fromDegree(0)))
        assertAlmostEqual(Point(-7.0710678118655, 7.0710678118655), point.rotate(Angle.fromDegree(-45)))
        assertAlmostEqual(Point(-10, 0), point.rotate(Angle.fromDegree(-90)))
        assertAlmostEqual(Point(0, -10), point.rotate(Angle.fromDegree(-180)))
        assertAlmostEqual(Point(10, 0), point.rotate(Angle.fromDegree(-270)))
    }

    @Test
    fun testOnlyYVectorCounterClockwise() {
        val point = Point(0, 10)

        assertAlmostEqual(point, point.rotate(Angle.fromDegree(0)))
        assertAlmostEqual(Point(7.0710678118655, 7.0710678118655), point.rotate(Angle.fromDegree(45)))
        assertAlmostEqual(Point(10, 0), point.rotate(Angle.fromDegree(90)))
        assertAlmostEqual(Point(0, -10), point.rotate(Angle.fromDegree(180)))
        assertAlmostEqual(Point(-10, 0), point.rotate(Angle.fromDegree(270)))
    }

    @Test
    fun testXandYVectorClockwise() {
        val point = Point(20, 10)

        assertAlmostEqual(point, point.rotate(Angle.fromDegree(0)))
        assertAlmostEqual(Point(7.0710678118655, 21.21320343559642), point.rotate(Angle.fromDegree(-45)))
        assertAlmostEqual(Point(-10, 20), point.rotate(Angle.fromDegree(-90)))
        assertAlmostEqual(Point(-20, -10), point.rotate(Angle.fromDegree(-180)))
        assertAlmostEqual(Point(10, -20), point.rotate(Angle.fromDegree(-270)))
    }

    @Test
    fun testXandYVectorCounterClockwise() {
        val point = Point(20, 10)

        assertAlmostEqual(point, point.rotate(Angle.fromDegree(0)))
        assertAlmostEqual(Point(21.21320343559642, -7.0710678118655,), point.rotate(Angle.fromDegree(45)))
        assertAlmostEqual(Point(10, -20), point.rotate(Angle.fromDegree(90)))
        assertAlmostEqual(Point(-20, -10), point.rotate(Angle.fromDegree(180)))
        assertAlmostEqual(Point(-10, 20), point.rotate(Angle.fromDegree(270)))
    }
}
