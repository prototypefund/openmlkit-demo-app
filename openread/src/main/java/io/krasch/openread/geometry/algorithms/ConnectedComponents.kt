package io.krasch.openread.geometry.algorithms

import io.krasch.openread.geometry.types.Array2D

// todo this should probably not be in here
const val THRESHOLD_TEXT_FIRST_PASS = 0.1
const val THRESHOLD_TEXT_SECOND_PASS = 0.7
const val THRESHOLD_LINK = 0.1

private fun getNeighbours(col: Int, row: Int): List<Pair<Int, Int>> {
    return listOf(
        Pair(col - 1, row - 1),
        Pair(col + 0, row - 1),
        Pair(col + 1, row - 1),
        Pair(col - 1, row + 0),
        // not (col + 0, row + 0) because that is the pixel itself!
        Pair(col + 1, row + 0),
        Pair(col - 1, row + 1),
        Pair(col + 0, row + 1),
        Pair(col + 1, row + 1)
    )
}

// todo should only text one array of threshold
fun findConnectedComponents(
    scoreText: Array2D<Float>,
    scoreLink: Array2D<Float>
) = sequence {

    // init 2D boolean array for storing which pixel has been visited  / not visited
    val visited_ = BooleanArray(scoreText.width * scoreText.height) { false }
    val visited = Array2D(visited_.toTypedArray(), scoreText.width, scoreText.height)

    fun isText(col: Int, row: Int): Boolean {
        return (scoreText[col, row] > THRESHOLD_TEXT_FIRST_PASS) or
            (scoreLink[col, row] > THRESHOLD_LINK)
    }

    fun findConnectedNeighbours(startCol: Int, startRow: Int) = sequence {
        // the start pixel itself is part of the component
        yield(Pair(startCol, startRow))

        val neighbours = getNeighbours(startCol, startRow).toMutableList()

        while (neighbours.isNotEmpty()) {
            val (col, row) = neighbours.removeFirst()

            if (!scoreText.isValidCoordinate(col, row))
                continue

            if (visited[col, row])
                continue

            visited[col, row] = true

            if (isText(col, row)) {
                yield(Pair(col, row)) // this neighbour is also part of the component
                neighbours.addAll(getNeighbours(col, row))
            }
        }
    }

    for (col in 0 until scoreText.width) {
        for (row in 0 until scoreText.height) {

            if (visited[col, row])
                continue

            visited[col, row] = true

            if (isText(col, row)) {
                val component = findConnectedNeighbours(col, row).toList()

                if (component.size < 10)
                    continue

                val maxTextScore = (component.map { scoreText[it.first, it.second] }).maxOf { it }
                if (maxTextScore > THRESHOLD_TEXT_SECOND_PASS)
                    yield(component)
            }
        }
    }
}
