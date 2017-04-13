package org.ligi.gobandroid_hd.ui.application

import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import org.ligi.gobandroid_hd.App
import org.ligi.gobandroid_hd.InteractionScope
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.model.GameProvider
import org.ligi.gobandroid_hd.ui.application.navigation.NavigationDrawerHandler

open class GobandroidFragmentActivity : AppCompatActivity() {

    val env: GoAndroidEnvironment by App.kodein.lazy.instance()
    val interactionScope: InteractionScope  by App.kodein.lazy.instance()
    val gameProvider: GameProvider  by App.kodein.lazy.instance()

    private var drawerToggle: ActionBarDrawerToggle? = null
    private var drawerLayout: DrawerLayout? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (supportActionBar != null) {// yes this happens - e.g.
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
        // a little hack because I strongly disagree with the style guide here
        // ;-)
        // not having the Actionbar overflow menu also with devices with hardware
        // key really helps discoverability
        // http://stackoverflow.com/questions/9286822/how-to-force-use-of-overflow-menu-on-devices-with-menu-button
        try {
            val config = ViewConfiguration.get(this)
            val menuKeyField = ViewConfiguration::class.java.getDeclaredField("sHasPermanentMenuKey")
            if (menuKeyField != null) {
                menuKeyField.isAccessible = true
                menuKeyField.setBoolean(config, false)
            }
        } catch (ignored: Exception) {
            // Ignore - but at least we tried ;-)
        }

        // we do not want focus on custom views ( mainly for GTV )
        if (supportActionBar != null && supportActionBar!!.customView != null) {
            this.supportActionBar!!.customView.isFocusable = false
        }
    }

    fun closeDrawers() {
        drawerLayout!!.closeDrawers()
    }

    override fun setContentView(layoutResId: Int) {
        super.setContentView(R.layout.navigation_drawer_container)

        layoutInflater.inflate(layoutResId, findViewById(R.id.content_frame) as ViewGroup)

        NavigationDrawerHandler(this).handle()

        drawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout

        drawerToggle = object : ActionBarDrawerToggle(this, /* host Activity */
                drawerLayout, /* DrawerLayout object */
                R.string.drawer_open, /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */) {
            override fun onDrawerOpened(drawerView: View?) {
                super.onDrawerOpened(drawerView)
            }

            override fun onDrawerClosed(drawerView: View?) {
                super.onDrawerClosed(drawerView)
            }

        }

        drawerLayout!!.setDrawerListener(drawerToggle)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return drawerToggle!!.onOptionsItemSelected(item)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (drawerToggle != null) {
            drawerToggle!!.syncState()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Pass any configuration change to the drawer toggles
        if (drawerToggle != null) {
            drawerToggle!!.onConfigurationChanged(newConfig)
        }
    }


    open fun doFullScreen(): Boolean {
        return false
    }

    override fun onResume() {
        super.onResume()

        //NaDra mMenuDrawer.refresh();

        if (doFullScreen()) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    val app: App
        get() = applicationContext as App

    val game: GoGame
        get() = gameProvider!!.get()


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_WINDOW) {
            return false
        }
        return super.onKeyDown(keyCode, event)
    }

}
