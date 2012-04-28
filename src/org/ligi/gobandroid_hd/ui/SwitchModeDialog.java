package org.ligi.gobandroid_hd.ui;

import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.ingame_common.SwitchModeHelper;
import android.content.DialogInterface;

public class SwitchModeDialog extends GobandroidDialog {

	private GobandroidFragmentActivity activity;
	
	private void addModeItem(final byte mode,int string_res,int icon_res) {
		if (mode==getApp().getInteractionScope().getMode())
			return; // already here
		
		addItem(icon_res, string_res,new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				activity.finish();
				SwitchModeHelper.startGame(activity,mode);
			}
			
		});
	}
	
	public SwitchModeDialog(GobandroidFragmentActivity context) {
		super(context);
		activity=context;
		this.setIconResource(R.drawable.mode);
		this.setTitle(R.string.switch_game_mode);
		this.setIsSmallDialog();
	
		addModeItem(InteractionScope.MODE_RECORD,R.string.record,R.drawable.dashboard_record);
		addModeItem(InteractionScope.MODE_REVIEW,R.string.review,R.drawable.dashboard_review);
		addModeItem(InteractionScope.MODE_TELEVIZE,R.string.televize,R.drawable.gobandroid_tv);
		addModeItem(InteractionScope.MODE_TSUMEGO,R.string.tsumego,R.drawable.dashboard_tsumego);
		
		this.setPositiveButton(R.string.cancel);
		
		
	}

}
