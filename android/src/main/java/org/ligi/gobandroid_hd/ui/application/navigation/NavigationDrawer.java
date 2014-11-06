package org.ligi.gobandroid_hd.ui.application.navigation;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.CloudHooks;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.ui.BaseProfileActivity;
import org.ligi.gobandroid_hd.ui.GoPrefsActivity;
import org.ligi.gobandroid_hd.ui.HelpDialog;
import org.ligi.gobandroid_hd.ui.UnzipSGFsDialog;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.links.LinksActivity;
import org.ligi.gobandroid_hd.ui.recording.GameRecordActivity;
import org.ligi.gobandroid_hd.ui.sgf_listing.SGFFileSystemListActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NavigationDrawer implements OnItemClickListener {

    private GobandroidFragmentActivity ctx;
    private ListView mListView;

    public NavigationDrawer(GobandroidFragmentActivity ctx) {
        this.ctx = ctx;

        mListView = (ListView) ctx.findViewById(R.id.left_drawer);// new ListView(ctx);
        mListView.setOnItemClickListener(this);

        refresh();
    }

    public void refresh() {
        mListView.setAdapter(getAdapter());
    }

    public ListAdapter getAdapter() {

        List<Object> items = new ArrayList<Object>();
        items.add(new Category(R.string.load));
        items.add(new Item(R.id.empty, R.string.empty_board, R.drawable.play));
        items.add(new Item(R.id.tsumego, R.string.tsumego, R.drawable.dashboard_tsumego));
        items.add(new Item(R.id.review, R.string.review, R.drawable.dashboard_review));
        items.add(new Item(R.id.bookmark, R.string.bookmark, R.drawable.bookmark));

        items.add(new Category(R.string.more));

        /*
            items.add(new Item(R.id.online_play, R.string.online_play, R.drawable.online_play));
        */

        items.add(new Item(R.id.profile, R.string.profile, R.drawable.profile));
        items.add(new Item(R.id.links, R.string.links, R.drawable.dashboard_links));
        items.add(new Item(R.id.preferences, R.string.preferences, R.drawable.preferences));
        items.add(new Item(R.id.help, R.string.help, R.drawable.help));

        return new MenuAdapter(items);
    }

    private boolean handleId(int id) {
        switch (id) {
            case R.id.help:

                new HelpDialog(ctx).show();
                App.getTracker().trackEvent("ui_action", "dashboard", "help", null);

                return true;
            case R.id.empty:
                final GoGame act_game = App.getGame();

                App.setGame(new GoGame((byte) act_game.getSize(), (byte) act_game.getHandicap()));
                App.getGame().notifyGameChange();

                ctx.startActivity(new Intent(ctx, GameRecordActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                return true;


            case R.id.links:
                ctx.startActivity(new Intent(ctx, LinksActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                return true;

            case R.id.preferences:

                ctx.startActivity(new Intent(ctx, GoPrefsActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                return true;


            case R.id.online_play:
                CloudHooks.profileOrOnlinePlay(ctx);
                return true;

            case R.id.tsumego:
                Intent next = startSGFListForPath(getApp().getSettings().getTsumegoPath());

                if (!unzipSGFifNeeded(next))
                    ctx.startActivity(next);

                return true;

            case R.id.review:
                Intent next2 = startSGFListForPath(getApp().getSettings().getReviewPath());

                if (!unzipSGFifNeeded(next2))
                    ctx.startActivity(next2);

                return true;

            case R.id.bookmark:
                ctx.startActivity(startSGFListForPath(getApp().getSettings().getBookmarkPath()));

                return true;


            case R.id.profile:
                ctx.startActivity(new Intent(ctx, BaseProfileActivity.class));

                return true;
        }
        return false;
    }

    private Intent startSGFListForPath(String path) {
        Intent i = new Intent(ctx, SGFFileSystemListActivity.class);
        i.setData(Uri.parse("file://" + path));
        return i;
    }

    private App getApp() {
        return (App) ctx.getApplicationContext();
    }

    private class Item {

        String mTitle;
        int mIconRes;
        int id;

        Item(int id, int title, int iconRes) {
            this.id = id;
            mTitle = ctx.getString(title);
            mIconRes = iconRes;
        }
    }

    private class Category {

        String mTitle;

        Category(int title) {
            mTitle = ctx.getString(title);
        }
    }

    private class MenuAdapter extends BaseAdapter {

        private List<Object> mItems;

        MenuAdapter(List<Object> items) {
            mItems = items;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position) instanceof Item ? 0 : 1;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public boolean isEnabled(int position) {
            return getItem(position) instanceof Item;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            Object item = getItem(position);

            if (item instanceof Category) {
                if (v == null) {
                    v = ctx.getLayoutInflater().inflate(R.layout.menu_row_category, parent, false);
                }

                ((TextView) v).setText(((Category) item).mTitle);

            } else {
                if (v == null) {
                    v = ctx.getLayoutInflater().inflate(R.layout.menu_row_item, parent, false);
                }

                v.setTag(((Item) item).id);

                TextView tv = (TextView) v;
                tv.setText(((Item) item).mTitle);
                // BitmapDrawable bmp=BitmapDrawable.c;
                Resources res = tv.getContext().getResources();
                int icon_size = res.getDimensionPixelSize(R.dimen.actionbar_height);
                Bitmap bmp = (Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, ((Item) item).mIconRes, null), icon_size, icon_size, false));
                Drawable bmp_d = new BitmapDrawable(ctx.getResources(), bmp);
                tv.setCompoundDrawablesWithIntrinsicBounds(bmp_d, null, null, null);
            }

			/*
             * v.setTag(R.id.mdActiveViewPosition, position);
			 *
			 * if (position == mActivePosition) { mMenuDrawer.setActiveView(v,
			 * position); }
			 */

            return v;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if (handleId((Integer) arg1.getTag())) {
            ctx.closeDrawers();
        }
        ;
    }

    /**
     * Downloads SGFs and shows a ProgressDialog when needed
     *
     * @return - weather we had to unzip files
     */
    public boolean unzipSGFifNeeded(Intent intent_after) {
        // we check for the tsumego path as the base path could already be there but  no valid tsumego

        String tsumegoPath = getApp().getSettings().getTsumegoPath();
        if (!(new File(tsumegoPath)).isDirectory()) {
            UnzipSGFsDialog.show(ctx, intent_after);
            return true;
        }
        return false;
        /*
        String storrage_state = Environment.getExternalStorageState();

        if ((storrage_state.equals(Environment.MEDIA_MOUNTED) && (!(new File(getApp().getSettings().getTsumegoPath())).isDirectory()))) {
            UnzipSGFsDialog.show(ctx, intent_after);
            return true;
        }*/

    }

}
