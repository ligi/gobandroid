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

package org.ligi.gobandroid_hd.ui.alerts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;

import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import org.ligi.axt.listeners.DialogDiscardingOnClickListener;
import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.ui.GobandroidDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Class to show an Alert with the Game Info ( who plays / rank / game name .. )
 */
public class GameInfoDialog extends GobandroidDialog {

    @InjectView(R.id.game_name_et)
    FloatLabeledEditText nameEdit;

    @InjectView(R.id.black_name_et)
    FloatLabeledEditText blackNameEdit;

    @InjectView(R.id.user_is_white_btn)
    Button user_is_white_btn;

    @InjectView(R.id.user_is_black_btn)
    Button user_is_black_btn;

    @InjectView(R.id.white_name_et)
    FloatLabeledEditText white_name_et;

    @InjectView(R.id.white_rank_et)
    FloatLabeledEditText white_rank_et;

    @InjectView(R.id.komi_et)
    FloatLabeledEditText game_komi_et;

    @InjectView(R.id.game_result_et)
    FloatLabeledEditText game_result_et;

    @InjectView(R.id.game_difficulty_et)
    FloatLabeledEditText game_difficulty_et;

    @InjectView(R.id.black_rank_et)
    FloatLabeledEditText black_rank_et;

    @InjectView(R.id.game_date_et)
    FloatLabeledEditText game_date_et;

    public GameInfoDialog(final Context context, final GoGame game) {
        super(context);
        setTitle(R.string.game_info);
        setIconResource(R.drawable.info);
        setContentView(R.layout.game_info);

        final App app = (App) context.getApplicationContext();

        ButterKnife.inject(this);

        nameEdit.setText(game.getMetaData().getName());
        blackNameEdit.setText(game.getMetaData().getBlackName());
        black_rank_et.setText(game.getMetaData().getBlackRank());
        white_name_et.setText(game.getMetaData().getWhiteName());
        white_rank_et.setText(game.getMetaData().getWhiteRank());
        game_komi_et.setText("" + game.getKomi());
        game_result_et.setText(game.getMetaData().getResult());
        game_difficulty_et.setText(game.getMetaData().getDifficulty());
        game_date_et.setText(game.getMetaData().getDate());

        if (app.getSettings().getUsername().isEmpty()) {
            user_is_black_btn.setVisibility(View.GONE);
            user_is_white_btn.setVisibility(View.GONE);
        } else {
            if (!game.getMetaData().getWhiteName().equals("")) {
                user_is_white_btn.setVisibility(View.GONE);
            }

            if (!game.getMetaData().getBlackName().equals("")) {
                user_is_black_btn.setVisibility(View.GONE);
            }

        }

        user_is_black_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                blackNameEdit.setText(app.getSettings().getUsername());
                black_rank_et.setText(app.getSettings().getRank());
                user_is_black_btn.setVisibility(View.GONE);
            }

        });

        user_is_white_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                white_name_et.setText(app.getSettings().getUsername());
                white_rank_et.setText(app.getSettings().getRank());
                user_is_white_btn.setVisibility(View.GONE);
            }

        });


        class SaveChangesOnClick implements DialogInterface.OnClickListener {
            public void onClick(DialogInterface dialog, int whichButton) {
                game.getMetaData().setName(nameEdit.getText().toString());
                game.getMetaData().setBlackName(blackNameEdit.getText().toString());
                game.getMetaData().setBlackRank(black_rank_et.getText().toString());
                game.getMetaData().setWhiteName(white_name_et.getText().toString());
                game.getMetaData().setWhiteRank(white_rank_et.getText().toString());
                game.getMetaData().setDate(game_date_et.getText().toString());

                try {
                    game.setKomi(Float.valueOf(game_komi_et.getText().toString()));
                } catch (NumberFormatException ne) {
                    new AlertDialog.Builder(context).setMessage(R.string.komi_must_be_a_number).setPositiveButton(android.R.string.ok, new DialogDiscardingOnClickListener()).setTitle(R.string.problem).show();
                    return;
                }

                game.getMetaData().setResult(game_result_et.getText().toString());
                dialog.dismiss();
            }
        }

        setPositiveButton(android.R.string.ok, new SaveChangesOnClick());
    }
}