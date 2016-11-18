package org.ligi.gobandroid_hd

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.ligi.gobandroid_hd.logic.markers.functions.findFirstFreeNumber


class TheMarkerUtilNumberFinder {

    @Test
    fun testFindFirst() {
        val firstFreeNumber = markerList("2", "3").findFirstFreeNumber()
        assertThat(firstFreeNumber).isEqualTo(1)
    }


    @Test
    fun testFindLast() {
        val firstFreeNumber = markerList("2", "1").findFirstFreeNumber()
        assertThat(firstFreeNumber).isEqualTo(3)
    }


    @Test
    fun testFindGap() {
        val firstFreeNumber = markerList("1", "3").findFirstFreeNumber()
        assertThat(firstFreeNumber).isEqualTo(2)
    }


    @Test
    fun testSurviveLetters() {
        val firstFreeNumber = markerList("A", "1").findFirstFreeNumber()
        assertThat(firstFreeNumber).isEqualTo(2)
    }
}