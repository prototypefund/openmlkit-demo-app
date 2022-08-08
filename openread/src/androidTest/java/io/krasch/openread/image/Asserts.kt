package io.krasch.openread.image

import android.graphics.Bitmap
import kotlin.math.abs

fun assertPixelAlmostEqual(expected: Int, actual: Int, x: Int, y: Int) {
    val expectedR = (expected shr 16 and 0xFF) / 255f
    val expectedG = (expected shr 8 and 0xFF) / 255f
    val expectedB = (expected and 0xFF) / 255f

    val actualR = (actual shr 16 and 0xFF) / 255f
    val actualG = (actual shr 8 and 0xFF) / 255f
    val actualB = (actual and 0xFF) / 255f

    val msg = {
        "Difference at pixel [$x, $y]: \n" +
            "expected: (R=$expectedR, G=$expectedG, B=$expectedB)\n" +
            "actual:   (R=$actualR, G=$actualG, B=$actualB)"
    }

    val diffR = abs(expectedR - actualR)
    assert(diffR < 0.01, msg)

    val diffG = abs(expectedG - actualG)
    assert(diffG < 0.01, msg)

    val diffB = abs(expectedB - actualB)
    assert(diffB < 0.01, msg)
}

fun assertAlmostEqual(expected: Bitmap, actual: Bitmap, excludeBorderPixels: Int = 0) {
    assert(expected.width == actual.width)
    assert(expected.height == actual.height)

    for (x in excludeBorderPixels until expected.width - excludeBorderPixels)
        for (y in excludeBorderPixels until expected.height - excludeBorderPixels)
            assertPixelAlmostEqual(expected.getPixel(x, y), actual.getPixel(x, y), x, y)
}
