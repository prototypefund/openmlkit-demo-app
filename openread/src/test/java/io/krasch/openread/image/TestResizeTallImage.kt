package io.krasch.openread.image

import org.junit.Assert.assertEquals
import org.junit.Test

class TestResizeTallImageBothDimensionsGrow {
    @Test
    fun bothGrowWithSameRatio() {
        val original = ImageDimensions(width = 10, height = 20)
        val target = ImageDimensions(width = 20, 40)

        val expected = ResizeParameters(ratio = 2.0, padRight = 0, padBottom = 0)

        val actual = calculateResizeRatioParameters(original, target)
        assertEquals(expected, actual)
    }

    @Test
    fun widthGrowsWithLargerRatio_resultStillTall() {
        val original = ImageDimensions(width = 5, height = 10)
        val target = ImageDimensions(width = 15, 20)

        val expected = ResizeParameters(ratio = 2.0, padRight = 5, padBottom = 0)

        val actual = calculateResizeRatioParameters(original, target)
        assertEquals(expected, actual)
    }

    @Test
    fun widthGrowsWithLargerRatio_resultSquare() {
        val original = ImageDimensions(width = 5, height = 10)
        val target = ImageDimensions(width = 20, 20)

        val expected = ResizeParameters(ratio = 2.0, padRight = 10, padBottom = 0)

        val actual = calculateResizeRatioParameters(original, target)
        assertEquals(expected, actual)
    }

    @Test
    fun widthGrowsWithLargerRatio_resultWide() {
        val original = ImageDimensions(width = 5, height = 10)
        val target = ImageDimensions(width = 30, 20)

        val expected = ResizeParameters(ratio = 2.0, padRight = 20, padBottom = 0)

        val actual = calculateResizeRatioParameters(original, target)
        assertEquals(expected, actual)
    }

    @Test
    fun heightGrowsWithLargerRatio() {
        val original = ImageDimensions(width = 5, height = 10)
        val target = ImageDimensions(width = 10, 40)

        val expected = ResizeParameters(ratio = 2.0, padRight = 0, padBottom = 20)

        val actual = calculateResizeRatioParameters(original, target)
        assertEquals(expected, actual)
    }
}

class TestResizeTallImageBothDimensionShrink {
    @Test
    fun bothShrinkWithSameRatio() {
        val original = ImageDimensions(width = 10, height = 20)
        val target = ImageDimensions(width = 5, 10)

        val expected = ResizeParameters(ratio = 0.5, padRight = 0, padBottom = 0)

        val actual = calculateResizeRatioParameters(original, target)
        assertEquals(expected, actual)
    }

    @Test
    fun widthShrinksWithLargerRatio() {
        val original = ImageDimensions(width = 20, height = 40)
        val target = ImageDimensions(width = 5, 30)

        val expected = ResizeParameters(ratio = 0.25, padRight = 0, padBottom = 20)

        val actual = calculateResizeRatioParameters(original, target)
        assertEquals(expected, actual)
    }

    @Test
    fun heightShrinksWithLargerRatio_resultStillTall() {
        val original = ImageDimensions(width = 20, height = 40)
        val target = ImageDimensions(width = 15, 20)

        val expected = ResizeParameters(ratio = 0.5, padRight = 5, padBottom = 0)

        val actual = calculateResizeRatioParameters(original, target)
        assertEquals(expected, actual)
    }

    @Test
    fun heightShrinksWithLargerRatio_resultSquare() {
        val original = ImageDimensions(width = 30, height = 40)
        val target = ImageDimensions(width = 20, 20)

        val expected = ResizeParameters(ratio = 0.5, padRight = 5, padBottom = 0)

        val actual = calculateResizeRatioParameters(original, target)
        assertEquals(expected, actual)
    }

    @Test
    fun heightShrinksWithLargerRatio_resultWide() {
        val original = ImageDimensions(width = 20, height = 40)
        val target = ImageDimensions(width = 15, 10)

        val expected = ResizeParameters(ratio = 0.25, padRight = 10, padBottom = 0)

        val actual = calculateResizeRatioParameters(original, target)
        assertEquals(expected, actual)
    }
}

class TestResizeTallImageWidthGrowsHeightShrinks {
    @Test
    fun resultStillTall() {
        val original = ImageDimensions(width = 20, height = 60)
        val target = ImageDimensions(width = 25, 30)

        val expected = ResizeParameters(ratio = 0.5, padRight = 15, padBottom = 0)

        val actual = calculateResizeRatioParameters(original, target)
        assertEquals(expected, actual)
    }

    @Test
    fun resultSquare() {
        val original = ImageDimensions(width = 20, height = 60)
        val target = ImageDimensions(width = 30, 30)

        val expected = ResizeParameters(ratio = 0.5, padRight = 20, padBottom = 0)

        val actual = calculateResizeRatioParameters(original, target)
        assertEquals(expected, actual)
    }

    @Test
    fun resultWide() {
        val original = ImageDimensions(width = 20, height = 60)
        val target = ImageDimensions(width = 40, 30)

        val expected = ResizeParameters(ratio = 0.5, padRight = 30, padBottom = 0)

        val actual = calculateResizeRatioParameters(original, target)
        assertEquals(expected, actual)
    }
}

class TestResizeTallImageWidthShrinksHeightGrows {
    // result will always be tall, so there is only one configuration to test here
    @Test
    fun test() {
        val original = ImageDimensions(width = 20, height = 40)
        val target = ImageDimensions(width = 10, 60)

        val expected = ResizeParameters(ratio = 0.5, padRight = 0, padBottom = 40)

        val actual = calculateResizeRatioParameters(original, target)
        assertEquals(expected, actual)
    }
}
