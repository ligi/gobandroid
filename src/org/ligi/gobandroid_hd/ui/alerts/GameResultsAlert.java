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
import org.ligi.gobandroid_hd.ui.GOSkin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Class to show an Alert with the Game Result ( who won / points .. )
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 * 
 * License: This software is licensed with GPLv3
 * 
 **/
public class GameResultsAlert {

	public static void show(Context context,GoGame game) {
		ScrollView scrollview=new ScrollView(context);
		LinearLayout lin=new LinearLayout(context);
		
		TableLayout table=new TableLayout(context);
		TableRow row=new TableRow(context);
		
		row.addView(filledTextView(context,"",true,0.0f));
		
		ImageView img=new ImageView(context);
		img.setImageBitmap(GOSkin.getBlackStone(32));
		img.setPadding(0, 0, 20, 0);
		
		row.addView(img);
		
		img=new ImageView(context);
		img.setImageBitmap(GOSkin.getWhiteStone(32));
		row.addView(img);
		
		table.addView(row);
		
		row=new TableRow(context);
		
		float size1=20.0f;
		float size2=23.0f;
		
		row.addView(filledTextView(context,R.string.territory,false,size1));
		row.addView(filledTextView(context,""+game.territory_black,true,size1));
		row.addView(filledTextView(context,""+game.territory_white,true,size1));
		table.addView(row);
		
		row=new TableRow(context);
		row.addView(filledTextView(context,R.string.captures,false,size1));
		row.addView(filledTextView(context,""+game.getCapturesBlack(),true,size1));
		row.addView(filledTextView(context,""+game.getCapturesWhite(),true,size1));
		table.addView(row);
		
		row=new TableRow(context);
		row.addView(filledTextView(context,R.string.komi,false,size1));
		row.addView(filledTextView(context,"0",true,size1));
		row.addView(filledTextView(context,""+game.getKomi(),true,size1));
		
		table.addView(row);
		
		row=new TableRow(context);
		row.addView(filledTextView(context,R.string.filal_points,false,size2));
		row.addView(filledTextView(context,""+game.getPointsBlack(),true,size2));
		row.addView(filledTextView(context,""+game.getPointsWhite(),true,size2));
		table.addView(row);
		
		String game_fin_txt="";
		if (game.getPointsBlack()==game.getPointsWhite())
			 game_fin_txt=context.getResources().getString(R.string.game_ended_in_draw);
					
		if (game.getPointsBlack()>game.getPointsWhite())
			game_fin_txt=(context.getString(R.string.black_won_with) + (game.getPointsBlack()-game.getPointsWhite()) + context.getString(R.string._points_));
					
		if (game.getPointsWhite()>game.getPointsBlack())
			game_fin_txt=(context.getString(R.string.white_won_with_) + (game.getPointsWhite()-game.getPointsBlack()) + context.getString(R.string._points_));
		
		lin.setOrientation(LinearLayout.VERTICAL);	
		lin.addView(table);
		lin.addView(filledTextView(context,game_fin_txt,false,size2));
		scrollview.addView(lin);
		
		new AlertDialog.Builder(context).setTitle(R.string.results).setView(scrollview)
		.setPositiveButton(R.string.ok,  new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) { }
	}).show();
	}
	
	private static TextView filledTextView(Context ctx,String txt,boolean center,float size) {
		TextView res=new TextView(ctx);
		res.setText(txt);
		res.setPadding(3, 0, 10, 0);
		if (center)
				res.setGravity(Gravity.CENTER_HORIZONTAL);
		res.setTextSize(size);
		return res;
	}
	
	private static TextView filledTextView(Context ctx,int txt_id,boolean center,float size) {
		return filledTextView(ctx,ctx.getResources().getString(txt_id),center,size);
	}
}