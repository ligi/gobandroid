package org.ligi.gobandroid_hd

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.ligi.gobandroid_hd.logic.markers.util.MarkerUtil

class TheMarkerUtilLetterFinder {

    @Test
    fun testFindFirst() {
        val firstFreeLetter = MarkerUtil.findNextLetter(markerList("C", "B"))
        assertThat(firstFreeLetter).isEqualTo("A")
    }

    @Test
    fun testFindLast() {
        val firstFreeLetter = MarkerUtil.findNextLetter(markerList("A", "B"))
        assertThat(firstFreeLetter).isEqualTo("C")
    }


    @Test
    fun testFindGap() {
        val firstFreeLetter = MarkerUtil.findNextLetter(markerList("A", "C"))
        assertThat(firstFreeLetter).isEqualTo("B")
    }


    @Test
    fun testSurviveNumbers() {
        val firstFreeLetter = MarkerUtil.findNextLetter(markerList("A", "1"))
        assertThat(firstFreeLetter).isEqualTo("B")
    }


    @Test
    fun testFindZ() {
        val firstFreeLetter = MarkerUtil.findNextLetter(markerList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y"))
        assertThat(firstFreeLetter).isEqualTo("Z")
    }
}