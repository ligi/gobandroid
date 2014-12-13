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
import android.view.Menu;
import android.view.MenuItem;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.GobandroidNotifications;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.tsumego.fetch.DownloadProblemsDialog;
import org.ligi.gobandroid_hd.ui.tsumego.fetch.TsumegoSource;

import java.io.File;

/**
 * Activity to load SGF's from SD card
 */

public class SGFFileSystemListActivity extends GobandroidFragmentActivity {

    private File dir;
    private SGFListFragment list_fragment;
    private AsyncTask<TsumegoSource[], String, Integer> downloadProblemsTask = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        setContentView(R.layout.list);

        if (getIntent().getBooleanExtra(GobandroidNotifications.BOOL_FROM_NOTIFICATION_EXTRA_KEY, false)) {
            new GobandroidNotifications(this).cancelNewTsumegosNotification();
        }


        final String sgfPath = getSGFPath();

        if (sgfPath.substring(sgfPath.indexOf('/')).startsWith(getSettings().getTsumegoPath().substring(sgfPath.indexOf('/'))))
            App.getInteractionScope().setMode(InteractionScope.MODE_TSUMEGO);
        if (sgfPath.substring(sgfPath.indexOf('/')).startsWith(getSettings().getReviewPath().substring(sgfPath.indexOf('/'))))
            App.getInteractionScope().setMode(InteractionScope.MODE_REVIEW);

        dir = new File(sgfPath);

        setActionbarProperties();

        list_fragment = SGFListFragment.newInstance(dir);
        getSupportFragmentManager().beginTransaction().replace(R.id.list_fragment, list_fragment).commit();
    }

    private String getSGFPath() {
        if (getIntent().getData() != null) {
            return getIntent().getData().getPath();
        }

        return getSettings().getSGFBasePath();
    }

    private void setActionbarProperties() {
        switch (App.getInteractionScope().getMode()) {
            case InteractionScope.MODE_TSUMEGO:
                this.setTitle(R.string.load_tsumego);
                break;
            default:
                // we can only show stuff for tsumego and review - if in doubt -
                // trade as review
                App.getInteractionScope().setMode(InteractionScope.MODE_REVIEW);
                // fall wanted

            case InteractionScope.MODE_REVIEW:
                this.setTitle(R.string.load_game);
                break;
        }

        this.getSupportActionBar().setSubtitle(dir.getAbsolutePath());
    }

    @Override
    protected void onStop() {
        if (downloadProblemsTask != null) {
            downloadProblemsTask.cancel(true);
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (App.getInteractionScope().getMode() == InteractionScope.MODE_TSUMEGO)
            this.getMenuInflater().inflate(R.menu.refresh_tsumego, menu);

        if (App.getInteractionScope().getMode() == InteractionScope.MODE_REVIEW)
            this.getMenuInflater().inflate(R.menu.review_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                downloadProblemsTask = DownloadProblemsDialog.getAndRunTask(this, list_fragment);
                return true;
            case R.id.menu_del_sgfmeta:
                list_fragment.delete_sgfmeta();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
