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
import android.widget.ProgressBar;
import android.widget.TextView;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
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
public class GameLoadingDialog extends GobandroidDialog {

    @InjectView(R.id.message)
    public TextView message;

    @InjectView(R.id.progressBar)
    public ProgressBar progress;

    public GameLoadingDialog(final Context context) {
        super(context);

        setContentView(R.layout.dialog_game_load);
        ButterKnife.inject(this);

        setTitle(R.string.loading_sgf);
        setCancelable(false);
    }

}