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
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GnuGoMover;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.links.LinksActivity;
import org.ligi.gobandroid_hd.ui.sgf_listing.SGFSDCardListActivity;
import org.ligi.tracedroid.sending.TraceDroidEmailSender;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

/**
 * This is the main Activity of gobandroid which shows an menu/dashboard 
 * with the stuff you can do here 
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         
**/

public class gobandroid extends GobandroidFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        
    	// if we have stacktraces - give user option to send them
	    TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this);

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
    	getTracker().trackPageView("/record");
    	getApp().getInteractionScope().setMode(InteractionScope.MODE_RECORD);
    	this.startActivity(new Intent(this,GoSetupActivity.class));
   	
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.help:
        	getTracker().trackPageView("/help");
        	this.startActivity( new Intent(this,LinksActivity.class));
    		return true;
    	}
		return super.onOptionsItemSelected(item);
	}

	private Intent startLoad(String path,byte mode) {
    	Intent i=new Intent(this,SGFSDCardListActivity.class);    	
    	i.setData((Uri.parse("file://"+path)));
    	getApp().getInteractionScope().setMode(mode);
    	return i;
    }

    public void solveProblem(View target) {
    	getTracker().trackPageView("/tsumego");
    	
    	Intent next=startLoad(getSettings().getTsumegoPath(),InteractionScope.MODE_TSUMEGO);
    	
    	if (!unzipSGFifNeeded(next)) 
    		startActivity(next);
    }

    public void reviewGame(View target) {
    	getTracker().trackPageView("/review");
    	Intent next=startLoad(getSettings().getReviewPath(),InteractionScope.MODE_REVIEW);
    	if (!unzipSGFifNeeded(next))
    		startActivity(next);
    		
    }

    /**
     * Downloads SGFs and shows a ProgressDialog when needed
     * 
     * @return - weather we had to unzip files
     */
    public boolean unzipSGFifNeeded(Intent intent_after) {
    	String storrage_state=Environment.getExternalStorageState();
    	
    	// we check for the tsumego path as the base path could already be there but no valid tsumego
    	if ((storrage_state.equals(Environment.MEDIA_MOUNTED)&&(!(new File(getSettings().getTsumegoPath())).isDirectory()))) {
    		UnzipSGFsDialog.show(this,intent_after);
    		return true;
    	}
    	return false;
    }
    
    public void startLinks(View target) {
    	getTracker().trackPageView("/links");
    	this.startActivity( new Intent(this,LinksActivity.class));
    }
    
    public void startGnuGoGame(View target) {
    
    	if (!IntentHelper.isServiceAvailable(new Intent(GnuGoMover.intent_action_name),this.getPackageManager(),0)) {
    		getTracker().trackPageView("/gnugo_missing");
    		new AlertDialog.Builder(this)
    			.setMessage(R.string.gnugo_not_installed)
    			.setTitle(R.string.problem)
    			.setNegativeButton(android.R.string.cancel, new DialogDiscarder())
    			.setPositiveButton(R.string.install_gnugo, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							IntentHelper.goToMarketPackage(gobandroid.this, "org.ligi.gobandroidhd.ai.gnugo");
						} catch (Exception e) {
							Intent fail_intent=new Intent();
							fail_intent.setAction(Intent.ACTION_VIEW);
							fail_intent.setData(Uri.parse("http://github.com/downloads/ligi/gobandroid-ai-gnugo/org_ligi_gobandroid_ai_gnugo_0.7.apk"));
							gobandroid.this.startActivity(fail_intent);
						}
					}
    				
    			})    			
    			.show() ;
    		return;
    	}
   
    	getTracker().trackPageView("/gnugo");
    	getApp().getInteractionScope().setMode(InteractionScope.MODE_GNUGO);
    	this.startActivity(new Intent(this,GoSetupActivity.class));
    }
    
    public void startPreferences(View target) {
    	this.startActivity(new Intent(this,GoPrefsActivity.class));
    }
}