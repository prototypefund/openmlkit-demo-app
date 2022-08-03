package io.krasch.openread.geometry.algorithms

import io.krasch.openread.geometry.types.Point
import java.util.LinkedList // todo why am I using a java linkedlist here?
import kotlin.NoSuchElementException

// not putting this directly into Point because this comparison is specific for convex hull algorithm
fun comparePoints(c1: Point, c2: Point): Int {
    return if (c1.x == c2.x)
    // minus because our y-axis is inverted
    // if c1.y > c2.y, then c1 lies below c2
        - (c1.y.compareTo(c2.y))

    else
        c1.x.compareTo(c2.x)
}

fun <T> List<T>.secondToLast(): T {
    if (this.size < 2) {
        throw NoSuchElementException("List only has $this.size elements")
    }
    return this[this.size - 2]
}

fun isClockwiseTurn(p1: Point, p2: Point, p3: Point): Boolean {
    val crossProduct = ((p2 - p1).x) * ((p3 - p1).y) - ((p2 - p1).y) * ((p3 - p1).x)

    // inverted from usual definition because our y-axis is inverted
    // positive -> clockwise (usually would be counter-clockwise)
    // zero -> colinear
    // negative -> counter-clockwise (usually would be clockwise)
    return crossProduct > 0 // todo
}

fun partialHull(sorted: List<Point>): LinkedList<Point> {
    val hull = LinkedList<Point>()

    for (newPoint in sorted) {
        while ((hull.size >= 2) && isClockwiseTurn(hull.secondToLast(), hull.last(), newPoint))
            hull.removeLast()
        hull.add(newPoint)
    }

    return hull
}

// https://en.wikibooks.org/wiki/Algorithm_Implementation/Geometry/Convex_hull/Monotone_chain
fun calculateConvexHull(points: List<Point>): List<Point> {
    // sort Points by x, if same x then sort by y
    val sorted = points.sortedWith { c1, c2 -> comparePoints(c1, c2) }

    val lowerHull = partialHull(sorted)
    val upperHull = partialHull(sorted.reversed())

    // duplicated points
    lowerHull.removeLast()
    upperHull.removeLast()

    return lowerHull + upperHull
}
