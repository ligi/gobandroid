/**
 * gobandroid
 * by Marcus -Ligi- Bueschleb
 * http://ligi.de
 *
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation;
 *
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http:></http:>//www.gnu.org/licenses/>.
 */

package org.ligi.gobandroid_hd.ui.alerts

import android.content.Context
import androidx.appcompat.app.AlertDialog
import android.view.View
import kotlinx.android.synthetic.main.dialog_gobandroid.view.*
import kotlinx.android.synthetic.main.game_info.view.*
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.ui.BaseProfileActivity
import org.ligi.gobandroid_hd.ui.GoPrefs
import org.ligi.gobandroid_hd.ui.GobandroidDialog
import org.ligi.kaxt.doAfterEdit
import org.ligi.kaxt.setVisibility
import org.ligi.kaxt.startActivityFromClass

/**
 * Class to show an Alert with the Game Info ( who plays / rank / game name .. )
 */
class GameInfoDialog(context: Context, game: GoGame) : GobandroidDialog(context) {

    private fun checkUserNamePresent(): Boolean {
        if (GoPrefs.username.isEmpty()) {
            context.startActivityFromClass(BaseProfileActivity::class.java)
            return false
        }
        return true
    }

    init {
        setTitle(R.string.game_info)
        setIconResource(R.drawable.ic_action_info_outline)
        setContentView(R.layout.game_info)

        container.dialog_content.black_name_et.doAfterEdit {
            updateItsMeButtonVisibility(container.dialog_content)
        }

        container.white_name_et.doAfterEdit {
            updateItsMeButtonVisibility(container.dialog_content)
        }

        container.dialog_content.user_is_white_btn.setOnClickListener {
            if (checkUserNamePresent()) {
                container.dialog_content.white_name_et.setText(GoPrefs.username)
                container.white_rank_et.setText(GoPrefs.rank)
            }
        }

        container.dialog_content.user_is_black_btn.setOnClickListener {
            if (checkUserNamePresent()) {
                container.dialog_content.black_name_et.setText(GoPrefs.username)
                container.dialog_content.black_rank_et.setText(GoPrefs.rank)
            }
        }

        container.button_komi_seven.setOnClickListener {
            container.komi_et.setText("7.5")
        }

        container.button_komi_six.setOnClickListener {
            container.komi_et.setText("6.5")
        }


        container.button_komi_five.setOnClickListener {
            container.komi_et.setText("5.5")
        }
        container.game_name_et.setText(game.metaData.name)
        container.black_name_et.setText(game.metaData.blackName)
        container.black_rank_et.setText(game.metaData.blackRank)
        container.white_name_et.setText(game.metaData.whiteName)
        container.white_rank_et.setText(game.metaData.whiteRank)
        container.komi_et.setText(game.komi.toString())
        container.game_result_et.setText(game.metaData.result)
        container.game_difficulty_et.setText(game.metaData.difficulty)
        container.game_date_et.setText(game.metaData.date)

        updateItsMeButtonVisibility(container)

        setPositiveButton(android.R.string.ok, { dialog ->
            game.metaData.name = container.game_name_et.text.toString()
            game.metaData.blackName = container.black_name_et.text.toString()
            game.metaData.blackRank = container.black_rank_et.text.toString()
            game.metaData.whiteName = container.white_name_et.text.toString()
            game.metaData.whiteRank = container.white_rank_et.text.toString()
            game.metaData.date = container.game_date_et.text.toString()

            try {
                game.komi = java.lang.Float.valueOf(container.komi_et.text.toString())
            } catch (ne: NumberFormatException) {
                AlertDialog.Builder(context).setMessage(R.string.komi_must_be_a_number)
                        .setPositiveButton(android.R.string.ok, null)
                        .setTitle(R.string.problem)
                        .show()
            }

            game.metaData.result = container.game_result_et.text.toString()
            dialog.dismiss()
        })
    }

    private fun updateItsMeButtonVisibility(content: View) {
        content.user_is_white_btn.setVisibility(content.white_name_et.text.toString().isEmpty())
        content.user_is_black_btn.setVisibility(content.black_name_et.text.toString().isEmpty())
    }
}