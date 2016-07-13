package org.ligi.gobandroid_hd.base;

import android.content.Context;
import android.content.res.AssetManager;

import org.ligi.axt.AXT;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Fail.fail;

public class GobandroidTestBaseUtil {
    protected static String readAsset(final Context context, final String fileName) {
        try {
            final AssetManager assets = context.getAssets();
            final InputStream inputStream = assets.open(fileName);
            return AXT.at(inputStream).readToString();
        } catch (IOException e) {
            fail("could not read test asset " + fileName + e);
            return null;
        }
    }
}
