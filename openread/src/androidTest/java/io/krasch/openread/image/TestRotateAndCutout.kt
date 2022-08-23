package io.krasch.openread.image

import android.graphics.Bitmap
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.krasch.openread.createBitmap
import io.krasch.openread.geometry.types.Angle
import io.krasch.openread.geometry.types.AngledRectangle
import io.krasch.openread.geometry.types.Point
import io.krasch.openread.geometry.types.expandRect
import io.krasch.openread.readBitmap
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestRotateAndCutout {
    private val bitmapClockwise = readBitmap("clockwise.png")
    private val rectClockwise = AngledRectangle(
        bottomLeft = Point(0, 40),
        width = 250.0,
        height = 50.0,
        angleBottom = Angle.fromDegree(-36.87)
    )

    private val bitmapCounterClockwise = readBitmap("counterclockwise.png")
    private val rectCounterClockwise = AngledRectangle(
        bottomLeft = Point(30, 190),
        width = 250.0,
        height = 50.0,
        angleBottom = Angle.fromDegree(36.87)
    )

    @Test
    fun testClockwiseImageSameSizeAsRect() {
        val bitmap = bitmapClockwise
        val rect = rectClockwise

        val actual = rotateAndCutout(bitmap, rect)

        val expected = createBitmap(rect.width, rect.height, Color.BLUE)
        assertAlmostEqual(expected, actual, excludeBorderPixels = 3)
    }

    @Test
    fun testCounterClockwiseImageSameSizeAsRect() {
        val bitmap = bitmapCounterClockwise
        val rect = rectCounterClockwise

        val actual = rotateAndCutout(bitmap, rect)

        val expected = createBitmap(rect.width, rect.height, Color.BLUE)
        assertAlmostEqual(expected, actual, excludeBorderPixels = 3)
    }

    @Test
    fun testClockwisePaddingImageLargerThanRect() {
        val bitmap = bitmapClockwise
        val rect = rectClockwise

        val paddedBitmap = pad(bitmap, left = 10.0, top = 17.0, right = 3.0, bottom = 200.0)
        val adjustedRect = AngledRectangle(
            rect.bottomLeft + Point(10, 17),
            rect.width,
            rect.height,
            rect.angleBottom
        )

        val actual = rotateAndCutout(paddedBitmap, adjustedRect)

        val expected = createBitmap(rect.width, rect.height, Color.BLUE)
        assertAlmostEqual(expected, actual, excludeBorderPixels = 3)
    }

    @Test
    fun testCounterClockwiseImageLargerThanRect() {
        val bitmap = bitmapCounterClockwise
        val rect = rectCounterClockwise

        val paddedBitmap = pad(bitmap, left = 220.0, top = 2.0, right = 900.0, bottom = 2.0)
        val adjustedRect = AngledRectangle(
            rect.bottomLeft + Point(220, 2),
            rect.width,
            rect.height,
            rect.angleBottom
        )

        val actual = rotateAndCutout(paddedBitmap, adjustedRect)

        val expected = createBitmap(rect.width, rect.height, Color.BLUE)
        assertAlmostEqual(expected, actual, excludeBorderPixels = 3)
    }

    @Test
    fun testEnsureNoErrorWhenRectOutsideImageBoundaries() {
        val bitmap = readBitmap("counterclockwiseCut.png")

        // all points of this rectangle lie outside the image boundaries
        // (it still matches the (partial) rectangle in the image)
        val rect = AngledRectangle(
            bottomLeft = Point(-20, 120),
            width = rectCounterClockwise.width,
            height = rectCounterClockwise.height,
            angleBottom = rectCounterClockwise.angleBottom
        )

        val actual = rotateAndCutout(bitmap, rect)

        // not checking the content of the bitmap here because to complicated
        // mainly interested in checking that out-of-bounds rect does not give exception
        // if you want to check the result, use:
        // writeBitmap(actual, "result.jpg")
        assert(actual.width.toDouble() == rect.width)
        assert(actual.height.toDouble() == rect.height)
    }

    @Test
    fun testExpandedRect() {
        val bitmap = bitmapClockwise
        val rect = rectClockwise

        val expandedRect = expandRect(rect, 0.1, 0.1)

        val actual_ = rotateAndCutout(bitmap, expandedRect)
        // blue rectangle should be at this position
        val actual = Bitmap.createBitmap(
            actual_,
            (rect.width * 0.1 / 2).toInt(),
            (rect.height * 0.1 / 2).toInt(),
            rect.width.toInt(),
            rect.height.toInt()
        )

        val expected = createBitmap(rect.width, rect.height, Color.BLUE)
        assertAlmostEqual(expected, actual, excludeBorderPixels = 3)
    }
}
