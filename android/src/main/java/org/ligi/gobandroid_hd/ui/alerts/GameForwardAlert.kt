/**

 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation;

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see //www.gnu.org/licenses/>.

 */

package org.ligi.gobandroid_hd.ui.alerts

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.logic.markers.TextMarker
import org.ligi.gobandroid_hd.ui.GoPrefs
import org.ligi.gobandroid_hd.ui.GobandroidDialog

/**
 * Dialog to show when user wants to go to next move - handles selection of
 * move-variations

 * @author [Marcus -Ligi- Bueschleb](http://ligi.de)
 * *
 *
 *
 * *         License: This software is licensed with GPLv3
 */
class GameForwardAlert(context: Context, game: GoGame) : GobandroidDialog(context) {

    init {

        setContentView(R.layout.dialog_game_forward)

        val message = findViewById(R.id.message) as TextView
        val buttonContainer = findViewById(R.id.buttonContainer) as ViewGroup
        val showvariationwin = findViewById(R.id.variant_promote_tips) as CheckBox

        // show the comment when there is one - useful for SGF game problems
        val variationCount = game.possibleVariationCount + 1
        if (game.actMove.hasComment()) {
            message.text = game.actMove.comment
        } else {
            message.text = "$variationCount " + context.getString(R.string.variations_found)
        }

        val var_select_listener = View.OnClickListener {
            dismiss()
            if (it.isEnabled) {
                it.isEnabled = false // prevent multi-click
                game.redo(it.tag as Int)
            }

            if (showvariationwin.isChecked) {
                GoPrefs.isShowForwardAlertWanted = false
            }
        }

        for (i in 0..variationCount - 1) {
            val var_btn = Button(context)
            var_btn.tag = i
            var_btn.setOnClickListener(var_select_listener)
            if (game.actMove.getnextMove(i).isMarked) {
                val goMarker = game.actMove.getnextMove(i).goMarker
                if (goMarker is TextMarker) {
                    var_btn.text = goMarker.text
                } else {
                    var_btn.text = (i + 1).toString()
                }
            } else {
                var_btn.text = (i + 1).toString()
            }

            buttonContainer.addView(var_btn)

            var_btn.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        setTitle(R.string.variations)

    }

    companion object {
        fun showIfNeeded(ctx: Context, game: GoGame) {
            if (game.canRedo()) {
                if (game.possibleVariationCount > 0) {
                    GameForwardAlert(ctx, game).show()
                } else {
                    game.redo(0)
                }
            }
        }
    }
}