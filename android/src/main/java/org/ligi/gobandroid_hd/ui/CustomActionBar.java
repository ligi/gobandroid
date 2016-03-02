package org.ligi.gobandroid_hd.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.ligi.axt.AXT;
import org.ligi.axt.listeners.DialogDiscardingOnClickListener;
import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.events.GameChangedEvent;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.model.GameProvider;
import org.ligi.gobandroid_hd.ui.gnugo.GnuGoHelper;
import org.ligi.gobandroid_hd.ui.ingame_common.SwitchModeHelper;
import org.ligi.tracedroid.logging.Log;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.ligi.gobandroid_hd.InteractionScope.Mode.COUNT;
import static org.ligi.gobandroid_hd.InteractionScope.Mode.EDIT;
import static org.ligi.gobandroid_hd.InteractionScope.Mode.GNUGO;
import static org.ligi.gobandroid_hd.InteractionScope.Mode.RECORD;
import static org.ligi.gobandroid_hd.InteractionScope.Mode.REVIEW;
import static org.ligi.gobandroid_hd.InteractionScope.Mode.SETUP;
import static org.ligi.gobandroid_hd.InteractionScope.Mode.TELEVIZE;
import static org.ligi.gobandroid_hd.InteractionScope.Mode.TSUMEGO;

public class CustomActionBar extends LinearLayout {

    @Inject
    GameProvider gameProvider;

    @Inject
    InteractionScope interactionScope;

    @Bind(R.id.white_captures_tv)
    TextView white_captures_tv;

    @Bind(R.id.black_captures_tv)
    TextView black_captures_tv;

    @Bind(R.id.move_tv)
    TextView move_tv;

    @Bind(R.id.mode_tv)
    TextView mode_tv;

    @Bind(R.id.blackStoneImageView)
    View black_info_container;

    @Bind(R.id.whiteStoneImageview)
    View white_info_container;


    @OnClick(R.id.mode_tv)
    void onModeSpinnerClick() {
        showModePopup(activity);
    }

    @OnClick(R.id.move_tv)
    void onMoveSpinnerClick() {
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
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    public CustomActionBar(Activity activity) {
        super(activity);

        App.component().inject(this);
        this.activity = activity;
        app = (App) activity.getApplicationContext();

        highlight_color = getResources().getColor(R.color.dividing_color);
        transparent = getResources().getColor(android.R.color.transparent);

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View top_view = inflater.inflate(R.layout.top_nav_and_extras, this);

        ButterKnife.bind(this, top_view);
        refresh();
    }

    private void addItem(LinearLayout container, int image_resId, int str_resid, final Runnable listener) {

        final View v = inflater.inflate(R.layout.dropdown_item, container, false);
        ((TextView) v.findViewById(R.id.text)).setText(str_resid);
        ((ImageView) v.findViewById(R.id.image)).setImageResource(image_resId);

        v.findViewById(R.id.click_container).setOnClickListener(new View.OnClickListener() {

                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        listener.run();
                                                                    }
                                                                });
        container.addView(v);
    }

    private void addModeItem(LinearLayout container, final InteractionScope.Mode mode, int string_res, int icon_res) {
        if (mode == interactionScope.getMode()) {
            return; // already in this mode - no need to present the user with this option
        }

        addItem(container, icon_res, string_res, new Runnable() {

                    @Override
                    public void run() {

                        if (mode == InteractionScope.Mode.GNUGO && !(GnuGoHelper.isGnuGoAvail(activity))) {
                            new AlertDialog.Builder(activity).setTitle(R.string.install_gnugo)
                                                             .setMessage(R.string.gnugo_not_installed)
                                                             .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                                 @Override
                                                                 public void onClick(DialogInterface dialog, int which) {
                                                                     AXT.at(activity)
                                                                        .startCommonIntent()
                                                                        .openUrl("market://details?id=org.ligi.gobandroidhd.ai.gnugo");
                                                                 }
                                                             })
                                                             .setNegativeButton(android.R.string.cancel, new DialogDiscardingOnClickListener())
                                                             .show();
                            return;
                        }
                        activity.finish();
                        Log.i("set mode" + mode);
                        interactionScope.setMode(mode);
                        final Intent i = SwitchModeHelper.getIntentByMode(app, mode);
                        activity.startActivity(i);
                    }

                });
    }

    private void showModePopup(Context ctx) {
        final ScrollView scrollView = new ScrollView(ctx);
        final LinearLayout contentView = new LinearLayout(ctx);
        contentView.setOrientation(LinearLayout.VERTICAL);
        final BitmapDrawableNoMinimumSize background = new BitmapDrawableNoMinimumSize(ctx.getResources(), R.drawable.wood_bg);
        contentView.setBackgroundDrawable(background);

        addModeItem(contentView, SETUP, R.string.setup, R.drawable.ic_action_settings_overscan);

        addModeItem(contentView, RECORD, R.string.play, R.drawable.ic_social_people);

        addModeItem(contentView, EDIT, R.string.edit, R.drawable.ic_editor_mode_edit);

        if (gameProvider.get().getActMove().getMovePos() > 0) { // these modes only make sense if there is minimum one
            addModeItem(contentView, COUNT, R.string.count, R.drawable.ic_editor_pie_chart);
            addModeItem(contentView, REVIEW, R.string.review, R.drawable.ic_maps_local_movies);
            addModeItem(contentView, TELEVIZE, R.string.televize, R.drawable.ic_notification_live_tv);
            addModeItem(contentView, TSUMEGO, R.string.tsumego, R.drawable.ic_action_extension);
        }

        if (isPlayStoreInstalled() || GnuGoHelper.isGnuGoAvail(activity)) {
            addModeItem(contentView, GNUGO, R.string.gnugo, R.drawable.ic_hardware_computer);
        }

        final BetterPopupWindow pop = new BetterPopupWindow(mode_tv);

        scrollView.addView(contentView);
        pop.setContentView(scrollView);

        pop.showLikePopDownMenu();
    }

    @Subscribe
    public void onGoGameChaged(GameChangedEvent event) {
        refresh();
    }

    private void refresh() {
        post(new Runnable() {

            @Override
            public void run() {
                final InteractionScope.Mode actMode = interactionScope.getMode();

                mode_tv.setText(actMode.getStringRes());

                final GoGame game = gameProvider.get();

                white_captures_tv.setText("" + game.getCapturesWhite());
                black_captures_tv.setText("" + game.getCapturesBlack());

                final boolean isWhitesMove = game.isBlackToMove() && (!game.isFinished());
                white_info_container.setBackgroundColor(isWhitesMove ? transparent : highlight_color);
                white_captures_tv.setBackgroundColor(isWhitesMove ? transparent : highlight_color);

                final boolean isBlacksMove = game.isBlackToMove() || game.isFinished();
                black_info_container.setBackgroundColor(isBlacksMove ? highlight_color : transparent);
                black_captures_tv.setBackgroundColor(isBlacksMove ? highlight_color : transparent);

                move_tv.setText(app.getResources().getString(R.string.move) + game.getActMove().getMovePos());
            }
        });
    }

    private static final String GooglePlayStorePackageNameOld = "com.google.market";
    private static final String GooglePlayStorePackageNameNew = "com.android.vending";

    private boolean isPlayStoreInstalled() {
        final PackageManager packageManager = app.getPackageManager();
        final List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (PackageInfo packageInfo : packages) {
            if (packageInfo.packageName.equals(GooglePlayStorePackageNameOld) || packageInfo.packageName.equals(GooglePlayStorePackageNameNew)) {
                return true;
            }
        }
        return false;
    }

}
