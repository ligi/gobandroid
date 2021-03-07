package org.ligi.gobandroid_hd.ui.editing.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

class IconEditModeItem(@DrawableRes val iconResId: Int?, override val mode: EditGameMode, @StringRes override val contentDescriptionResId: Int) : EditModeItem(mode, contentDescriptionResId)