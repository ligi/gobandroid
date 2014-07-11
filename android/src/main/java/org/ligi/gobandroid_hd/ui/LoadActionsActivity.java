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

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.sgf_listing.SGFFileSystemListActivity;

/**
 * Activity to select a source from where to load SGF's
 *
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 */

public class LoadActionsActivity extends ListActivity {

    private final static String[] menu_items = {"SD Card", "Online"};

    private final static int MENU_SDCARD = 0;
    private final static int MENU_ONLINE = 1;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoPrefs.init(this);

        setContentView(R.layout.list);
        this.setListAdapter(new ArrayAdapter<>(this, R.layout.list_item, menu_items));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        startActivity(getIntentByPosition(position));
    }

    private Intent getIntentByPosition(final int position) {
        switch (position) {
            case MENU_SDCARD:
                return new Intent(this, SGFFileSystemListActivity.class);

            case MENU_ONLINE:
                return new Intent(this, SGFOnlineListActivity.class);
        }
        return null; // never expected
    }

}