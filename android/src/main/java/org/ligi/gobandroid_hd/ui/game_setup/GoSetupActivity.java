/**
 * gobandroid 
 * by Marcus -Ligi- Bueschleb 
 * http://ligi.de
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as 
 * published by the Free Software Foundation; 
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. 
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 **/

package org.ligi.gobandroid_hd.ui.game_setup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.Cell;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.ui.GoActivity;
import org.ligi.gobandroid_hd.ui.recording.GameRecordActivity;

/**
 * Activity for setting up a game ( board size / handicap / .. )
 */

public class GoSetupActivity extends GoActivity {

    private GameSetupFragment setup_fragment;
    private MenuItem clear_board_menu_item;

    @Override
    public Fragment getGameExtraFragment() {
        if (setup_fragment == null)
            setup_fragment = new GameSetupFragment();
        return setup_fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.game_setup, menu);
        clear_board_menu_item = menu.findItem(R.id.menu_clear_board);
        clear_board_menu_item.setVisible(getGame().getActMove().getParent() != null);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public byte doMoveWithUIFeedback(Cell cell) {
        byte res = super.doMoveWithUIFeedback(cell);
        if (res == GoGame.MOVE_VALID)
            if (getGame().getActMove().hasNextMove())
                getGame().jump(getGame().getActMove().getnextMove(0));

        getGame().notifyGameChange();
        startActivity(new Intent(this, GameRecordActivity.class));
        finish();
        return res;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear_board:
                App.setGame(new GoGame(setup_fragment.act_size, setup_fragment.act_handicap));
                clear_board_menu_item.setVisible(false);
                getGame().notifyGameChange();
                break;
            case R.id.menu_start:
                startActivity(new Intent(this, GameRecordActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}