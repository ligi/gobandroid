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

package org.ligi.gobandroid_hd.ui.game_setup

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.logic.Cell
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.logic.GoGame.MoveStatus
import org.ligi.gobandroid_hd.logic.GoGame.MoveStatus.VALID
import org.ligi.gobandroid_hd.ui.GoActivity
import org.ligi.gobandroid_hd.ui.recording.GameRecordActivity

/**
 * Activity for setting up a game ( board size / handicap / .. )
 */

class GoSetupActivity : GoActivity() {

    private var clear_board_menu_item: MenuItem? = null

    override val gameExtraFragment by lazy { GameSetupFragment() }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menuInflater.inflate(R.menu.game_setup, menu)
        clear_board_menu_item = menu.findItem(R.id.menu_clear_board)
        clear_board_menu_item!!.isVisible = game.actMove.parent != null
        return super.onCreateOptionsMenu(menu)
    }

    public override fun doMoveWithUIFeedback(cell: Cell?): MoveStatus {
        val res = super.doMoveWithUIFeedback(cell)
        if (res === VALID && game.actMove.hasNextMove()) {
            game.jump(game.actMove.getnextMove(0))
        }

        notifyGoGameChange()
        startActivity(Intent(this, GameRecordActivity::class.java))
        finish()
        return res
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_clear_board -> {
                gameProvider.set(GoGame(gameExtraFragment.act_size, gameExtraFragment.act_handicap))
                clear_board_menu_item!!.isVisible = false
                notifyGoGameChange()
            }
            R.id.menu_start -> {
                startActivity(Intent(this, GameRecordActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}