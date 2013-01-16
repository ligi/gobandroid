package org.ligi.gobandroid_hd.ui;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;

/**
 * A BitmapDrawable without a minimum size
 *
 * @author <a href="http://ligi.de">Marcus -LiGi- Bueschleb </a>
 *         <p/>
 *         This software is licenced with GPLv3
 */
public class BitmapDrawableNoMinimumSize extends BitmapDrawable {

    public BitmapDrawableNoMinimumSize(Resources res, int resId) {
        super(res, ((BitmapDrawable) res.getDrawable(resId)).getBitmap());
    }

    @Override
    public int getMinimumHeight() {
        return 0;
    }

    @Override
    public int getMinimumWidth() {
        return 0;
    }
}