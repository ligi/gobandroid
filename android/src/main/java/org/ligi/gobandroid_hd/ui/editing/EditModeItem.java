package org.ligi.gobandroid_hd.ui.editing;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

public class EditModeItem {

    public final int iconResId;
    public final EditGameMode mode;
    public final int contentDescriptionResId;

    public EditModeItem(@DrawableRes int icon_resId, EditGameMode mode, @StringRes int contentDescriptionResId) {
        this.iconResId = icon_resId;
        this.mode = mode;
        this.contentDescriptionResId = contentDescriptionResId;
    }

}