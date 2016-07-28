package org.ligi.gobandroid_hd.ui.editing.model

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes

class IconEditModeItem(@DrawableRes val iconResId: Int?, override val mode: EditGameMode, @StringRes override val contentDescriptionResId: Int) : EditModeItem(mode, contentDescriptionResId)