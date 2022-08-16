package io.krasch.openread.geometry.algorithms

import io.krasch.openread.geometry.types.Array2D

typealias Coordinate = Pair<Int, Int>
typealias Component = Set<Coordinate>

private fun getNeighbours(row: Int, col: Int): List<Pair<Int, Int>> {
    return listOf(
        Pair(row - 1, col),
        Pair(row + 1, col),
        Pair(row, col - 1),
        Pair(row, col + 1)
        /*Pair(row - 1, col - 1),
        Pair(row + 0, col - 1),
        Pair(row + 1, col - 1),
        Pair(row - 1, col + 0),
        // not (col + 0, row + 0) because that is the pixel itself!
        Pair(row + 1, col + 0),
        Pair(row - 1, col + 1),
        Pair(row + 0, col + 1),
        Pair(row + 1, col + 1)*/
    )
}

fun findConnectedComponents(data: Array2D<Boolean>) = sequence<Component> {

    // init 2D boolean array for storing which pixel has been visited  / not visited
    val visited_ = BooleanArray(data.width * data.height) { false }
    val visited = Array2D(visited_.toTypedArray(), data.height, data.width)

    fun findConnectedNeighbours(startRow: Int, startCol: Int) = sequence<Coordinate> {
        // the start pixel itself is part of the component
        yield(Pair(startRow, startCol))

        // neighbours are not filtered, might contain invalid coordinates!
        val neighbours = getNeighbours(startRow, startCol).toMutableList()

        while (neighbours.isNotEmpty()) {
            val (row, col) = neighbours.removeFirst()

            if (!data.isValidCoordinate(row, col))
                continue

            if (visited[row, col])
                continue

            visited[row, col] = true

            if (data[row, col] == true) {
                yield(Pair(row, col)) // this neighbour is also part of the component
                neighbours.addAll(getNeighbours(row, col))
            }
        }
    }

    for (row in 0 until data.height) {
        for (col in 0 until data.width) {

            if (visited[row, col])
                continue

            visited[row, col] = true

            if (data[row, col])
                yield(findConnectedNeighbours(row, col).toSet())
        }
    }
}
