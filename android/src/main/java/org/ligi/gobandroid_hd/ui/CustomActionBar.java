package org.ligi.gobandroid_hd.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.ligi.axt.AXT;
import org.ligi.axt.listeners.DialogDiscardingOnClickListener;
import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.ui.gnugo.GnuGoHelper;
import org.ligi.gobandroid_hd.ui.ingame_common.SwitchModeHelper;
import org.ligi.tracedroid.logging.Log;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class CustomActionBar
        extends LinearLayout
        implements GoGame.GoGameChangeListener {

    @InjectView(R.id.white_captures_tv)
    TextView white_captures_tv;

    @InjectView(R.id.black_captures_tv)
    TextView black_captures_tv;

    @InjectView(R.id.move_tv)
    TextView move_tv;

    @InjectView(R.id.mode_tv)
    TextView mode_tv;

    @InjectView(R.id.black_info_container)
    ViewGroup black_info_container;

    @InjectView(R.id.white_info_container)
    ViewGroup white_info_container;

    @OnClick(R.id.fake_spinner)
    void onSpinnerCLick() {
        showModePopup(activity);
    }

    private final LayoutInflater inflater;
    private final App app;
    private final Activity activity;

    private final int highlight_color;
    private final int transparent;

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

    public CustomActionBar(Activity activity) {
        super(activity);

        this.activity = activity;
        app = (App) activity.getApplicationContext();

        highlight_color = getResources().getColor(R.color.dividing_color);
        transparent = getResources().getColor(android.R.color.transparent);

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final ViewGroup top_view = (ViewGroup) inflater.inflate(R.layout.top_nav_and_extras, null);

        ButterKnife.inject(this, top_view);
        addView(top_view);
        refresh();
    }

    private void addItem(LinearLayout container, int image_resId,
                         int str_resid, final Runnable listener) {

        final View v = inflater.inflate(R.layout.dropdown_item, null);
        ((TextView) v.findViewById(R.id.text)).setText(str_resid);
        ((ImageView) v.findViewById(R.id.image)).setImageResource(image_resId);

        v.findViewById(R.id.click_container).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        listener.run();
                    }
                }
        );
        container.addView(v);
    }

    private void addModeItem(LinearLayout container, final byte mode,
                             int string_res, int icon_res) {
        if (mode == app.getInteractionScope().getMode()) {
            return; // already in this mode - no need to present the user with this option
        }

        addItem(container, icon_res, string_res,
                new Runnable() {

                    @Override
                    public void run() {

                        if (mode == InteractionScope.MODE_GNUGO && !(GnuGoHelper.isGnuGoAvail(activity))) {
                            new AlertDialog.Builder(activity).setTitle(R.string.install_gnugo).setMessage(R.string.gnugo_not_installed)
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            AXT.at(activity).startCommonIntent().openUrl("market://details?id=org.ligi.gobandroidhd.ai.gnugo");
                                        }
                                    })
                                    .setNegativeButton(android.R.string.cancel, new DialogDiscardingOnClickListener())
                                    .show();
                            return;
                        }
                        activity.finish();
                        Log.i("set mode" + mode);
                        app.getInteractionScope().setMode(mode);
                        final Intent i = SwitchModeHelper.getIntentByMode(app, mode);
                        activity.startActivity(i);
                    }

                }
        );
    }

    private void showModePopup(Context ctx) {
        final ScrollView scrollView = new ScrollView(ctx);
        final LinearLayout contentView = new LinearLayout(ctx);
        contentView.setOrientation(LinearLayout.VERTICAL);
        final BitmapDrawableNoMinimumSize background = new BitmapDrawableNoMinimumSize(ctx.getResources(), R.drawable.wood_bg);
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

                final boolean isWhitesMove = app.getGame().isBlackToMove() && (!app.getGame().isFinished());
                white_info_container.setBackgroundColor(isWhitesMove ? transparent : highlight_color);
                final boolean isBlacksMove = app.getGame().isBlackToMove() || app.getGame().isFinished();
                black_info_container.setBackgroundColor(isBlacksMove ? highlight_color : transparent);

                move_tv.setText(app.getResources().getString(R.string.move) + app.getGame().getActMove().getMovePos());
            }
        });
    }

}
