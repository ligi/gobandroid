package org.ligi.gobandroid_hd.ui.application;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import java.lang.reflect.Field;
import javax.inject.Inject;
import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.model.GameProvider;
import org.ligi.gobandroid_hd.ui.application.navigation.NavigationDrawerHandler;

public class GobandroidFragmentActivity extends AppCompatActivity {

    @Inject
    protected GoAndroidEnvironment env;

    @Inject
    public InteractionScope interactionScope;

    @Inject
    protected GameProvider gameProvider;

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.component().inject(this);

        if (getSupportActionBar() != null) {// yes this happens - e.g.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // a little hack because I strongly disagree with the style guide here
        // ;-)
        // not having the Actionbar overflow menu also with devices with hardware
        // key really helps discoverability
        // http://stackoverflow.com/questions/9286822/how-to-force-use-of-overflow-menu-on-devices-with-menu-button
        try {
            final ViewConfiguration config = ViewConfiguration.get(this);
            final Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ignored) {
            // Ignore - but at least we tried ;-)
        }

        // we do not want focus on custom views ( mainly for GTV )
        if (getSupportActionBar() != null && getSupportActionBar().getCustomView() != null) {
            this.getSupportActionBar().getCustomView().setFocusable(false);
        }
    }

    public void closeDrawers() {
        drawerLayout.closeDrawers();
    }

    @Override
    public void setContentView(int layoutResId) {
        super.setContentView(R.layout.navigation_drawer_container);
        final View v = getLayoutInflater().inflate(layoutResId, (ViewGroup) findViewById(R.id.drawer_layout), false);
        final ViewGroup vg = (ViewGroup) findViewById(R.id.content_frame);
        vg.addView(v);
        new NavigationDrawerHandler(this).handle();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerToggle = new ActionBarDrawerToggle(this,                  /* host Activity */
                                                 drawerLayout,         /* DrawerLayout object */
                                                 R.string.drawer_open,  /* "open drawer" description */
                                                 R.string.drawer_close  /* "close drawer" description */) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

        };

        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return (drawerToggle.onOptionsItemSelected(item));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (drawerToggle != null) {
            drawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        if (drawerToggle != null) {
            drawerToggle.onConfigurationChanged(newConfig);
        }
    }


    public boolean doFullScreen() {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        //NaDra mMenuDrawer.refresh();

        if (doFullScreen()) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public App getApp() {
        return (App) getApplicationContext();
    }

    public GoGame getGame() {
        return gameProvider.get();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_WINDOW) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
