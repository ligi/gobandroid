package org.ligi.gobandroid_hd.ui;

import org.ligi.gobandroid_beta.R;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.CheckBox;

public class UndoWithVariationDialog extends GobandroidDialog {

	public UndoWithVariationDialog(Context context) {
		super(context);
		setTitle(R.string.keep_variant_);
		setIconResource(R.drawable.help);
		setContentView(R.layout.dialog_keep_variant);
		setIsSmallDialog();

		
		final CheckBox prevent_cb=(CheckBox)findViewById(R.id.keep_variant_session_cb);
		
		class OnYesClick implements OnClickListener {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				getApp().getGame().undo(true);
				if (prevent_cb.isChecked())
					getApp().getInteractionScope().ask_variant_session=false;
				
				dialog.dismiss();
			}

		}
		
		class OnNoClick implements OnClickListener {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				getApp().getGame().undo(false);
				if (prevent_cb.isChecked())
					getApp().getInteractionScope().ask_variant_session=false;
				
				dialog.dismiss();
			}

		}
		
		setPositiveButton(R.string.yes,new OnYesClick());
		setNegativeButton(R.string.no,new OnNoClick());
	}

}
