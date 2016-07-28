package org.ligi.gobandroid_hd.ui.editing.model

import android.support.annotation.StringRes
import org.ligi.gobandroid_hd.logic.markers.GoMarker

class MarkerEditModeItem(val marker: GoMarker?, override val mode: EditGameMode, @StringRes override val contentDescriptionResId: Int) : EditModeItem(mode, contentDescriptionResId)