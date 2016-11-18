package org.ligi.gobandroid_hd.logic.markers.functions

import org.ligi.gobandroid_hd.logic.markers.GoMarker
import org.ligi.gobandroid_hd.logic.markers.TextMarker

fun List<GoMarker>.findFirstFreeNumber(): Int {
    val relevantMarkers = this
            .filterIsInstance<TextMarker>()
            .filter { it.text.matches("[0-9]*".toRegex()) }
            .map { Integer.parseInt(it.text) }
            .toSortedSet()

    return 1.rangeTo(Int.MAX_VALUE).first { it > relevantMarkers.size || it != relevantMarkers.elementAt(it - 1).toInt() }

}

fun List<GoMarker>.findNextLetter(): String {
    val relevantMarkers = this
            .filterIsInstance<TextMarker>()
            .filter { it.text.matches("[A-Z]*".toRegex()) }
            .map { it.text }

    return (0..25).map { it to ('A' + it).toString() }
            .firstOrNull { relevantMarkers.elementAtOrNull(it.first) != it.second }
            ?.second ?: "a"
}