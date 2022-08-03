package io.krasch.openread.geometry.types

class Array2D<T>(
    private val data: Array<T>,
    val width: Int,
    val height: Int
) {

    init {
        if (width * height != data.size)
            throw IllegalArgumentException("2D array shape does not match array data")
    }

    operator fun get(x: Int, y: Int) = data[get1DIndex(x, y)]

    operator fun set(x: Int, y: Int, value: T) {
        data[get1DIndex(x, y)] = value
    }

    fun isValidCoordinate(x: Int, y: Int): Boolean {
        return (
            (x > 0) and
                (x < width) and
                (y > 0) and
                (y < height)
            )
    }

    private fun get1DIndex(x: Int, y: Int): Int {
        if ((x < 0) or (x >= width))
            throw ArrayIndexOutOfBoundsException("width=$width x=$x")

        if ((y < 0) or (y >= height))
            throw ArrayIndexOutOfBoundsException("height=$height y=$y")

        return y * height + x
    }
}
