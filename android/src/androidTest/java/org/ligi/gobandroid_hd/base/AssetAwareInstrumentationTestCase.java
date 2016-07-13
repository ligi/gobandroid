package org.ligi.gobandroid_hd.base;

import android.test.InstrumentationTestCase;

public class AssetAwareInstrumentationTestCase extends InstrumentationTestCase {

    protected String readAsset(final String fileName) {
        return GobandroidTestBaseUtil.readAsset(getInstrumentation().getContext(), fileName);
    }

}