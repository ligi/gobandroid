package org.ligi.gobandroid_hd

import org.ligi.gobandroid_hd.logic.CellImpl
import org.ligi.gobandroid_hd.logic.markers.TextMarker
import java.io.File

fun markerList(vararg markers: String) = markers.map { TextMarker(CellImpl(1, 1), it) }
fun markerList(markers: List<String>) = markers.map { TextMarker(CellImpl(1, 1), it) }
fun Any.readAsset(file: String) = File(this.javaClass.classLoader!!.getResource(file).toURI()).bufferedReader().readText()