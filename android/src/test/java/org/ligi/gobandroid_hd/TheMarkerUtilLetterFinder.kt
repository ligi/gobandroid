package org.ligi.gobandroid_hd

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.ligi.gobandroid_hd.logic.markers.functions.findNextLetter

class TheMarkerUtilLetterFinder {

    @Test
    fun testFindFirst() {
        val firstFreeLetter = markerList("C", "B").findNextLetter()
        assertThat(firstFreeLetter).isEqualTo("A")
    }

    @Test
    fun testFindLast() {
        val firstFreeLetter = markerList("A", "B").findNextLetter()
        assertThat(firstFreeLetter).isEqualTo("C")
    }

    @Test
    fun testFindGap() {
        val firstFreeLetter = markerList("A", "C").findNextLetter()
        assertThat(firstFreeLetter).isEqualTo("B")
    }


    @Test
    fun testSurviveNumbers() {
        val firstFreeLetter = markerList("A", "1").findNextLetter()
        assertThat(firstFreeLetter).isEqualTo("B")
    }


    @Test
    fun testFindZ() {
        val firstFreeLetter = markerList(('A'..'Y').toList().map(Char::toString)).findNextLetter()
        assertThat(firstFreeLetter).isEqualTo("Z")
    }


    @Test
    fun testFindAfterZ() {
        val firstFreeLetter = markerList(('A'..'Z').toList().map(Char::toString)).findNextLetter()
        assertThat(firstFreeLetter).isEqualTo("a")
    }
}