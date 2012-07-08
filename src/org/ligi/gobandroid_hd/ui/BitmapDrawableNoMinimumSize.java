package org.ligi.gobandroid_hd.ui;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;

public class BitmapDrawableNoMinimumSize extends BitmapDrawable {

    public BitmapDrawableNoMinimumSize(Resources res, int resId) {
        super(res, ((BitmapDrawable)res.getDrawable(resId)).getBitmap());
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