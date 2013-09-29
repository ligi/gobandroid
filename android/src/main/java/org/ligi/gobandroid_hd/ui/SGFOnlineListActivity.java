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

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import org.ligi.gobandroid_hd.R;

/**
 * Activity to jump to websites with SGF files
 *
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 */

public class SGFOnlineListActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list);        /*
         * // Create the list fragment and add it as our sole content. if
		 * (getSupportFragmentManager().findFragmentById(android.R.id.list) ==
		 * null) { SGFOnlineListFragment list = new SGFOnlineListFragment();
		 * getSupportFragmentManager().beginTransaction().add(android.R.id.list,
		 * list).commit(); }
		 */
    }

}