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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
/**
 * Class to show an Alert with the Game Info ( who plays / rank / game name .. )
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 * 
 * License: This software is licensed with GPLv3
 * 
 **/
public class GameInfoAlert {

	public static void show(Context ctx,final GoGame game) {
		ScrollView scroll_view=new ScrollView(ctx);
		TableLayout table_gameinfo=new TableLayout(ctx);
		TableRow row_gameinfo=new TableRow(ctx);
		
		table_gameinfo.setColumnStretchable(1, true);
		
		final EditText game_name_et=new EditText(ctx);
		game_name_et.setText(game.getMetaData().getName());
		game_name_et.setPadding(2, 0, 5, 0);
		TextView game_name_tv=new TextView(ctx);
		game_name_tv.setText("Game Name");
		
		row_gameinfo.addView(game_name_tv);
		row_gameinfo.addView(game_name_et);
		
		table_gameinfo.addView(row_gameinfo);

		TableRow row_black_name=new TableRow(ctx);
		final EditText black_name_et=new EditText(ctx);
		black_name_et.setText(game.getMetaData().getBlackName());
		black_name_et.setPadding(2, 0, 5, 0);
		TextView black_name_tv=new TextView(ctx);
		black_name_tv.setPadding(2, 0, 5, 0);
		black_name_tv.setText("Black Name");
		
		row_black_name.addView(black_name_tv);
		row_black_name.addView(black_name_et);
		
		table_gameinfo.addView(row_black_name);

		TableRow row_black_rank=new TableRow(ctx);
		final EditText black_rank_et=new EditText(ctx);
		black_rank_et.setText(game.getMetaData().getBlackRank());
		black_rank_et.setPadding(2, 0, 5, 0);
		TextView black_rank_tv=new TextView(ctx);
		black_rank_tv.setPadding(2, 0, 5, 0);
		black_rank_tv.setText("Black Rank");
		
		row_black_rank.addView(black_rank_tv);
		row_black_rank.addView(black_rank_et);
		
		table_gameinfo.addView(row_black_rank);

		TableRow row_white_name=new TableRow(ctx);
		final  EditText white_name_et=new EditText(ctx);
		white_name_et.setText(game.getMetaData().getWhiteName());
		white_name_et.setPadding(2, 0, 5, 0);
		TextView white_name_tv=new TextView(ctx);
		white_name_tv.setPadding(2, 0, 5, 0);
		white_name_tv.setText("White Name");
		
		row_white_name.addView(white_name_tv);
		row_white_name.addView(white_name_et);
		
		table_gameinfo.addView(row_white_name);

		TableRow row_white_rank=new TableRow(ctx);
		final EditText white_rank_et=new EditText(ctx);
		white_rank_et.setText(game.getMetaData().getWhiteRank());
		white_rank_et.setPadding(2, 0, 5, 0);
		TextView white_rank_tv=new TextView(ctx);
		white_rank_tv.setPadding(2, 0, 5, 0);
		white_rank_tv.setText("White Rank");
		
		row_white_rank.addView(white_rank_tv);
		row_white_rank.addView(white_rank_et);
		
		table_gameinfo.addView(row_white_rank);

		TableRow game_komi_row=new TableRow(ctx);
		final EditText game_komi_et=new EditText(ctx);
		game_komi_et.setText(Float.toString(game.getKomi()));
		game_komi_et.setPadding(2, 0, 5, 0);
		TextView game_komi_tv=new TextView(ctx);
		game_komi_tv.setPadding(2, 0, 5, 0);
		game_komi_tv.setText("Komi");
		
		game_komi_row.addView(game_komi_tv);
		game_komi_row.addView(game_komi_et);
		
		table_gameinfo.addView(game_komi_row);
		
		TableRow game_result_row=new TableRow(ctx);
		final EditText game_result_et=new EditText(ctx);
		game_result_et.setText(game.getMetaData().getResult());
		game_result_et.setPadding(2, 0, 5, 0);
		TextView game_result_tv=new TextView(ctx);
		game_result_tv.setPadding(2, 0, 5, 0);
		game_result_tv.setText("Game Result");
		
		game_result_row.addView(game_result_tv);
		game_result_row.addView(game_result_et);
		
		table_gameinfo.addView(game_result_row);
		
		scroll_view.addView(table_gameinfo);
		
		new AlertDialog.Builder(ctx).setTitle("Game Info").setView(scroll_view)
		.setMessage("").setPositiveButton(R.string.ok,  new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			game.getMetaData().setName(game_name_et.getText().toString());
			game.getMetaData().setBlackName(black_name_et.getText().toString());
			game.getMetaData().setBlackRank(black_rank_et.getText().toString());
			game.getMetaData().setWhiteName(white_name_et.getText().toString());
			game.getMetaData().setWhiteRank(white_rank_et.getText().toString());
			game.setKomi(new Float(game_komi_et.getText().toString()));
			game.getMetaData().setResult(game_result_et.getText().toString());
		}
		}).show();
	}
}