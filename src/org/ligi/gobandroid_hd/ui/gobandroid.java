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
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.links.LinksActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
    }
  
    /**
     * the following start* functions are used in the xml via android:onClick
     **/

    public void recordGame(View target) {
    	this.startActivity(new Intent(this,GoSetupActivity.class));
    }

    private void startLoad(String path,byte mode) {
    	Intent i=new Intent(this,SGFSDCardListActivity.class);    	
    	i.setData((Uri.parse("file://"+path)));
    	GoInteractionProvider.setMode(mode);
    	this.startActivity(i);
    }

    public void solveProblem(View target) {
    	startLoad(getSettings().getTsumegoPath(),GoInteractionProvider.MODE_TSUMEGO);
    }

    public void reviewGame(View target) {
    	startLoad(getSettings().getReviewPath(),GoInteractionProvider.MODE_REVIEW);
    }
    
    public void startSettings(View target) {
    	this.startActivity(new Intent(this,GoPrefsActivity.class));
    }
    
    public void startLinks(View target) {
    	this.startActivity( new Intent(this,LinksActivity.class));
    }
}