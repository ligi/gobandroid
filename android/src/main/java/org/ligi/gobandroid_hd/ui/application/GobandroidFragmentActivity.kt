package org.ligi.gobandroid_hd.ui.application

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import kotlinx.android.synthetic.main.navigation_drawer_container.*
import org.greenrobot.eventbus.EventBus
import org.ligi.gobandroid_hd.App
import org.ligi.gobandroid_hd.InteractionScope
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.events.GameChangedEvent
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.model.GameProvider
import org.ligi.gobandroid_hd.ui.BaseProfileActivity
import org.ligi.gobandroid_hd.ui.GoPrefs
import org.ligi.gobandroid_hd.ui.GoPrefsActivity
import org.ligi.gobandroid_hd.ui.UnzipSGFsDialog
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivityPermissionsDispatcher.startForPathWithCheck
import org.ligi.gobandroid_hd.ui.links.LinksActivity
import org.ligi.gobandroid_hd.ui.recording.GameRecordActivity
import org.ligi.gobandroid_hd.ui.sgf_listing.SGFFileSystemListActivity
import org.ligi.kaxt.startActivityFromURL
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.File

@RuntimePermissions
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

        left_drawer.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->
            val function = actionMap[item.itemId]
            if (function != null) {
                closeDrawers()
                function.invoke()
                return@OnNavigationItemSelectedListener true
            }
            return@OnNavigationItemSelectedListener false
        })

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


    val actionMap by lazy {
        mapOf(
                R.id.menu_drawer_empty to {
                    val act_game = gameProvider.get()
                    gameProvider.set(GoGame(act_game.size.toByte().toInt(), act_game.handicap.toByte().toInt()))
                    EventBus.getDefault().post(GameChangedEvent)
                    startForClass(GameRecordActivity::class.java)
                },

                R.id.menu_drawer_links to {
                    startForClass(LinksActivity::class.java)
                },

                R.id.menu_drawer_settings to {
                    startForClass(GoPrefsActivity::class.java)
                },

                R.id.menu_drawer_tsumego to {
                    startForPathWithCheck(this, env.tsumegoPath)
                },

                R.id.menu_drawer_review to {
                    val path = env.reviewPath
                    startForPathWithCheck(this, path)

                },

                R.id.menu_drawer_bookmark to {
                    startActivity(startSGFListForPath(env.bookmarkPath))
                },


                R.id.menu_drawer_profile to {
                    startActivity(Intent(this, BaseProfileActivity::class.java))
                },

                R.id.menu_drawer_beta to {
                    startActivityFromURL("https://play.google.com/apps/testing/org.ligi.gobandroid_hd")
                },

                R.id.menu_drawer_translation to {
                    startActivityFromURL("https://www.transifex.com/ligi/gobandroid")
                }
        )
    }

    private fun startForClass(java: Class<out Activity>) {
        startActivity(Intent(this, java).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))
    }

    @NeedsPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    protected fun startForPath(path: File) {
        val next = startSGFListForPath(path)

        if (!unzipSGFifNeeded(next)) {
            startActivity(next)
        }
    }


    private fun startSGFListForPath(path: File) = Intent(this, SGFFileSystemListActivity::class.java).apply {
        data = Uri.parse("file://" + path.absolutePath)
    }

    /**
     * Downloads SGFs and shows a ProgressDialog when needed

     * @return - if we had to unzip files
     */

    private fun unzipSGFifNeeded(intent_after: Intent): Boolean {
        // we check for the tsumego path as the base path could already be there but  no valid tsumego

        val tsumegoPath = env.tsumegoPath
        if (!tsumegoPath.isDirectory || GoPrefs.isVersionSeen(2)) {
            UnzipSGFsDialog(this, intent_after, env).show()
            return true
        }
        return false

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        GobandroidFragmentActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults)
    }

}
