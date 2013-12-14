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

import android.os.AsyncTask;
import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.GobandroidNotifications;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.tsumego.fetch.DownloadProblemsDialog;
import org.ligi.gobandroid_hd.ui.tsumego.fetch.TsumegoSource;

import java.io.File;

/**
 * Activity to load SGF's from SD card
 *
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 */

public class SGFFileSystemListActivity extends GobandroidFragmentActivity {

    private File dir;
    private SGFListFragment list_fragment;
    private String sgf_path;
    private AsyncTask<TsumegoSource[], String, Integer> my_task = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getSupportActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.list);
        sgf_path = getSettings().getSGFBasePath();

        if (getIntent().getBooleanExtra(GobandroidNotifications.BOOL_FROM_NOTIFICATION_EXTRA_KEY, false))
            new GobandroidNotifications(this).cancelNewTsumegosNotification();

        if (getIntent().getData() != null)
            sgf_path = getIntent().getData().getPath();

        if (sgf_path.substring(sgf_path.indexOf('/')).startsWith(getSettings().getTsumegoPath().substring(sgf_path.indexOf('/'))))
            this.getApp().getInteractionScope().setMode(InteractionScope.MODE_TSUMEGO);
        if (sgf_path.substring(sgf_path.indexOf('/')).startsWith(getSettings().getReviewPath().substring(sgf_path.indexOf('/'))))
            this.getApp().getInteractionScope().setMode(InteractionScope.MODE_REVIEW);
        dir = new File(sgf_path);

        setActionbarProperties();

        list_fragment = new SGFListFragment(dir);
        getSupportFragmentManager().beginTransaction().replace(R.id.list_fragment, list_fragment).commit();
    }

    private void setActionbarProperties() {
        switch (getApp().getInteractionScope().getMode()) {
            case InteractionScope.MODE_TSUMEGO:
                this.setTitle(R.string.load_tsumego);
                break;
            default:
                // we can only show stuff for tsumego and review - if in doubt -
                // trade as review
                getApp().getInteractionScope().setMode(InteractionScope.MODE_REVIEW);
                // fall wanted

            case InteractionScope.MODE_REVIEW:
                this.setTitle(R.string.load_game);
                break;
        }

        this.getSupportActionBar().setSubtitle(dir.getAbsolutePath());
    }

    @Override
    protected void onStop() {
        if (my_task != null) {
            my_task.cancel(true);
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getApp().getInteractionScope().getMode() == InteractionScope.MODE_TSUMEGO)
            this.getSupportMenuInflater().inflate(R.menu.refresh_tsumego, menu);

        if (getApp().getInteractionScope().getMode() == InteractionScope.MODE_REVIEW)
            this.getSupportMenuInflater().inflate(R.menu.review_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                my_task = DownloadProblemsDialog.getAndRunTask(this, list_fragment);
                return true;
            case R.id.menu_del_sgfmeta:
                list_fragment.delete_sgfmeta();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
