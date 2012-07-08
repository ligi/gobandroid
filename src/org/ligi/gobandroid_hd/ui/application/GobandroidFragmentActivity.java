package org.ligi.gobandroid_hd.ui.application;

import org.ligi.gobandroid_hd.GobandroidApp;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.ui.gobandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;

public class GobandroidFragmentActivity extends SherlockFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // we do not want focus on custom views ( mainly for GTV )
        if ((this.getSupportActionBar()!=null) && (this.getSupportActionBar().getCustomView()!=null))
        	this.getSupportActionBar().getCustomView().setFocusable(false);
    }
    
    public boolean doFullScreen() {
    	return false;
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		
    	if (doFullScreen())                
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);                                                                          
        else                                                                                                                                          
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);                                                              
	}

	public GobandroidApp getApp() {
		return (GobandroidApp)getApplicationContext();
	}
	
	public GoGame getGame() {
		return getApp().getGame();
	}

	public GobandroidSettings getSettings() {
		return getApp().getSettings();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; go home
	            Intent intent = new Intent(this, gobandroid.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
	            return true;
	    }
	    return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode==KeyEvent.KEYCODE_WINDOW)
			return false;
		return super.onKeyDown(keyCode, event);
	}
	
	// very nice hint by Jake Wharton by twitter
	@SuppressWarnings("unchecked") 
	public <T> T findById(int id) { return (T) findViewById(id); }

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		EasyTracker.getInstance().activityStop(this); // Add this method.
	}
	@Override
	protected void onStart() {
		super.onStart();
		   
		EasyTracker.getInstance().activityStart(this); // Add this method
}
}
