package org.ligi.gobandroid_hd;

import org.ligi.gobandroid_hd.logic.CellImpl;
import org.ligi.gobandroid_hd.logic.markers.GoMarker;
import org.ligi.gobandroid_hd.logic.markers.TextMarker;

import java.util.ArrayList;
import java.util.List;

public class MarkerTestBase {

    protected List<GoMarker> markerList(String... markers) {
        final List<GoMarker> result = new ArrayList<>();
        for (String marker : markers) {
            result.add(new TextMarker(new CellImpl(1, 1), marker));
        }
        return result;
    }

}