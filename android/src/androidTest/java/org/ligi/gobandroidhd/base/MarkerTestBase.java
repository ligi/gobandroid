package org.ligi.gobandroidhd.base;

import org.ligi.gobandroid_hd.logic.markers.GoMarker;

import java.util.ArrayList;
import java.util.List;

public class MarkerTestBase extends AssetAwareInstrumentationTestCase {

    protected List<GoMarker> markerList(String... markers) {
        final List<GoMarker> result = new ArrayList<>();
        for (String marker : markers) {
            result.add(new GoMarker((byte) 1, (byte) 1, marker));
        }
        return result;
    }

}