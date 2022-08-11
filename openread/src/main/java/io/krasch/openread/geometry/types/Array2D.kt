package io.krasch.openread.geometry.types

class Array2D<T>(
    private val data: Array<T>,
    val height: Int,
    val width: Int
) {

    init {
        if (width * height != data.size)
            throw IllegalArgumentException("2D array shape does not match array data")
    }

    operator fun get(row: Int, col: Int) = data[get1DIndex(row, col)]

    operator fun set(row: Int, col: Int, value: T) {
        data[get1DIndex(row, col)] = value
    }

    fun isValidCoordinate(row: Int, col: Int): Boolean {
        return (
            (row >= 0) and
                (row < height) and
                (col >= 0) and
                (col < width)
            )
    }

    private fun get1DIndex(row: Int, col: Int): Int {
        if ((col < 0) or (col >= width))
            throw ArrayIndexOutOfBoundsException("width=$width col=$col")

        if ((row < 0) or (row >= height))
            throw ArrayIndexOutOfBoundsException("height=$height row=$row")

        return row * width + col
    }
}
