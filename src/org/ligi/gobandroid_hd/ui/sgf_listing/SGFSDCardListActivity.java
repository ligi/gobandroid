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

package org.ligi.gobandroid_hd.ui.sgf_listing;

import java.io.File;
import org.ligi.android.common.arrays.ArrayHelper;
import java.util.Vector;
import java.util.Arrays;

import org.ligi.android.common.activitys.ActivityFinishOnCancelListener;
import org.ligi.android.common.dialogs.ActivityFinishOnDialogClickListener;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.GobandroidNotifications;
import org.ligi.gobandroid_hd.ui.Refreshable;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.review.SGFMetaData;
import org.ligi.gobandroid_hd.ui.tsumego.fetch.DownloadProblemsDialog;
import org.ligi.tracedroid.logging.Log;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.app.AlertDialog;
import android.os.Bundle;

/**
 * Activity to load SGF's from SD card
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         
 **/

public class SGFSDCardListActivity extends GobandroidFragmentActivity implements Refreshable {

	private String[] menu_items;
	private File[] files;
	private File dir;
	private SGFListFragment list_fragment;
	private String sgf_path;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.getSupportActionBar().setHomeButtonEnabled(true);
		setContentView(R.layout.list);
		sgf_path=getSettings().getSGFBasePath();

		if (getIntent().getBooleanExtra(GobandroidNotifications.BOOL_FROM_NOTIFICATION_EXTRA_KEY,false))
			GobandroidNotifications.cancelNewTsumegosNotification(this);
		
		if (getIntent().getData()!=null)
			sgf_path=getIntent().getData().getPath();

		if (sgf_path.substring(sgf_path.indexOf('/')).startsWith(getSettings().getTsumegoPath().substring(sgf_path.indexOf('/'))))
			this.getApp().getInteractionScope().setMode(InteractionScope.MODE_TSUMEGO);
		if (sgf_path.substring(sgf_path.indexOf('/')).startsWith(getSettings().getReviewPath().substring(sgf_path.indexOf('/'))))
			this.getApp().getInteractionScope().setMode(InteractionScope.MODE_REVIEW);
		dir=new File(sgf_path);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (getApp().getInteractionScope().getMode()==InteractionScope.MODE_TSUMEGO)
			this.getSupportMenuInflater().inflate(R.menu.refresh_tsumego, menu);

		if (getApp().getInteractionScope().getMode()==InteractionScope.MODE_REVIEW)
			this.getSupportMenuInflater().inflate(R.menu.review_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			DownloadProblemsDialog.show(this,(Refreshable)this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		refresh();		
	}
	
	@Override
	public void refresh(){
		
		switch (getApp().getInteractionScope().getMode()) {
			case InteractionScope.MODE_TSUMEGO:
				this.setTitle(R.string.load_tsumego);
				break;
			default:
				// we can only show stuff for tsumego and review - if in doubt - trade as review
				getApp().getInteractionScope().setMode(InteractionScope.MODE_REVIEW);
				// fall wanted
				
			case InteractionScope.MODE_REVIEW:
				this.setTitle(R.string.load_game);
				break;
		}
			
		Log.i("refresh list");
		AlertDialog.Builder alert=new AlertDialog.Builder(this).setTitle(R.string.problem_listing_sgf);

		alert.setPositiveButton(R.string.ok,  new ActivityFinishOnDialogClickListener(this));
		alert.setOnCancelListener(new ActivityFinishOnCancelListener(this));

		if (dir==null) {
			alert.setMessage(getResources().getString(R.string.sgf_path_invalid) +" " +sgf_path).show();
			return;
		}

		files=dir.listFiles();

		if (files==null){
			alert.setMessage(getResources().getString(R.string.there_are_no_files_in) + " " +dir.getAbsolutePath() ).show();
			return;
		}

		Vector<String> fnames=new Vector<String>();
		for(File file:files) 
			if ((file.getName().endsWith(".sgf"))||(file.isDirectory())||(file.getName().endsWith(".golink"))) {
				fnames.add(file.getName());
				Log.i("refresh adding + " +  file.getName());
			}

		if (fnames.size()==0){
			alert.setMessage(getResources().getString(R.string.there_are_no_files_in) + " " +dir.getAbsolutePath() ).show();
			return;
		}

		this.getSupportActionBar().setSubtitle(dir.getAbsolutePath());
		
		
		if (getApp().getInteractionScope().getMode()==InteractionScope.MODE_TSUMEGO) {
			Vector<String> done=new Vector<String>(),undone=new Vector<String>(); 
			for (String fname:fnames)
				if (new SGFMetaData(dir.getAbsolutePath()+"/"+fname).is_solved)
					done.add(fname);
				else
					undone.add(fname);

			String[] undone_arr=(String[])undone.toArray(new String[undone.size()]),done_arr=(String[])done.toArray(new String[done.size()]);
			Arrays.sort(undone_arr);
			Arrays.sort(done_arr);
			menu_items=ArrayHelper.concat(undone_arr,done_arr);
		}
		else {
			menu_items=(String[])fnames.toArray(new String[fnames.size()]);
			Arrays.sort(menu_items);
		}

		list_fragment=new SGFListFragment(menu_items,dir);
		getSupportFragmentManager().beginTransaction().replace(R.id.list_fragment, list_fragment).commit();

	}
}
