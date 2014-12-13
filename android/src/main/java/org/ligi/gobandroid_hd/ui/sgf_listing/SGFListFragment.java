package org.ligi.gobandroid_hd.ui.sgf_listing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Optional;

import org.ligi.axt.AXT;
import org.ligi.axt.listeners.ActivityFinishingOnCancelListener;
import org.ligi.axt.listeners.ActivityFinishingOnClickListener;
import org.ligi.axt.listeners.DialogDiscardingOnClickListener;
import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.helper.SGFFileNameFilter;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.sgf.SGFReader;
import org.ligi.gobandroid_hd.ui.GoLinkLoadActivity;
import org.ligi.gobandroid_hd.ui.Refreshable;
import org.ligi.gobandroid_hd.ui.SGFLoadActivity;
import org.ligi.gobandroid_hd.ui.review.SGFMetaData;
import org.ligi.gobandroid_hd.ui.sgf_listing.item_view_holder.PathViewHolder;
import org.ligi.gobandroid_hd.ui.sgf_listing.item_view_holder.ReviewViewHolder;
import org.ligi.gobandroid_hd.ui.sgf_listing.item_view_holder.TsumegoViewHolder;
import org.ligi.gobandroid_hd.ui.sgf_listing.item_view_holder.ViewHolderInterface;
import org.ligi.tracedroid.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.text.TextUtils.isEmpty;

public class SGFListFragment extends Fragment implements Refreshable {

    public static final String EXTRA_DIR = "dir";
    public static final String EXTRA_MENU_ITEMS = "menu_items";

    private String[] menu_items;
    private String dir;
    private Optional<ActionMode> actionMode = Optional.absent();

    public static SGFListFragment newInstance(File dir) {
        final SGFListFragment f = new SGFListFragment();

        final Bundle args = new Bundle();
        args.putString(EXTRA_DIR, dir.getAbsolutePath());
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getEnvFromSavedInstance();

        if (menu_items == null) { // we got nothing from savedInstance
            refresh();
        }

    }


