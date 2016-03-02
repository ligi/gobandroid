/**
 * gobandroid
 * by Marcus -Ligi- Bueschleb
 * http://ligi.de
 * <p/>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation;
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 **/

package org.ligi.gobandroid_hd.ui.alerts;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.ligi.axt.AXT;
import org.ligi.axt.listeners.DialogDiscardingOnClickListener;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.ui.BaseProfileActivity;
import org.ligi.gobandroid_hd.ui.GobandroidDialog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Class to show an Alert with the Game Info ( who plays / rank / game name .. )
 */
public class GameInfoDialog extends GobandroidDialog {

    @Bind(R.id.game_name_et)
    EditText nameEdit;

    @Bind(R.id.black_name_et)
    EditText blackNameEdit;

    @Bind(R.id.user_is_white_btn)
    Button user_is_white_btn;

    @Bind(R.id.user_is_black_btn)
    Button user_is_black_btn;

    @Bind(R.id.white_name_et)
    EditText white_name_et;

    @Bind(R.id.white_rank_et)
    EditText white_rank_et;

    @Bind(R.id.komi_et)
    EditText game_komi_et;

    @Bind(R.id.game_result_et)
    EditText game_result_et;

    @Bind(R.id.game_difficulty_et)
    EditText game_difficulty_et;

    @Bind(R.id.black_rank_et)
    EditText black_rank_et;

    @Bind(R.id.game_date_et)
    EditText game_date_et;

    @OnClick(R.id.user_is_black_btn)
    void onBlack() {
        if (checkUserNamePresent()) {
            blackNameEdit.setText(settings.getUsername());
            black_rank_et.setText(settings.getRank());
        }
    }

    @OnTextChanged({R.id.black_name_et, R.id.white_name_et})
    void onWhiteTextChanged() {
        updateItsMeButtonVisibility();
    }

    private boolean checkUserNamePresent() {
        if (settings.getUsername().isEmpty()) {

            AXT.at(getContext()).startCommonIntent().activityFromClass(BaseProfileActivity.class);
            return false;
        }
        return true;
    }

    @OnClick(R.id.user_is_white_btn)
    void onWhite() {
        if (checkUserNamePresent()) {
            white_name_et.setText(settings.getUsername());
            white_rank_et.setText(settings.getRank());
        }
    }


    public GameInfoDialog(final Context context, final GoGame game) {
        super(context);
        setTitle(R.string.game_info);
        setIconResource(R.drawable.ic_action_info_outline);
        setContentView(R.layout.game_info);

        ButterKnife.bind(this);

        nameEdit.setText(game.getMetaData().getName());
        blackNameEdit.setText(game.getMetaData().getBlackName());
        black_rank_et.setText(game.getMetaData().getBlackRank());
        white_name_et.setText(game.getMetaData().getWhiteName());
        white_rank_et.setText(game.getMetaData().getWhiteRank());
        game_komi_et.setText("" + game.getKomi());
        game_result_et.setText(game.getMetaData().getResult());
        game_difficulty_et.setText(game.getMetaData().getDifficulty());
        game_date_et.setText(game.getMetaData().getDate());

        updateItsMeButtonVisibility();

        setPositiveButton(android.R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                game.getMetaData().setName(nameEdit.getText().toString());
                game.getMetaData().setBlackName(blackNameEdit.getText().toString());
                game.getMetaData().setBlackRank(black_rank_et.getText().toString());
                game.getMetaData().setWhiteName(white_name_et.getText().toString());
                game.getMetaData().setWhiteRank(white_rank_et.getText().toString());
                game.getMetaData().setDate(game_date_et.getText().toString());

                try {
                    game.setKomi(Float.valueOf(game_komi_et.getText().toString()));
                } catch (NumberFormatException ne) {
                    new AlertDialog.Builder(context).setMessage(R.string.komi_must_be_a_number)
                                                    .setPositiveButton(android.R.string.ok, new DialogDiscardingOnClickListener())
                                                    .setTitle(R.string.problem)
                                                    .show();
                    return;
                }

                game.getMetaData().setResult(game_result_et.getText().toString());
                dialog.dismiss();

            }
        });
    }

    private void updateItsMeButtonVisibility() {
        user_is_white_btn.setVisibility(white_name_et.getText().toString().isEmpty() ? View.VISIBLE : View.GONE);
        user_is_black_btn.setVisibility(blackNameEdit.getText().toString().isEmpty() ? View.VISIBLE : View.GONE);
    }
}