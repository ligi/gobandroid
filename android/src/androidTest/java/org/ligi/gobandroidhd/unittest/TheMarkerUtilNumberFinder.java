package org.ligi.gobandroidhd.unittest;

import android.test.suitebuilder.annotation.SmallTest;

import org.ligi.gobandroid_hd.logic.markers.GoMarker;
import org.ligi.gobandroid_hd.logic.markers.MarkerUtil;
import org.ligi.gobandroidhd.base.AssetAwareInstrumentationTestCase;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TheMarkerUtilNumberFinder extends AssetAwareInstrumentationTestCase {

    @SmallTest
    public void testFindFirst() {
        final int firstFreeNumber = MarkerUtil.findFirstFreeNumber(markerList("2", "3"));
        assertThat(firstFreeNumber).isEqualTo(1);
    }


    @SmallTest
    public void testFindLast() {
        final int firstFreeNumber = MarkerUtil.findFirstFreeNumber(markerList("2", "1"));
        assertThat(firstFreeNumber).isEqualTo(3);
    }


    @SmallTest
    public void testFindGap() {
        final int firstFreeNumber = MarkerUtil.findFirstFreeNumber(markerList("1", "3"));
        assertThat(firstFreeNumber).isEqualTo(2);
    }


    @SmallTest
    public void testSurviveLetters() {
        final int firstFreeNumber = MarkerUtil.findFirstFreeNumber(markerList("A", "1"));
        assertThat(firstFreeNumber).isEqualTo(2);
    }

    private List<GoMarker> markerList(String... markers) {
        final List<GoMarker> result = new ArrayList<>();
        for (String marker : markers) {
            result.add(new GoMarker((byte) 1, (byte) 1, marker));
        }
        return result;
    }

}