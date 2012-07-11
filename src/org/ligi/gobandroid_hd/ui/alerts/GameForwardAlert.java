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

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * Dialog to show when user wants to go to next move - handles selection of move-variations
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 * 
 * License: This software is licensed with GPLv3
 * 
 **/
public class GameForwardAlert {

	
	public static void show(Context ctx,final GoGame game) {
		if (!game.canRedo())
			return ;
		
		if (game.getPossibleVariationCount()>0)	{
		LinearLayout lin=new LinearLayout(ctx);
		LinearLayout li=new LinearLayout(ctx);

		TextView txt =new TextView(ctx);

		// show the comment when there is one - useful for SGF game problems
		if (game.getActMove().hasComment())
			txt.setText(game.getActMove().getComment());
		else
			txt.setText("" +( game.getPossibleVariationCount()+1) + " Variations found for this move - which should we take?");
	
		txt.setPadding(10, 2, 10, 23);
		lin.addView(txt);
		lin.addView(li);
		lin.setOrientation(LinearLayout.VERTICAL);
		
		final Dialog select_dlg=new Dialog(ctx);
		final Boolean redoing=false;
		View.OnClickListener var_select_listener=new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (redoing)
					return;
				select_dlg.hide();
				if (!v.isEnabled()) return;
				v.setEnabled(false	);
				
				game.redo((Integer)(v.getTag()));
			}
		};
		
		li.setWeightSum(1.0f*(game.getPossibleVariationCount()+1));
		li.setLayoutParams(new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		
		for (Integer i=0;i<game.getPossibleVariationCount()+1;i++)
			{
			Button var_btn=new Button(ctx);
			var_btn.setTag(i);
			var_btn.setOnClickListener(var_select_listener );
			if (game.getActMove().getnextMove(i).isMarked())
				var_btn.setText(game.getActMove().getnextMove(i).getMarkText());
			else
				var_btn.setText(""+(i+1));
				
			li.addView(var_btn);
	
			var_btn.setLayoutParams(new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f));
			}

		select_dlg.setTitle(R.string.variations);
		select_dlg.setContentView(lin);
		
		select_dlg.show();
	}
	else
		game.redo(0);
	}
}