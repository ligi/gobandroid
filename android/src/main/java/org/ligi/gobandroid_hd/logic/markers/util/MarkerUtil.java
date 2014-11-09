package org.ligi.gobandroid_hd.logic.markers.util;

import org.ligi.gobandroid_hd.logic.markers.GoMarker;
import org.ligi.gobandroid_hd.logic.markers.TextMarker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MarkerUtil {

    public static int findFirstFreeNumber(final List<GoMarker> markers) {
        final List<Integer> presentNumberList = new ArrayList<>();

        for (GoMarker marker : markers) {
            if (marker instanceof TextMarker) {
                final String markerText = ((TextMarker)marker).getText();
                if (markerText.matches("[0-9]*")) {
                    presentNumberList.add(Integer.parseInt(markerText));
                }
            }
        }

        Collections.sort(presentNumberList);

        int probe = 1; // initial offset - perhaps an options to set it to 0 would be nice for IT players - but 1 is more common

        for (Integer act : presentNumberList) {
            if (act > probe) {
                return probe;
            } else if (act == probe) {
                probe++;
            }
        }
        return probe;
    }


    public static String findNextLetter(final List<GoMarker> markers) {
        for (int i = 0; i < 26; i++) {
            boolean found = false;
            for (GoMarker marker : markers) {
                if (marker instanceof TextMarker) {
                    found |= ((TextMarker)marker).getText().equals("" + (char) ('A' + i));
                }
            }

            if (!found) {
                return "" + (char) ('A' + i);
            }
        }
        return "a";
    }

}
