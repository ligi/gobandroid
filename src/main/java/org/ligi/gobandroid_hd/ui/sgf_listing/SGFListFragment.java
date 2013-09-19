package org.ligi.gobandroid_hd.ui.sgf_listing;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.ligi.androidhelper.AndroidHelper;
import org.ligi.androidhelper.helpers.dialog.ActivityFinishingOnCancelListener;
import org.ligi.androidhelper.helpers.dialog.ActivityFinishingOnClickListener;
import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.GoLinkLoadActivity;
import org.ligi.gobandroid_hd.ui.GobandroidListFragment;
import org.ligi.gobandroid_hd.ui.Refreshable;
import org.ligi.gobandroid_hd.ui.SGFLoadActivity;
import org.ligi.gobandroid_hd.ui.review.SGFMetaData;
import org.ligi.tracedroid.logging.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SGFListFragment extends GobandroidListFragment implements Refreshable {

    private String[] menu_items;
    private String dir;
    private BaseAdapter adapter;
    private File[] files;

    public SGFListFragment() {
    }

    public SGFListFragment(File dir) {
        this.dir = dir.getAbsolutePath();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            getEnvFromSavedInstance(savedInstanceState);
        }

        if (menu_items == null) { // we got nothing from savedInstance
            refresh();
        }

    }

    private void getEnvFromSavedInstance(Bundle savedInstanceState) {
        if (menu_items == null) {
            menu_items = savedInstanceState.getStringArray("menu_items");
        }

        if (dir == null) {
            dir = savedInstanceState.getString("dir");
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.

        getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        this.getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                getSherlockActivity().startActionMode(new SGFListActionMode(SGFListFragment.this.getActivity(), dir + "/" + menu_items[position], SGFListFragment.this));
                getListView().setItemChecked(position, true);
                return true;
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent intent2start = new Intent(getActivity(), SGFLoadActivity.class);
        String fname = dir + "/" + menu_items[position];

        // check if it is directory behind golink or general
        if (GoLink.isGoLink(fname)) {
            intent2start.setClass(getActivity(), GoLinkLoadActivity.class);
        } else if (!fname.endsWith(".sgf")) {
            intent2start.setClass(getActivity(), SGFFileSystemListActivity.class);
        }

        intent2start.setData(Uri.parse(fname));
        startActivity(intent2start);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray("menu_items", menu_items);
        outState.putString("dir", dir);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void refresh() {
        Log.i("refreshing sgf");
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity()).setTitle(R.string.problem_listing_sgf);

        alert.setPositiveButton(R.string.ok, new ActivityFinishingOnClickListener(getActivity()));
        alert.setOnCancelListener(new ActivityFinishingOnCancelListener(getActivity()));

        if (dir == null) {
            alert.setMessage(getResources().getString(R.string.sgf_path_invalid) + " " + dir).show();
            return;
        }

        File dir_file = new File(dir);

        files = new File(dir).listFiles();

        if (files == null) {
            alert.setMessage(getResources().getString(R.string.there_are_no_files_in) + " " + dir_file.getAbsolutePath()).show();
            return;
        }

        List<String> fileNames = new ArrayList<String>();
        for (File file : files) {
            if ((file.getName().endsWith(".sgf")) || (file.isDirectory()) || (file.getName().endsWith(".golink"))) {
                fileNames.add(file.getName());
            }
        }

        if (fileNames.size() == 0) {
            alert.setMessage(getResources().getString(R.string.there_are_no_files_in) + " " + dir_file.getAbsolutePath()).show();
            return;
        }


        if (getApp().getInteractionScope().getMode() == InteractionScope.MODE_TSUMEGO) {
            List<String> done = new ArrayList<String>(), undone = new ArrayList<String>();
            for (String fname : fileNames)
                if (new SGFMetaData(dir_file.getAbsolutePath() + "/" + fname).is_solved) {
                    done.add(fname);
                } else {
                    undone.add(fname);
                }

            String[] undone_arr = (String[]) undone.toArray(new String[undone.size()]), done_arr = (String[]) done.toArray(new String[done.size()]);
            Arrays.sort(undone_arr);
            Arrays.sort(done_arr);
            menu_items = AndroidHelper.at(undone_arr).combineWith(done_arr);
        } else {
            menu_items = (String[]) fileNames.toArray(new String[fileNames.size()]);
            Arrays.sort(menu_items);
        }

        InteractionScope interaction_scope = ((App) (getActivity().getApplicationContext())).getInteractionScope();

        adapter = getAdapterByInteractionScope(interaction_scope);

        this.setListAdapter(adapter);

    }

    private BaseAdapter getAdapterByInteractionScope(InteractionScope interaction_scope) {
        switch (interaction_scope.getMode()) {
            case InteractionScope.MODE_TSUMEGO:
                return new TsumegoPathViewAdapter(this.getActivity(), menu_items, dir);

            case InteractionScope.MODE_REVIEW:
            default: // use Review adapter as default
                return new ReviewPathViewAdapter(this.getActivity(), menu_items, dir);
        }
    }

    private App getApp() {
        return (App) getActivity().getApplicationContext();
    }

}