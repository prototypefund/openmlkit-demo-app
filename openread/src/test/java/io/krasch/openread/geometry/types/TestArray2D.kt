package io.krasch.openread.geometry.types

import org.junit.Assert.assertThrows
import org.junit.Test

class TestArray2D {

    @Test
    fun testQuadraticArray() {
        val values = arrayOf(1, 2, 3, 4)

        // should be
        // [1 2
        //  3 4]
        val array = Array2D(values, 2, 2)

        assert(array[0, 0] == 1)
        assert(array[0, 1] == 2)
        assert(array[1, 0] == 3)
        assert(array[1, 1] == 4)

        assert(array.isValidCoordinate(0, 0))
        assert(array.isValidCoordinate(0, 1))
        assert(array.isValidCoordinate(1, 0))
        assert(array.isValidCoordinate(1, 1))

        // row out of bounds
        assert(!array.isValidCoordinate(-1, 1))
        assert(!array.isValidCoordinate(2, 1))

        // col out of bounds
        assert(!array.isValidCoordinate(1, -1))
        assert(!array.isValidCoordinate(1, 2))

        // row out of bound
        assertThrows(ArrayIndexOutOfBoundsException::class.java) {
            array[-1, 0] = 3
        }
        assertThrows(ArrayIndexOutOfBoundsException::class.java) {
            array[2, 0] = 3
        }

        // col out of bound
        assertThrows(ArrayIndexOutOfBoundsException::class.java) {
            array[1, -1] = 3
        }
        assertThrows(ArrayIndexOutOfBoundsException::class.java) {
            array[1, 2] = 3
        }
    }

    @Test
    fun testTallArray() {
        val values = arrayOf(1, 2, 3, 4, 5, 6)

        // should be
        // [1 2
        //  3 4
        //  5 6]
        val array = Array2D(values, 3, 2)

        assert(array[0, 0] == 1)
        assert(array[0, 1] == 2)
        assert(array[1, 0] == 3)
        assert(array[1, 1] == 4)
        assert(array[2, 0] == 5)
        assert(array[2, 1] == 6)

        assert(array.isValidCoordinate(0, 0))
        assert(array.isValidCoordinate(0, 1))
        assert(array.isValidCoordinate(1, 0))
        assert(array.isValidCoordinate(1, 1))
        assert(array.isValidCoordinate(2, 0))
        assert(array.isValidCoordinate(2, 1))

        // row out of bounds
        assert(!array.isValidCoordinate(-1, 1))
        assert(!array.isValidCoordinate(3, 1))

        // col out of bounds
        assert(!array.isValidCoordinate(1, -1))
        assert(!array.isValidCoordinate(1, 2))

        // row out of bound
        assertThrows(ArrayIndexOutOfBoundsException::class.java) {
            array[-1, 0] = 3
        }
        assertThrows(ArrayIndexOutOfBoundsException::class.java) {
            array[3, 0] = 3
        }

        // col out of bound
        assertThrows(ArrayIndexOutOfBoundsException::class.java) {
            array[1, -1] = 3
        }
        assertThrows(ArrayIndexOutOfBoundsException::class.java) {
            array[1, 2] = 3
        }
    }

    @Test
    fun testWideArray() {
        val values = arrayOf(1, 2, 3, 4, 5, 6)

        // should be
        // [1 2 3
        //  4 5 6]
        val array = Array2D(values, 2, 3)

        assert(array[0, 0] == 1)
        assert(array[0, 1] == 2)
        assert(array[0, 2] == 3)
        assert(array[1, 0] == 4)
        assert(array[1, 1] == 5)
        assert(array[1, 2] == 6)

        assert(array.isValidCoordinate(0, 0))
        assert(array.isValidCoordinate(0, 1))
        assert(array.isValidCoordinate(0, 2))
        assert(array.isValidCoordinate(1, 0))
        assert(array.isValidCoordinate(1, 1))
        assert(array.isValidCoordinate(1, 2))

        // row out of bounds
        assert(!array.isValidCoordinate(-1, 1))
        assert(!array.isValidCoordinate(2, 1))

        // col out of bounds
        assert(!array.isValidCoordinate(1, -1))
        assert(!array.isValidCoordinate(1, 3))

        // row out of bound
        assertThrows(ArrayIndexOutOfBoundsException::class.java) {
            array[-1, 0] = 3
        }
        assertThrows(ArrayIndexOutOfBoundsException::class.java) {
            array[2, 0] = 3
        }

        // col out of bound
        assertThrows(ArrayIndexOutOfBoundsException::class.java) {
            array[1, -1] = 3
        }
        assertThrows(ArrayIndexOutOfBoundsException::class.java) {
            array[1, 3] = 3
        }
    }
}
