package org.ligi.gobandroidhd;

import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Test;
import org.ligi.gobandroid_hd.logic.Cell;
import org.ligi.gobandroid_hd.logic.markers.GoMarker;
import org.ligi.gobandroid_hd.logic.markers.TextMarker;
import org.ligi.gobandroid_hd.logic.markers.util.MarkerUtil;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TheMarkerUtilNumberFinder {

    @Test
    public void testFindFirst() {
        final int firstFreeNumber = MarkerUtil.findFirstFreeNumber(markerList("2", "3"));
        assertThat(firstFreeNumber).isEqualTo(1);
    }


    @Test
    public void testFindLast() {
        final int firstFreeNumber = MarkerUtil.findFirstFreeNumber(markerList("2", "1"));
        assertThat(firstFreeNumber).isEqualTo(3);
    }


    @Test
    public void testFindGap() {
        final int firstFreeNumber = MarkerUtil.findFirstFreeNumber(markerList("1", "3"));
        assertThat(firstFreeNumber).isEqualTo(2);
    }


    @Test
    public void testSurviveLetters() {
        final int firstFreeNumber = MarkerUtil.findFirstFreeNumber(markerList("A", "1"));
        assertThat(firstFreeNumber).isEqualTo(2);
    }

    private List<GoMarker> markerList(String... markers) {
        final List<GoMarker> result = new ArrayList<>();
        for (String marker : markers) {
            result.add(new TextMarker(new Cell(1, 1), marker));
        }
        return result;
    }

}