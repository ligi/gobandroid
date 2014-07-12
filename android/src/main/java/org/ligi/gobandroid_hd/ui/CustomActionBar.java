package org.ligi.gobandroid_hd.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.ligi.axt.listeners.DialogDiscardingOnClickListener;
import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.ui.gnugo.GnuGoHelper;
import org.ligi.gobandroid_hd.ui.ingame_common.SwitchModeHelper;
import org.ligi.tracedroid.logging.Log;

public class CustomActionBar extends LinearLayout implements
        GoGame.GoGameChangeListener, DialogInterface {

    private TextView white_captures_tv;
    private TextView black_captures_tv;
    private TextView move_tv;
    private TextView mode_tv;
    private ViewGroup black_info_container;
    private ViewGroup white_info_container;
    private LayoutInflater inflater;
    private App app;
    private Activity activity;

    public CustomActionBar(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        app.getGame().addGoGameChangeListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        app.getGame().removeGoGameChangeListener(this);
    }

    public CustomActionBar(Activity _activity) {
        super(_activity);

        activity = _activity;
        app = (App) _activity.getApplicationContext();

        inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewGroup top_view = (ViewGroup) inflater.inflate(R.layout.top_nav_and_extras, null);

        white_captures_tv = (TextView) top_view.findViewById(R.id.white_captures_tv);
        black_captures_tv = (TextView) top_view.findViewById(R.id.black_captures_tv);
        move_tv = (TextView) top_view.findViewById(R.id.move_tv);
        mode_tv = (TextView) top_view.findViewById(R.id.mode_tv);
        black_info_container = (ViewGroup) top_view.findViewById(R.id.black_info_container);
        white_info_container = (ViewGroup) top_view.findViewById(R.id.white_info_container);
        // move_tv.setOnClickListener(new ToggleModePopup(context));

        top_view.findViewById(R.id.fake_spinner).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        showModePopup(v.getContext());
                    }

                }
        );

        this.addView(top_view);

        refresh();

    }

    private void addItem(LinearLayout container, int image_resId,
                         int str_resid, final DialogInterface.OnClickListener listener) {

        Log.i("", "container" + container + " inflater" + inflater);
        View v = inflater.inflate(R.layout.dropdown_item, null);
        ((TextView) v.findViewById(R.id.text)).setText(str_resid);
        ((ImageView) v.findViewById(R.id.image)).setImageResource(image_resId);

        v.findViewById(R.id.click_container).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        listener.onClick(CustomActionBar.this, 0);

                    }
                }
        );
        container.addView(v);
    }

    private void addModeItem(LinearLayout container, final byte mode,
                             int string_res, int icon_res) {
        if (mode == app.getInteractionScope().getMode())
            return; // already here

        addItem(container, icon_res, string_res,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (mode == InteractionScope.MODE_GNUGO && !(GnuGoHelper.isGnuGoAvail(activity))) {
                            new AlertDialog.Builder(activity).setTitle(R.string.install_gnugo).setMessage(R.string.gnugo_not_installed)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent i = new Intent();
                                            i.setType(Intent.ACTION_VIEW);
                                            i.setData(Uri.parse("market://details?id=org.ligi.gobandroidhd.ai.gnugo"));
                                            activity.startActivity(i);

                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogDiscardingOnClickListener())
                                    .show();
                            return;
                        }
                        activity.finish();
                        Log.i("settingmode" + mode);
                        app.getInteractionScope().setMode(mode);
                        Intent i = SwitchModeHelper.getIntentByMode(app, mode);
                        activity.startActivity(i);
                    }

                }
        );
    }

    private void showModePopup(Context ctx) {
        ScrollView scrollView = new ScrollView(ctx);
        LinearLayout contentView = new LinearLayout(ctx);
        contentView.setOrientation(LinearLayout.VERTICAL);
        BitmapDrawableNoMinimumSize background = new BitmapDrawableNoMinimumSize(ctx.getResources(), R.drawable.wood_bg);
        contentView.setBackgroundDrawable(background);


        addModeItem(contentView, InteractionScope.MODE_SETUP, R.string.setup, R.drawable.preferences);

        addModeItem(contentView, InteractionScope.MODE_RECORD, R.string.play, R.drawable.play);

        addModeItem(contentView, InteractionScope.MODE_EDIT, R.string.edit, R.drawable.dashboard_record);

        addModeItem(contentView, InteractionScope.MODE_COUNT, R.string.count, R.drawable.dashboard_score);

        addModeItem(contentView, InteractionScope.MODE_REVIEW, R.string.review, R.drawable.dashboard_review);
        addModeItem(contentView, InteractionScope.MODE_TELEVIZE, R.string.televize, R.drawable.gobandroid_tv);
        addModeItem(contentView, InteractionScope.MODE_TSUMEGO, R.string.tsumego, R.drawable.dashboard_tsumego);

        addModeItem(contentView, InteractionScope.MODE_GNUGO, R.string.gnugo, R.drawable.server);

        BetterPopupWindow pop = new BetterPopupWindow(mode_tv);

        scrollView.addView(contentView);
        pop.setContentView(scrollView);

        pop.showLikePopDownMenu();
    }

    @Override
    public void onGoGameChange() {
        Log.i("game changed");
        refresh();
    }

    private void refresh() {
        post(new Runnable() {

            @Override
            public void run() {
                final byte actMode = app.getInteractionScope().getMode();
                mode_tv.setText(InteractionScope.getModeStringRes(actMode));

                white_captures_tv.setText("" + app.getGame().getCapturesWhite());
                black_captures_tv.setText("" + app.getGame().getCapturesBlack());

                int highlight_color = app.getResources().getColor(R.color.dividing_color);
                int transparent = getResources().getColor(android.R.color.transparent);

                boolean isWhitesMove = app.getGame().isBlackToMove() && (!app.getGame().isFinished());
                white_info_container.setBackgroundColor(isWhitesMove ? transparent : highlight_color);
                boolean isBlacksMove = app.getGame().isBlackToMove() || app.getGame().isFinished();
                black_info_container.setBackgroundColor(isBlacksMove ? highlight_color : transparent);
                move_tv.setText(app.getResources().getString(R.string.move) + app.getGame().getActMove().getMovePos());

            }
        });
    }

    @Override
    public void cancel() {
    }

    @Override
    public void dismiss() {
    }

}
