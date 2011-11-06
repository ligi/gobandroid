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
import org.ligi.gobandroid_hd.R;
import org.ligi.tracedroid.TraceDroid;
import org.ligi.tracedroid.logging.Log;
import org.ligi.tracedroid.sending.TraceDroidEmailSender;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

/**
 * This is the main Activity of gobandroid which shows an menu/dashboard 
 * with the stuff you can do here 
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         
**/

public class gobandroid extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    
        TraceDroid.init(this);
        Log.setTAG("gobandroid");
        TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this);
        GoPrefs.init(this);
        setContentView(R.layout.main_menu);
    }
  
    /****
     * the following start* functions are used in the xml via android:onClick
     */

    public void recordGame(View target) {
    	this.startActivity(new Intent(this,GoSetupActivity.class));
    }
    
    public void solveProblem(View target) {
    	Intent i=new Intent(this,SGFSDCardListActivity.class);
    	i.setData(Uri.parse("file:///sdcard/gobandroid/sgf/problems"));
    		
    	this.startActivity(i);
    }
    
    public void startSettings(View target) {
    	this.startActivity(new Intent(this,GoPrefsActivity.class));
    }
    
    public void startHelp(View target) {
    	this.startActivity( new Intent(this,AboutActivity.class));
    }
}