package org.ligi.gobandroid_hd.ui.sgf_listing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import org.ligi.androidhelper.AndroidHelper;
import org.ligi.androidhelper.helpers.dialog.ActivityFinishingOnCancelListener;
import org.ligi.androidhelper.helpers.dialog.ActivityFinishingOnClickListener;
import org.ligi.androidhelper.helpers.dialog.DialogDiscardingOnClickListener;
import org.ligi.gobandroid_hd.GobandroidApp;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.GoLinkLoadActivity;
import org.ligi.gobandroid_hd.ui.GobandroidListFragment;
import org.ligi.gobandroid_hd.ui.Refreshable;
import org.ligi.gobandroid_hd.ui.SGFLoadActivity;
import org.ligi.gobandroid_hd.ui.review.SGFMetaData;
import org.ligi.gobandroid_hd.ui.share.ShareAsAttachmentDialog;
import org.ligi.tracedroid.logging.Log;

import java.io.File;
import java.util.Arrays;
import java.util.Vector;

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


        if (savedInstanceState != null)
            getEnvFromSavedInstance(savedInstanceState);

        if (menu_items == null) // we got nothing from savedInstance
            refresh();


    }

    private void getEnvFromSavedInstance(Bundle savedInstanceState) {
        if (menu_items == null)
            menu_items = savedInstanceState.getStringArray("menu_items");

        if (dir == null)
            dir = savedInstanceState.getString("dir");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.


        this.getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {


                new AlertDialog.Builder(getActivity()).setItems(R.array.sgf_longclick_items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                new AlertDialog.Builder(getActivity()).setMessage("Really delete " + dir + "/" + menu_items[position]).setTitle("Delete?")
                                        .setNegativeButton("NO", new DialogDiscardingOnClickListener())
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                new File(dir + "/" + menu_items[position]).delete();
                                                refresh();
                                            }
                                        })

                                        .show();

                                break;
                            case 1:
                                new ShareAsAttachmentDialog(getActivity(), dir + "/" + menu_items[position]).show();
                                break;
                        }
                    }
                }).show();
                return false;
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent intent2start = new Intent(this.getActivity(), SGFLoadActivity.class);
        String fname = dir + "/" + menu_items[position];

        // check if it is directory behind golink or general
        if (GoLink.isGoLink(fname)) {
            intent2start.setClass(getActivity(), GoLinkLoadActivity.class);
        } else if (!fname.endsWith(".sgf")) {
            intent2start.setClass(getActivity(), SGFSDCardListActivity.class);
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


    public void refresh() {
        Log.i("refresh list");
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

        Vector<String> fnames = new Vector<String>();
        for (File file : files)
            if ((file.getName().endsWith(".sgf")) || (file.isDirectory()) || (file.getName().endsWith(".golink"))) {
                fnames.add(file.getName());
                Log.i("refresh adding + " + file.getName());
            }

        if (fnames.size() == 0) {
            alert.setMessage(getResources().getString(R.string.there_are_no_files_in) + " " + dir_file.getAbsolutePath()).show();
            return;
        }


        if (getApp().getInteractionScope().getMode() == InteractionScope.MODE_TSUMEGO) {
            Vector<String> done = new Vector<String>(), undone = new Vector<String>();
            for (String fname : fnames)
                if (new SGFMetaData(dir_file.getAbsolutePath() + "/" + fname).is_solved)
                    done.add(fname);
                else
                    undone.add(fname);

            String[] undone_arr = (String[]) undone.toArray(new String[undone.size()]), done_arr = (String[]) done.toArray(new String[done.size()]);
            Arrays.sort(undone_arr);
            Arrays.sort(done_arr);
            menu_items = AndroidHelper.at(undone_arr).combineWith( done_arr);
        } else {
            menu_items = (String[]) fnames.toArray(new String[fnames.size()]);
            Arrays.sort(menu_items);
        }

       /* if (adapter!=null)
            adapter.notifyDataSetChanged();
         */

        InteractionScope interaction_scope = ((GobandroidApp) (getActivity().getApplicationContext())).getInteractionScope();

        if (interaction_scope.getMode() == InteractionScope.MODE_TSUMEGO)
            adapter = new TsumegoPathViewAdapter(this.getActivity(), menu_items, dir);
        else if (interaction_scope.getMode() == InteractionScope.MODE_REVIEW)
            adapter = new ReviewPathViewAdapter(this.getActivity(), menu_items, dir);

        this.setListAdapter(adapter);

    }

    private GobandroidApp getApp() {
        return (GobandroidApp) getActivity().getApplicationContext();
    }

}