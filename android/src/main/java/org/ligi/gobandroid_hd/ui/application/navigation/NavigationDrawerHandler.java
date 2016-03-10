package org.ligi.gobandroid_hd.ui.application.navigation;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;
import org.ligi.axt.AXT;
import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.events.GameChangedEvent;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.model.GameProvider;
import org.ligi.gobandroid_hd.ui.BaseProfileActivity;
import org.ligi.gobandroid_hd.ui.GoPrefsActivity;
import org.ligi.gobandroid_hd.ui.UnzipSGFsDialog;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.application.GobandroidSettings;
import org.ligi.gobandroid_hd.ui.links.LinksActivity;
import org.ligi.gobandroid_hd.ui.recording.GameRecordActivity;
import org.ligi.gobandroid_hd.ui.sgf_listing.SGFFileSystemListActivity;

import java.io.File;

import javax.inject.Inject;

public class NavigationDrawerHandler {

    private final GobandroidFragmentActivity ctx;
    private final NavigationView navigationDrawer;

    @Inject
    GobandroidSettings settings;

    @Inject
    GameProvider gameProvider;

    public NavigationDrawerHandler(final GobandroidFragmentActivity ctx) {
        App.component().inject(this);
        this.ctx = ctx;

        navigationDrawer = (NavigationView) ctx.findViewById(R.id.left_drawer);
    }

    public void handle() {
        navigationDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.menu_drawer_empty:
                        final GoGame act_game = gameProvider.get();

                        gameProvider.set(new GoGame((byte) act_game.getSize(), (byte) act_game.getHandicap()));

                        EventBus.getDefault().post(GameChangedEvent.INSTANCE);

                        ctx.startActivity(new Intent(ctx, GameRecordActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                        return true;


                    case R.id.menu_drawer_links:
                        ctx.startActivity(new Intent(ctx, LinksActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                        return true;

                    case R.id.menu_drawer_settings:

                        ctx.startActivity(new Intent(ctx, GoPrefsActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                        return true;


                    /*case R.id.online_play:
                        CloudHooks.profileOrOnlinePlay(ctx);
                        return true;
*/

                    case R.id.menu_drawer_tsumego:
                        Intent next = startSGFListForPath(settings.getTsumegoPath());

                        if (!unzipSGFifNeeded(next)) {
                            ctx.startActivity(next);
                        }

                        return true;

                    case R.id.menu_drawer_review:
                        Intent next2 = startSGFListForPath(settings.getReviewPath());

                        if (!unzipSGFifNeeded(next2)) {
                            ctx.startActivity(next2);
                        }

                        return true;

                    case R.id.menu_drawer_bookmark:
                        ctx.startActivity(startSGFListForPath(settings.getBookmarkPath()));

                        return true;


                    case R.id.menu_drawer_profile:
                        ctx.startActivity(new Intent(ctx, BaseProfileActivity.class));

                        return true;


                    case R.id.menu_drawer_beta:
                        AXT.at(ctx).startCommonIntent().openUrl("https://play.google.com/apps/testing/org.ligi.gobandroid_hd");
                        return true;

                }
                return false;
            }
        });
    }

    private Intent startSGFListForPath(File path) {
        final Intent i = new Intent(ctx, SGFFileSystemListActivity.class);
        i.setData(Uri.parse("file://" + path.getAbsolutePath()));
        return i;
    }

    /**
     * Downloads SGFs and shows a ProgressDialog when needed
     *
     * @return - weather we had to unzip files
     */
    public boolean unzipSGFifNeeded(Intent intent_after) {
        // we check for the tsumego path as the base path could already be there but  no valid tsumego

        final File tsumegoPath = settings.getTsumegoPath();
        if (!tsumegoPath.isDirectory() || settings.isVersionSeen(1)) {
            new UnzipSGFsDialog(ctx, intent_after, settings).show();
            return true;
        }
        return false;

    }

}
