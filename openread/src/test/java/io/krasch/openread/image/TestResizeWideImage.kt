package io.krasch.openread.image

import org.junit.Assert.assertEquals
import org.junit.Test

class TestResizeWideImageBothDimensionsGrow {
    @Test
    fun bothGrowWithSameRatio() {
        val original = ImageDimensions(width = 20, height = 10)
        val target = ImageDimensions(width = 40, 20)

        val expected = ResizeParameters(ratio = 2.0, padRight = 0, padBottom = 0)

        val actual = calculateResizeRatioParameters(original, target)
        assertEquals(expected, actual)
    }

    @Test
    fun widthGrowsWithLargerRatio() {
        val original = ImageDimensions(width = 20, height = 10)
        val target = ImageDimensions(width = 40, 15)

        val expected = ResizeParameters(ratio = 1.5, padRight = 10, padBottom = 0)

        val actual = calculateResizeRatioParameters(original, target)
        assertEquals(expected, actual)
    }

    @Test
    fun heightGrowsWithLargerRatio_resultStillWide() {
        val original = ImageDimensions(width = 20, height = 10)
        val target = ImageDimensions(width = 40, 30)

        val expected = ResizeParameters(ratio = 2.0, padRight = 0, padBottom = 10)

        val actual = calculateResizeRatioParameters(original, target)
        assertEquals(expected, actual)
    }

    @Test
    fun heightGrowsWithLargerRatio_resultSquare() {
        val original = ImageDimensions(width = 20, height = 10)
        val target = ImageDimensions(width = 40, 40)

        val expected = ResizeParameters(ratio = 2.0, padRight = 0, padBottom = 20)

        val actual = calculateResizeRatioParameters(original, target)
        assertEquals(expected, actual)
    }

    @Test
    fun heightGrowsWithLargerRatio_resultTall() {
        val original = ImageDimensions(width = 20, height = 10)
        val target = ImageDimensions(width = 40, 50)

        val expected = ResizeParameters(ratio = 2.0, padRight = 0, padBottom = 30)

        val actual = calculateResizeRatioParameters(original, target)
        assertEquals(expected, actual)
    }
}

class TestResizeWideImageBothDimensionsShrink {
    @Test
    fun bothSameRatio() {
        val original = ImageDimensions(width = 20, height = 10)
        val target = ImageDimensions(width = 10, 5)

        val expected = ResizeParameters(ratio = 0.5, padRight = 0, padBottom = 0)

        val actual = calculateResizeRatioParameters(original, target)
        assertEquals(expected, actual)
    }

    @Test
    fun widthShrinksWithLargerRatio_resultStillWide() {
        val original = ImageDimensions(width = 20, height = 10)
        val target = ImageDimensions(width = 10, 8)

        val expected = ResizeParameters(ratio = 0.5, padRight = 0, padBottom = 3)

        val actual = calculateResizeRatioParameters(original, target)
        assertEquals(expected, actual)
    }

    @Test
    fun widthShrinksWithLargerRatio_resultSquare() {
        val original = ImageDimensions(width = 40, height = 20)
        val target = ImageDimensions(width = 10, 10)

        val expected = ResizeParameters(ratio = 0.25, padRight = 0, padBottom = 5)

        val actual = calculateResizeRatioParameters(original, target)
        assertEquals(expected, actual)
    }

    @Test
    fun widthShrinksWithLargerRatio_resultTall() {
        val original = ImageDimensions(width = 40, height = 16)
        val target = ImageDimensions(width = 5, 10)

        val expected = ResizeParameters(ratio = 0.125, padRight = 0, padBottom = 8)

        val actual = calculateResizeRatioParameters(original, target)
        assertEquals(expected, actual)
    }

    @Test
    fun heightShrinksWithLargerRatio() {
        val original = ImageDimensions(width = 40, height = 20)
        val target = ImageDimensions(width = 30, 5)

        val expected = ResizeParameters(ratio = 0.25, padRight = 20, padBottom = 0)

        val actual = calculateResizeRatioParameters(original, target)
        assertEquals(expected, actual)
    }
}

class TestResizeWideImageWidthGrowsHeightShrinks {
    // result will always be wide, so there is only one configuration to test here
    @Test
    fun test() {
        val original = ImageDimensions(width = 20, height = 10)
        val target = ImageDimensions(width = 40, 5)

        val expected = ResizeParameters(ratio = 0.5, padRight = 30, padBottom = 0)

        val actual = calculateResizeRatioParameters(original, target)
        assertEquals(expected, actual)
    }
}

class TestResizeWideImageWidthShrinksHeightGrows {
    @Test
    fun resultStillWide() {
        val original = ImageDimensions(width = 60, height = 20)
        val target = ImageDimensions(width = 30, 25)

        val expected = ResizeParameters(ratio = 0.5, padRight = 0, padBottom = 15)

        val actual = calculateResizeRatioParameters(original, target)
        assertEquals(expected, actual)
    }

    @Test
    fun resultSquare() {
        val original = ImageDimensions(width = 60, height = 20)
        val target = ImageDimensions(width = 30, 30)

        val expected = ResizeParameters(ratio = 0.5, padRight = 0, padBottom = 20)

        val actual = calculateResizeRatioParameters(original, target)
        assertEquals(expected, actual)
    }

    @Test
    fun resultTall() {
        val original = ImageDimensions(width = 60, height = 10)
        val target = ImageDimensions(width = 30, 20)

        val expected = ResizeParameters(ratio = 0.5, padRight = 0, padBottom = 15)

        val actual = calculateResizeRatioParameters(original, target)
        assertEquals(expected, actual)
    }
}
