package org.ligi.gobandroid_hd.ui;

import org.ligi.gobandroid_hd.ui.sgf_listing.GoLink;
import org.ligi.gobandroid_hd.ui.sgf_listing.SGFSDCardListActivity;
import org.ligi.tracedroid.logging.Log;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class GoLinkLoadActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Uri intent_uri=getIntent().getData(); // extract the uri from the intent
		
		GobandroidNotifications.cancelGoLinkNotification(this);
		
		if (intent_uri==null) {
			Log.e("GoLinkLoadActivity with intent_uri==null");
			finish();
			return;
		}

		GoLink link=new GoLink(intent_uri.toString());

		Intent intent=getIntent();
		intent.setData(Uri.parse(link.getFileName()));

		if (link.linksToDirectory()) {
			intent.setClass(this,SGFSDCardListActivity.class);
		} else {
			// we got some sgf - go to sgfload
			intent.putExtra("move_num",link.getMoveDepth());
			intent.setClass(this,SGFLoadActivity.class);
		}
		
		startActivity(intent);
		finish();
		//new AlertDialog.Builder(this).setTitle("golink" + intent_uri).show();
		/*
		 *
		    if (intent_uri.getLastPathSegment().endsWith(".golink")) {
		    try {
			   		fname=contentToStringFileHelper.file2String(new File(fname));
			        	} catch (IOException e) {
			        		Log.w("problem loading file" + fname.toString());
			        	}
			        }
			        
			        if (fname.contains(":#")) {
			        	String[] arr_content=fname.split(":#");
			        	int move_id=Integer.parseInt(arr_content[1]);
			        	fname=arr_content[0];
			        	intent2start.putExtra("move_num",move_id);
			        }
			        
			        if (!fname.endsWith(".sgf")) {
			        	intent2start=new Intent(this.getActivity(),SGFSDCardListActivity.class);
			        }
			        
			        if (!fname.contains("://"))
			        	fname="file://"+fname;
					
										
		 */
	}

	
}
