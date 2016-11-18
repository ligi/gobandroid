package org.ligi.gobandroid_hd

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.ligi.gobandroid_hd.logic.markers.util.MarkerUtil

class TheMarkerUtilNumberFinder {

    @Test
    fun testFindFirst() {
        val firstFreeNumber = MarkerUtil.findFirstFreeNumber(markerList("2", "3"))
        assertThat(firstFreeNumber).isEqualTo(1)
    }


    @Test
    fun testFindLast() {
        val firstFreeNumber = MarkerUtil.findFirstFreeNumber(markerList("2", "1"))
        assertThat(firstFreeNumber).isEqualTo(3)
    }


    @Test
    fun testFindGap() {
        val firstFreeNumber = MarkerUtil.findFirstFreeNumber(markerList("1", "3"))
        assertThat(firstFreeNumber).isEqualTo(2)
    }


    @Test
    fun testSurviveLetters() {
        val firstFreeNumber = MarkerUtil.findFirstFreeNumber(markerList("A", "1"))
        assertThat(firstFreeNumber).isEqualTo(2)
    }
}