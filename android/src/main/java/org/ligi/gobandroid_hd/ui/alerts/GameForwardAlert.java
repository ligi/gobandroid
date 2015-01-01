/**
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

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.markers.GoMarker;
import org.ligi.gobandroid_hd.logic.markers.TextMarker;
import org.ligi.gobandroid_hd.ui.GobandroidDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Dialog to show when user wants to go to next move - handles selection of
 * move-variations
 *
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         <p/>
 *         License: This software is licensed with GPLv3
 */
public class GameForwardAlert extends GobandroidDialog {

    final GoGame game;

    @InjectView(R.id.message)
    TextView message;

    @InjectView(R.id.buttonContainer)
    ViewGroup buttonContainer;

    public GameForwardAlert(final Context context, final GoGame game) {
        super(context);
        this.game = game;

        setContentView(R.layout.dialog_game_forward);
        ButterKnife.inject(this);

        // show the comment when there is one - useful for SGF game problems
        if (game.getActMove().hasComment()) {
            message.setText(game.getActMove().getComment());
        } else {
            message.setText("" + (game.getPossibleVariationCount() + 1) + " Variations found for this move - which should we take?");
        }

        final View.OnClickListener var_select_listener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                dismiss();

                if (!view.isEnabled()) {
                    return;
                }
                view.setEnabled(false);

                game.redo((Integer) (view.getTag()));
            }
        };


        for (Integer i = 0; i < game.getPossibleVariationCount() + 1; i++) {
            final Button var_btn = new Button(context);
            var_btn.setTag(i);
            var_btn.setOnClickListener(var_select_listener);
            if (game.getActMove().getnextMove(i).isMarked()) {
                final GoMarker goMarker = game.getActMove().getnextMove(i).getGoMarker().get();
                if (goMarker instanceof TextMarker) {
                    var_btn.setText(((TextMarker) goMarker).getText());
                }
            } else {
                var_btn.setText(String.valueOf(i + 1));
            }

            buttonContainer.addView(var_btn);

            var_btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        }

        setTitle(R.string.variations);

    }


    public static void showIfNeeded(final Context ctx, final GoGame game) {
        if (!game.canRedo()) {
            return;
        }

        if (game.getPossibleVariationCount() > 0) {
            new GameForwardAlert(ctx, game).show();
        } else {
            game.redo(0);
        }
    }
}