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
import android.support.v7.app.AlertDialog
import android.view.View
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

        val content = findViewById(R.id.dialog_content)

        content.black_name_et.doAfterEdit {
            updateItsMeButtonVisibility(content)
        }

        content.white_name_et.doAfterEdit {
            updateItsMeButtonVisibility(content)
        }

        content.user_is_white_btn.setOnClickListener {
            if (checkUserNamePresent()) {
                content.white_name_et.setText(GoPrefs.username)
                content.white_rank_et.setText(GoPrefs.rank)
            }
        }

        content.user_is_black_btn.setOnClickListener {
            if (checkUserNamePresent()) {
                content.black_name_et.setText(GoPrefs.username)
                content.black_rank_et.setText(GoPrefs.rank)
            }
        }

        content.button_komi_seven.setOnClickListener{
            content.komi_et.setText("7.5")
        }

        content.button_komi_six.setOnClickListener{
            content.komi_et.setText("6.5")
        }


        content.button_komi_five.setOnClickListener{
            content.komi_et.setText("5.5")
        }
        content.game_name_et.setText(game.metaData.name)
        content.black_name_et.setText(game.metaData.blackName)
        content.black_rank_et.setText(game.metaData.blackRank)
        content.white_name_et.setText(game.metaData.whiteName)
        content.white_rank_et.setText(game.metaData.whiteRank)
        content.komi_et.setText("" + game.komi)
        content.game_result_et.setText(game.metaData.result)
        content.game_difficulty_et.setText(game.metaData.difficulty)
        content.game_date_et.setText(game.metaData.date)

        updateItsMeButtonVisibility(content)

        setPositiveButton(android.R.string.ok, { dialog ->
            game.metaData.name = content.game_name_et.text.toString()
            game.metaData.blackName = content.black_name_et.text.toString()
            game.metaData.blackRank = content.black_rank_et.text.toString()
            game.metaData.whiteName = content.white_name_et.text.toString()
            game.metaData.whiteRank = content.white_rank_et.text.toString()
            game.metaData.date = content.game_date_et.text.toString()

            try {
                game.komi = java.lang.Float.valueOf(content.komi_et.text.toString())
            } catch (ne: NumberFormatException) {
                AlertDialog.Builder(context).setMessage(R.string.komi_must_be_a_number)
                        .setPositiveButton(android.R.string.ok, null)
                        .setTitle(R.string.problem)
                        .show()
            }

            game.metaData.result = content.game_result_et.text.toString()
            dialog.dismiss()
        })
    }

    private fun updateItsMeButtonVisibility(content: View) {
        content.user_is_white_btn.setVisibility(content.white_name_et.text.toString().isEmpty())
        content.user_is_black_btn.setVisibility(content.black_name_et.text.toString().isEmpty())
    }
}