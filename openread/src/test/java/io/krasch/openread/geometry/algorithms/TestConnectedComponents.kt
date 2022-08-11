package io.krasch.openread.geometry.algorithms

import io.krasch.openread.geometry.types.Array2D
import junit.framework.Assert.assertEquals
import org.junit.Test

class TestConnectedComponents {
    @Test
    fun test1x1ArrayNoComponent() {
        val data = Array2D(arrayOf(false), height = 1, width = 1)

        val actual = findConnectedComponents(data).toList()
        assertEquals(0, actual.size)
    }

    @Test
    fun test1x1ArrayOneComponent() {
        val data = Array2D(arrayOf(true), height = 1, width = 1)

        val expected = listOf(setOf(Pair(0, 0)))

        val actual = findConnectedComponents(data).toList()
        assertEquals(expected, actual)
    }

    @Test
    fun test3x2ArrayNoComponent() {
        val data = Array2D(
            arrayOf(
                false, false,
                false, false,
                false, false
            ),
            height = 3, width = 2
        )

        val actual = findConnectedComponents(data).toList()
        assertEquals(0, actual.size)
    }

    @Test
    fun test3x2ArrayOneComponent() {
        val data = Array2D(
            arrayOf(
                true, true,
                true, false,
                true, true
            ),
            height = 3, width = 2
        )

        val component1 = setOf(
            Pair(0, 0), Pair(0, 1),
            Pair(1, 0),
            Pair(2, 0), Pair(2, 1)
        )

        val expected = listOf(component1)

        val actual = findConnectedComponents(data)
        assertEquals(expected.toSet(), actual.toSet())
    }

    @Test
    fun test3x2ArrayTwoComponents() {
        val data = Array2D(
            arrayOf(
                true, true,
                true, false,
                false, true
            ),
            height = 3, width = 2
        )

        val component1 = setOf(
            Pair(0, 0), Pair(0, 1),
            Pair(1, 0)
        )

        val component2 = setOf(
            Pair(2, 1)
        )

        val expected = listOf(component1, component2)

        val actual = findConnectedComponents(data)
        assertEquals(expected.toSet(), actual.toSet())
    }

    @Test
    fun test3x5ArraySeveralComponents() {
        val data = Array2D(
            arrayOf(
                true, true, false, true, false,
                true, false, false, true, false,
                true, false, true, false, true
            ),
            height = 3, width = 5
        )

        val component1 = setOf(
            Pair(0, 0), Pair(0, 1),
            Pair(1, 0),
            Pair(2, 0)
        )

        val component2 = setOf(
            Pair(0, 3),
            Pair(1, 3)
        )

        val component3 = setOf(
            Pair(2, 2)
        )

        val component4 = setOf(
            Pair(2, 4)
        )

        val expected = listOf(component1, component2, component3, component4)

        val actual = findConnectedComponents(data)
        assertEquals(expected.toSet(), actual.toSet())
    }
}
