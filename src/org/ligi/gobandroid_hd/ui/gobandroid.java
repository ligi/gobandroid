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

import java.io.File;

import org.ligi.android.common.dialogs.DialogDiscarder;
import org.ligi.android.common.intents.IntentHelper;
import org.ligi.gobandroid_beta.R;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.etc.GobandroidConfiguration;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.game_setup.GoSetupActivity;
import org.ligi.gobandroid_hd.ui.gnugo.GnuGoHelper;
import org.ligi.gobandroid_hd.ui.links.LinksActivity;
import org.ligi.gobandroid_hd.ui.sgf_listing.SGFSDCardListActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * This is the main Activity of gobandroid which shows an menu/dashboard with
 * the stuff you can do here
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 * 
 **/

public class gobandroid extends GobandroidFragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);

		if (getVersionCode().contains("eta")) // only show in Beta
			setTitle("Gobandroid " + getVersionCode());

		// if we have stacktraces - give user option to send them

		/*
		 * Intent intent = new Intent(Intent.ACTION_PICK,
		 * ContactsContract.Contacts.CONTENT_URI);
		 * 
		 * // Intent intent = new Intent(Intent.ACTION_PICK,
		 * Contacts.People.CONTENT_URI); startActivityForResult(intent, 1);
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getSupportMenuInflater().inflate(R.menu.dashboard, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * the following start* functions are used in the xml via android:onClick
	 **/

	public void recordGame(View target) {
		EasyTracker.getTracker().trackEvent("ui_action", "dashboard", "record", null);
		getApp().getInteractionScope().setMode(InteractionScope.MODE_RECORD);
		this.startActivity(new Intent(this, GoSetupActivity.class));

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.help:
			new HelpDialog(this).show();
			EasyTracker.getTracker().trackEvent("ui_action", "dashboard", "help", null);

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private Intent startLoad(String path) {
		Intent i = new Intent(this, SGFSDCardListActivity.class);
		i.setData((Uri.parse("file://" + path)));
		return i;
	}

	public void solveProblem(View target) {
		EasyTracker.getTracker().trackEvent("ui_action", "dashboard", "tsumego", null);
		Intent next = startLoad(getSettings().getTsumegoPath());

		if (!unzipSGFifNeeded(next))
			startActivity(next);
	}

	public void reviewGame(View target) {
		EasyTracker.getTracker().trackEvent("ui_action", "dashboard", "review", null);
		Intent next = startLoad(getSettings().getReviewPath());
		if (!unzipSGFifNeeded(next))
			startActivity(next);
	}

	/**
	 * Downloads SGFs and shows a ProgressDialog when needed
	 * 
	 * @return - weather we had to unzip files
	 */
	public boolean unzipSGFifNeeded(Intent intent_after) {
		String storrage_state = Environment.getExternalStorageState();

		// we check for the tsumego path as the base path could already be there
		// but
		// no valid tsumego
		if ((storrage_state.equals(Environment.MEDIA_MOUNTED) && (!(new File(getSettings().getTsumegoPath())).isDirectory()))) {
			UnzipSGFsDialog.show(this, intent_after);
			return true;
		}
		return false;
	}

	public void startLinks(View target) {
		EasyTracker.getTracker().trackEvent("ui_action", "dashboard", "links", null);
		this.startActivity(new Intent(this, LinksActivity.class));
	}

	public void startGnuGoGame(View target) {

		if (!GnuGoHelper.isGnuGoAvail(this)) {
			EasyTracker.getTracker().trackEvent("ui_action", "intern", "gnugo_missing", null);
			new AlertDialog.Builder(this).setMessage(R.string.gnugo_not_installed).setTitle(R.string.problem).setNegativeButton(android.R.string.cancel, new DialogDiscarder()).setPositiveButton(R.string.install_gnugo, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					try {
						IntentHelper.goToMarketPackage(gobandroid.this, "org.ligi.gobandroidhd.ai.gnugo");
					} catch (Exception e) {
						Intent fail_intent = new Intent();
						fail_intent.setAction(Intent.ACTION_VIEW);
						fail_intent.setData(Uri.parse(GobandroidConfiguration.GNUGO_MANUAL_INSTALL_URL));
						gobandroid.this.startActivity(fail_intent);
					}
				}

			}).show();
			return;
		}
		EasyTracker.getTracker().trackEvent("ui_action", "dashboard", "gnugo", null);

		getApp().getInteractionScope().setMode(InteractionScope.MODE_GNUGO);
		this.startActivity(new Intent(this, GoSetupActivity.class));
	}

	public void startPreferences(View target) {
		this.startActivity(new Intent(this, GoPrefsActivity.class));
	}

	private String getVersionCode() {
		return getVersionCode(this);
	}

	public static String getVersionCode(Context ctx) {
		try {
			return "v" + ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			return "v?";
		}
	}
}