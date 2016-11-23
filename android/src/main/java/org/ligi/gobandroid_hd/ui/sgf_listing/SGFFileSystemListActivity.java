/**
 * gobandroid
 * by Marcus -Ligi- Bueschleb
 * http://ligi.de
 * <p/>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation;
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 **/

package org.ligi.gobandroid_hd.ui.sgf_listing;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.io.File;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.GobandroidNotifications;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.tsumego.fetch.DownloadProblemsDialog;
import static org.ligi.gobandroid_hd.InteractionScope.Mode.REVIEW;
import static org.ligi.gobandroid_hd.InteractionScope.Mode.TSUMEGO;

/**
 * Activity to load SGF's from SD card
 */

public class SGFFileSystemListActivity extends GobandroidFragmentActivity {

    private SGFListFragment list_fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list);

        if (getIntent().getBooleanExtra(GobandroidNotifications.BOOL_FROM_NOTIFICATION_EXTRA_KEY, false)) {
            new GobandroidNotifications(this).cancelNewTsumegosNotification();
        }

        final File sgfPath = getSGFPath();
        final String sgfPathString = sgfPath.getAbsolutePath();

        if (sgfPathString.substring(sgfPathString.indexOf('/')).startsWith(env.getTsumegoPath().getAbsolutePath().substring(sgfPathString.indexOf('/')))) {
            interactionScope.setMode(InteractionScope.Mode.TSUMEGO);
        }

        if (sgfPathString.substring(sgfPathString.indexOf('/')).startsWith(env.getReviewPath().getAbsolutePath().substring(sgfPathString.indexOf('/')))) {
            interactionScope.setMode(REVIEW);
        }


        setActionbarProperties(sgfPath);

        list_fragment = SGFListFragment.Companion.newInstance(sgfPath);
        getSupportFragmentManager().beginTransaction().replace(R.id.list_fragment, list_fragment).commit();
    }

    private File getSGFPath() {
        if (getIntent().getData() != null) {
            return new File(getIntent().getData().getPath());
        }

        return env.getSGFBasePath();
    }

    private void setActionbarProperties(final File dir) {
        switch (interactionScope.getMode()) {
            case TSUMEGO:
                setTitle(R.string.load_tsumego);
                break;
            default:
                // we can only show stuff for tsumego and review - if in doubt -
                // trade as review
                interactionScope.setMode(REVIEW);
                // fall wanted

            case REVIEW:
                setTitle(R.string.load_game);
                break;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setSubtitle(dir.getAbsolutePath());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (interactionScope.getMode() == TSUMEGO) {
            getMenuInflater().inflate(R.menu.refresh_tsumego, menu);
        }

        if (interactionScope.getMode() == REVIEW) {
            getMenuInflater().inflate(R.menu.review_menu, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                new DownloadProblemsDialog(this, list_fragment).show();

                return true;
            case R.id.menu_del_sgfmeta:
                list_fragment.delete_sgfmeta();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
