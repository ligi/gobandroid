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

package org.ligi.gobandroid_hd.ui.review;


import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.GobandroidDialog;
import org.ligi.gobandroid_hd.ui.GobandroidNotifications;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.sgf_listing.GoLink;

import android.content.DialogInterface;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;


/**
 * Dialog to show when user wants to set a BookMark
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 * 
 * License: This software is licensed with GPLv3
 * 
 **/
public class EndReviewDialog extends GobandroidDialog {

	private CheckBox bookmark_notification;
	private CheckBox save_bookmark_cp;
	private EditText bookmark_name;
	
	public EndReviewDialog(final GobandroidFragmentActivity context) {
		super(context);
		
		setContentView(R.layout.end_review_dialog);
		
		setTitle(R.string.end_review);
		setIconResource(R.drawable.help);
		
		save_bookmark_cp=(CheckBox)findViewById(R.id.save_bookmark_cp);
		save_bookmark_cp.setChecked(true);
		
		bookmark_notification=(CheckBox)findViewById(R.id.bookmark_notification_cb);
		bookmark_name=(EditText)findViewById(R.id.bookmark_name_et);
		bookmark_name.setText(BookmarkDialog.getCleanEnsuredFilename(context));
		
		save_bookmark_cp.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				bookmark_notification.setEnabled(isChecked);
				bookmark_name.setEnabled(isChecked);
			}
		});

		class PositiveOnClickListener implements OnClickListener {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				if (save_bookmark_cp.isChecked()) {
					GoLink.saveGameToGoLink(getApp().getGame(), context.getSettings().getBookmarkPath(),bookmark_name.getText().toString()+".golink");
				}
				
				if (bookmark_notification.isChecked()) {
					GobandroidNotifications.addGoLinkNotification(context, context.getSettings().getBookmarkPath()+"/"+bookmark_name.getText().toString()+".golink");
				}

				context.finish();
			}
		}
		
		setPositiveButton(R.string.yes,new PositiveOnClickListener());
		setNegativeButton(R.string.no);
	}

}
