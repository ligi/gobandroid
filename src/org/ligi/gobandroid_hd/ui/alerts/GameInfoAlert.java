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

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.ui.GobandroidDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
/**
 * Class to show an Alert with the Game Info ( who plays / rank / game name .. )
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 * 
 * License: This software is licensed with GPLv3
 * 
 **/
public class GameInfoAlert extends GobandroidDialog {

	public GameInfoAlert(Context context,final GoGame game) {
		super(context);
		setTitle(R.string.game_info);
		setIconResource(R.drawable.info);
		setContentView(R.layout.game_info);

		final EditText game_name_et=(EditText)findViewById(R.id.game_name_et);
		game_name_et.setText(game.getMetaData().getName());
		
		final EditText black_name_et=(EditText)findViewById(R.id.black_name_et);
		black_name_et.setText(game.getMetaData().getBlackName());

		final EditText black_rank_et=(EditText)findViewById(R.id.black_rank_et);
		black_rank_et.setText(game.getMetaData().getBlackRank());

		final EditText white_name_et=(EditText)findViewById(R.id.white_name_et);
		white_name_et.setText(game.getMetaData().getWhiteName());

		final EditText white_rank_et=(EditText)findViewById(R.id.white_rank_et);
		white_rank_et.setText(game.getMetaData().getWhiteRank());

		final EditText game_komi_et=(EditText)findViewById(R.id.komi_et);
		game_komi_et.setText(""+game.getKomi());
		
		final EditText game_result_et=(EditText)findViewById(R.id.game_result_et);
		game_result_et.setText(game.getMetaData().getResult());
		
		class SaveChangesOnClick implements DialogInterface.OnClickListener {
			public void onClick(DialogInterface dialog, int whichButton) {
				game.getMetaData().setName(game_name_et.getText().toString());
				game.getMetaData().setBlackName(black_name_et.getText().toString());
				game.getMetaData().setBlackRank(black_rank_et.getText().toString());
				game.getMetaData().setWhiteName(white_name_et.getText().toString());
				game.getMetaData().setWhiteRank(white_rank_et.getText().toString());
				game.setKomi(new Float(game_komi_et.getText().toString()));
				game.getMetaData().setResult(game_result_et.getText().toString());
				dialog.dismiss();
			}
		}
		
		setPositiveButton(android.R.string.ok,new SaveChangesOnClick());
	}
}