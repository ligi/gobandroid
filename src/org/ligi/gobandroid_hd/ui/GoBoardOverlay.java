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

package org.ligi.gobandroid_hd.ui;

import java.util.Vector;

import org.ligi.gobandroid_hd.GobandroidApp;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.tracedroid.logging.Log;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

/**
 * overlay for the go-board with comments and nav buttons
 * 
 * @author ligi
 *
 */
public class GoBoardOverlay implements OnClickListener {
	
		private TextView comment_tv;
	    private ScrollView comment_sv;
	    private LinearLayout outer_lin;
		public ImageButton next,back,first,last,comments;
		private LinearLayout button_container;
		private Context context;
		private GoBoardView board_view;
		
	    public GoBoardOverlay(Context context,GoBoardView board_view,boolean horizontal) {
	    	this.context=context;
	    	this.board_view=board_view;
	    	
	    	comment_tv=new TextView(context);
	     	comment_tv.setTextColor(0xCC111111);
	 		comment_tv.setPadding(10, 0, 10, 10);
	 		comment_tv.setText("test");
	 		comment_tv.setBackgroundColor(0x00FF00);
	 		comment_sv=new ScrollView(context);
	 		comment_sv.addView(comment_tv);
	 		
			Vector<ImageButton> control_buttons=new Vector<ImageButton>();
			
			first=new ImageButton(context);
			first.setImageResource(android.R.drawable.ic_media_previous);
			control_buttons.add(first);
			first.setOnClickListener(this);
			
			back=new ImageButton(context);
			back.setImageResource(android.R.drawable.ic_media_rew);
			control_buttons.add(back);
			back.setOnClickListener(this);
			
			comments=new ImageButton(context);
			comments.setImageResource(android.R.drawable.ic_dialog_email);
			control_buttons.add(comments);
			comments.setOnClickListener(this);
						
			next=new ImageButton(context);
			next.setImageResource(android.R.drawable.ic_media_ff);
			control_buttons.add(next);
			next.setOnClickListener(this);
			
			last=new ImageButton(context);
			last.setImageResource(android.R.drawable.ic_media_next);
			last.setOnClickListener(this);
			control_buttons.add(last);

			outer_lin=new LinearLayout(context);

			button_container=new LinearLayout(context);
						
			for (ImageButton btn:control_buttons) {
				btn.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,1));
				button_container.addView(btn);
			}

			if (horizontal) {
				button_container.setOrientation(LinearLayout.VERTICAL);
				button_container.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT));
				
				FrameLayout.LayoutParams bottom_nav_params=new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
					
				bottom_nav_params.gravity=Gravity.RIGHT;
				outer_lin.setOrientation(LinearLayout.HORIZONTAL);
				outer_lin.setLayoutParams(bottom_nav_params);
			}
			else { //vertical layout
				
				button_container.setOrientation(LinearLayout.HORIZONTAL);
				button_container.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
				
				FrameLayout.LayoutParams bottom_nav_params=new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
					
				bottom_nav_params.gravity=Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL;
						
				outer_lin.setLayoutParams(bottom_nav_params);
				outer_lin.setOrientation(LinearLayout.VERTICAL);
			}
			
			comment_sv.setVisibility(View.VISIBLE);	

			outer_lin.addView(comment_sv);
			outer_lin.addView(button_container);

			updateButtonState();
			updateCommentText();
		}
	    

		public GobandroidApp getApp() {
			return (GobandroidApp)context.getApplicationContext();
		}
		
	    private String getGameComment() {
	    	if (getApp().getGame()==null)
				return "";
			else
				return getApp().getGame().getActMove().getComment();
	    }
	    
	    public void updateCommentsSize(int w,int h,boolean horizontal) {

	    	Log.i("refreshing overlay in update to --" + w + "x" + h + " " + (horizontal?"h":"v") + " " + back.getHeight());
	    	if (horizontal) 
	    		comment_tv.setWidth(w-h-20-back.getWidth());
			else
				comment_sv.setLayoutParams(new LinearLayout.LayoutParams(w, h-w-back.getHeight()));

	    	//updateCommentText();
	    	comment_sv.requestLayout();
	    }

	    public void updateCommentText() {
	    	Log.i("Update Comment Text to " + getGameComment()+"_");
	    	
	    	comment_tv.setText(getGameComment());
			comment_tv.requestLayout(); // to make the changed text appear on screen 
	    }
	    
	    
	    public View getView() {
	    	return outer_lin;
	    }
	    
	    @Override
		public void onClick(View btn) {
			final GoGame game=getApp().getGame();
			//GoGame game=GoGameProvider.getGame();
			
			if (btn==back) {
				// dont do it if the mover has to move at the moment
				if (game.getGoMover().isMoversMove())
					return;
				
				game.getGoMover().paused=true;
				game.undo();

				// undo twice if there is a mover
				if (game.canUndo()&&(game.getGoMover().isMoversMove()))
					game.undo();
				
				game.getGoMover().paused=false;
				
			}
			else if (btn==next) {
							
				if (game.getPossibleVariationCount()>0)	{
					LinearLayout lin=new LinearLayout(context);
					LinearLayout li=new LinearLayout(context);

					TextView txt =new TextView(context);
			
					// show the comment when there is one - useful for SGF game problems
					if (game.getActMove().hasComment())
						txt.setText(game.getActMove().getComment());
					else
						txt.setText("" +( game.getPossibleVariationCount()+1) + " Variations found for this move - which should we take?");
				
					txt.setPadding(10, 2, 10, 23);
					lin.addView(txt);
					lin.addView(li);
					lin.setOrientation(LinearLayout.VERTICAL);
					
					final Dialog select_dlg=new Dialog(context);
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
						
							updateButtonState();
							board_view.invalidate();
						}
					};
					
					li.setWeightSum(1.0f*(game.getPossibleVariationCount()+1));
					li.setLayoutParams(new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
					
					for (Integer i=0;i<game.getPossibleVariationCount()+1;i++)
						{
						Button var_btn=new Button(context);
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
			else if (btn==first)
				game.jumpFirst();
			else if (btn==last)
				game.jumpLast();
			else if (btn==comments) {
				
				new AlertDialog.Builder(context).setTitle(R.string.comments)
				.setMessage(getGameComment())
				.setPositiveButton(R.string.ok,  new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {	}
				})
				.setNeutralButton("Edit", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {	
				
					final EditText comment_edit=new EditText(context);
					comment_edit.setText(getGameComment());
					
					new AlertDialog.Builder(context).setTitle(R.string.comments)
					.setView(comment_edit)
					.setPositiveButton(R.string.ok,  new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							game.getActMove().setComment(comment_edit.getText().toString());
							updateCommentText();
						}
					})
					.setNegativeButton(R.string.cancel,  new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {	}
					}).show();
					
					}
				} )
				.show();
			}

			updateCommentText();
			updateButtonState();
			board_view.invalidate();
		}
		
		/**
		 * set the buttons active/inactive depending on the possibility to use them
		 */
		public void updateButtonState() {
			
			GoGame game=getApp().getGame();

			// prevent NPE
			if (game==null)	{
				Log.w("no game there when updateControlsStatus");	
				return;
				}
			
			back.setEnabled(game.canUndo()&&(!game.getGoMover().isMoversMove()));
			first.setEnabled(game.canUndo()&&(!game.getGoMover().isPlayingInThisGame()));
			next.setEnabled(game.canRedo()&&(!game.getGoMover().isPlayingInThisGame()));
			last.setEnabled(game.canRedo()&&(!game.getGoMover().isPlayingInThisGame()));
			//comments.setEnabled(game.getActMove().hasComment());
		}
}