    private void getEnvFromSavedInstance() {
        if (menu_items == null) {
            menu_items = getArguments().getStringArray(EXTRA_MENU_ITEMS);
        }

        if (dir == null) {
            dir = getArguments().getString(EXTRA_DIR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View inflate = View.inflate(container.getContext(), R.layout.recycler_view, null);
        final RecyclerView recylerView = (RecyclerView) inflate.findViewById(R.id.content_recycler);

        final int rows = getResources().getInteger(R.integer.sgf_list_rows);

        recylerView.setLayoutManager(new StaggeredGridLayoutManager(rows, OrientationHelper.VERTICAL));
        recylerView.setAdapter(new RecyclerView.Adapter() {

            private final static int TYPE_PATH = 0;
            private final static int TYPE_TSUMEGO = 1;
            private final static int TYPE_GOLINK = 2;
            private final static int TYPE_REVIEW = 3;

            @Override
            public int getItemViewType(int position) {

                if (getFile(position).isDirectory()) {
                    return TYPE_PATH;
                }

                if (GoLink.isGoLink(getFile(position))) {
                    return TYPE_GOLINK;
                }

                if (App.getInteractionScope().getMode() == InteractionScope.MODE_TSUMEGO) {
                    return TYPE_TSUMEGO;
                }

                return TYPE_REVIEW;
            }

            private File getFile(int position) {
                final String fileName = dir + "/" + menu_items[position];
                return new File(fileName);
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                final LayoutInflater inflator = LayoutInflater.from(parent.getContext());

                switch (viewType) {
                    case TYPE_PATH:
                        return new PathViewHolder(inflator.inflate(R.layout.sgf_dir_list_item, parent, false));
                    case TYPE_TSUMEGO:
                        return new TsumegoViewHolder(inflator.inflate(R.layout.sgf_tsumego_list_item, parent, false));

                    case TYPE_GOLINK:
                    case TYPE_REVIEW:
                        return new ReviewViewHolder(inflator.inflate(R.layout.sgf_review_game_details_list_item, parent, false));

                    default:
                        throw new IllegalStateException("unknown view-type " + viewType);
                }
            }

            @Override
            public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
                ((ViewHolderInterface) holder).apply(getFile(position));


                final CardView cardView = (CardView) holder.itemView;

                cardView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (v.getTag(R.id.tag_actionmode)!=null) {
                            return false;
                        }
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_CANCEL:
                            case MotionEvent.ACTION_UP:
                                cardView.setCardElevation(getResources().getDimension(R.dimen.cardview_default_elevation));
                                break;

                            case MotionEvent.ACTION_DOWN:
                                cardView.setCardElevation(getResources().getDimension(R.dimen.cardview_unelevated_elevation));
                                break;
                        }
                        return false;
                    }
                });


                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Intent intent2start = new Intent(getActivity(), SGFLoadActivity.class);
                        final String fileName = dir + "/" + menu_items[position];

                        // check if it is directory behind golink or general
                        if (GoLink.isGoLink(fileName)) {
                            intent2start.setClass(getActivity(), GoLinkLoadActivity.class);
                        } else if (!fileName.endsWith(".sgf")) {
                            intent2start.setClass(getActivity(), SGFFileSystemListActivity.class);
                        }

                        intent2start.setData(Uri.parse(fileName));

                        if (actionMode.isPresent()) {
                            actionMode.get().finish();
                        }

                        startActivity(intent2start);

                    }
                });

                cardView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        if (!(getActivity() instanceof ActionBarActivity)) {
                            Log.w("Activity not instanceof ActionbarActivity - this is not really expected");
                            return false;
                        }

                        v.setTag(R.id.tag_actionmode,Boolean.TRUE);

                        final ActionBarActivity activity = (ActionBarActivity) getActivity();
                        actionMode = Optional.fromNullable(activity.startSupportActionMode(getActionMode(position)));

                        cardView.setCardElevation(getResources().getDimension(R.dimen.cardview_elevated_elevation));

                        return true;
                    }

                    private SGFListActionMode getActionMode(final int position) {
                        String fileName = dir + "/" + menu_items[position];
                        File file = new File(fileName);
                        int menuResource = R.menu.list_file_sgf_action_mode;

                        if (file.isDirectory()) {
                            menuResource = R.menu.list_dir_sgf_action_mode;
                        }

                        return new SGFListActionMode(SGFListFragment.this.getActivity(), fileName, SGFListFragment.this, menuResource) {
                            @Override
                            public void onDestroyActionMode(ActionMode mode) {
                                actionMode = Optional.absent();
                                cardView.setCardElevation(getResources().getDimension(R.dimen.cardview_default_elevation));
                                cardView.setTag(R.id.tag_actionmode,null);
                                super.onDestroyActionMode(mode);
                            }
                        };
                    }
                });
            }

            @Override
            public int getItemCount() {
                if (menu_items == null) {
                    return 0;
                }
                return menu_items.length;
            }
        });
        return inflate;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray(EXTRA_MENU_ITEMS, menu_items);
        outState.putString(EXTRA_DIR, dir);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void refresh() {
        Log.i("refreshing sgf");
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity()).setTitle(R.string.problem_listing_sgf);

        alert.setPositiveButton(R.string.ok, new ActivityFinishingOnClickListener(getActivity()));
        alert.setOnCancelListener(new ActivityFinishingOnCancelListener(getActivity()));

        if (dir == null) {
            alert.setMessage(getResources().getString(R.string.sgf_path_invalid) + " " + dir).show();
            return;
        }

        final File dir_file = new File(dir);
        final File[] files = new File(dir).listFiles();

        if (files == null) {
            alert.setMessage(getResources().getString(R.string.there_are_no_files_in) + " " + dir_file.getAbsolutePath()).show();
            return;
        }

        final List<String> fileNames = new ArrayList<>();
        for (File file : files) {
            if ((file.getName().endsWith(".sgf")) || (file.isDirectory()) || (file.getName().endsWith(".golink"))) {
                fileNames.add(file.getName());
            }
        }

        if (fileNames.size() == 0) {
            alert.setMessage(getResources().getString(R.string.there_are_no_files_in) + " " + dir_file.getAbsolutePath()).show();
            return;
        }


        if (App.getInteractionScope().getMode() == InteractionScope.MODE_TSUMEGO) {

            if (fileNames.size() > 1000) {
                try {
                    final String[] list = dir_file.list(new SGFFileNameFilter());
                    final GoGame game1 = SGFReader.sgf2game(AXT.at(new File(dir_file, list[10])).readToString(), null, SGFReader.BREAKON_FIRSTMOVE);
                    final GoGame game2 = SGFReader.sgf2game(AXT.at(new File(dir_file, list[12])).readToString(), null, SGFReader.BREAKON_FIRSTMOVE);
                    if (!isEmpty(game1.getMetaData().getDifficulty()) && !isEmpty(game2.getMetaData().getDifficulty())) {
                        new AlertDialog.Builder(getActivity()).setMessage("This looks like the gogameguru offline selection - sort by difficulty")
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new GoProblemsRenaming(getActivity(), dir_file).execute();
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, null)

                                .show();
                    }
                } catch (IOException e) {
                    Log.w("problem in gogameguru rename offer " + e);
                }
                return;
            }

            List<String> done = new ArrayList<>(), undone = new ArrayList<>();
            for (String fileName : fileNames)
                if (new SGFMetaData(dir_file.getAbsolutePath() + "/" + fileName).is_solved) {
                    done.add(fileName);
                } else {
                    undone.add(fileName);
                }

            final String[] undone_arr = undone.toArray(new String[undone.size()]), done_arr = done.toArray(new String[done.size()]);
            Arrays.sort(undone_arr);
            Arrays.sort(done_arr);
            menu_items = AXT.at(undone_arr).combineWith(done_arr);
        } else {
            menu_items = fileNames.toArray(new String[fileNames.size()]);
            Arrays.sort(menu_items);
        }

    }

    public void delete_sgfmeta() {
        Log.i("delete sgfmeta files");
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity()).setTitle(R.string.del_sgfmeta);
        alertBuilder.setMessage(R.string.del_sgfmeta_prompt);

        if (dir == null) {
            alertBuilder.setMessage(getResources().getString(R.string.sgf_path_invalid) + " " + dir).show();
            return;
        }

        final File dir_file = new File(dir);
        final File[] filesToDelete = new File(dir).listFiles();

        if (filesToDelete == null) {
            alertBuilder.setMessage(getResources().getString(R.string.there_are_no_files_in) + " " + dir_file.getAbsolutePath()).show();
            return;
        }

        alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.dismiss();
                for (File file : filesToDelete) {
                    if (file.getName().endsWith(SGFMetaData.FNAME_ENDING)) {
                        file.delete();
                    }
                }
                refresh();
            }
        });

        alertBuilder.setNegativeButton(R.string.cancel, new DialogDiscardingOnClickListener());

        alertBuilder.create().show();
    }

}