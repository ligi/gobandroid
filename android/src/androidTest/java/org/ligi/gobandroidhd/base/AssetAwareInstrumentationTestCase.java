package org.ligi.gobandroidhd.base;

import android.content.res.AssetManager;
import android.test.InstrumentationTestCase;

import org.ligi.axt.AXT;

import java.io.IOException;
import java.io.InputStream;

public class AssetAwareInstrumentationTestCase extends InstrumentationTestCase {

    protected String readAsset(final String fileName) {
        try {
            final AssetManager assets = getInstrumentation().getContext().getAssets();
            final InputStream inputStream = assets.open(fileName);
            return AXT.at(inputStream).readToString();
        } catch (IOException e) {
            fail("could not read test asset " + fileName + e);
            return null;
        }
    }

}