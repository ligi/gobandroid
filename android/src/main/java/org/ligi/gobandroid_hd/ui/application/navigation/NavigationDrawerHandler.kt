package org.ligi.gobandroid_hd.ui.application.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.design.widget.NavigationView
import org.greenrobot.eventbus.EventBus
import org.ligi.gobandroid_hd.App
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.events.GameChangedEvent
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.model.GameProvider
import org.ligi.gobandroid_hd.ui.BaseProfileActivity
import org.ligi.gobandroid_hd.ui.GoPrefs
import org.ligi.gobandroid_hd.ui.GoPrefsActivity
import org.ligi.gobandroid_hd.ui.UnzipSGFsDialog
import org.ligi.gobandroid_hd.ui.application.GoAndroidEnvironment
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity
import org.ligi.gobandroid_hd.ui.links.LinksActivity
import org.ligi.gobandroid_hd.ui.recording.GameRecordActivity
import org.ligi.gobandroid_hd.ui.sgf_listing.SGFFileSystemListActivity
import org.ligi.kaxt.startActivityFromURL
import java.io.File
import javax.inject.Inject

class NavigationDrawerHandler(private val ctx: GobandroidFragmentActivity) {
    private val navigationView: NavigationView

    @Inject
    lateinit internal var env: GoAndroidEnvironment

    @Inject
    lateinit internal var gameProvider: GameProvider

    init {
        App.component().inject(this)
        navigationView = ctx.findViewById(R.id.left_drawer) as NavigationView
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
                    startForPath(env.tsumegoPath)
                },

                R.id.menu_drawer_review to {
                    val path = env.reviewPath
                    startForPath(path)

                },

                R.id.menu_drawer_bookmark to {
                    ctx.startActivity(startSGFListForPath(env.bookmarkPath))
                },


                R.id.menu_drawer_profile to {
                    ctx.startActivity(Intent(ctx, BaseProfileActivity::class.java))
                },


                R.id.menu_drawer_beta to {
                    ctx.startActivityFromURL("https://play.google.com/apps/testing/org.ligi.gobandroid_hd")
                }
        )
    }

    private fun startForClass(java: Class<out Activity>) {
        ctx.startActivity(Intent(ctx, java).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))
    }

    private fun startForPath(path: File) {
        val next = startSGFListForPath(path)

        if (!unzipSGFifNeeded(next)) {
            ctx.startActivity(next)
        }
    }

    fun handle() {
        navigationView.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->
            val function = actionMap[item.itemId]
            if (function != null) {
                ctx.closeDrawers()
                function.invoke()
                return@OnNavigationItemSelectedListener true
            }
            return@OnNavigationItemSelectedListener false
        })
    }


    private fun startSGFListForPath(path: File): Intent {
        val i = Intent(ctx, SGFFileSystemListActivity::class.java)
        i.data = Uri.parse("file://" + path.absolutePath)
        return i
    }

    /**
     * Downloads SGFs and shows a ProgressDialog when needed

     * @return - weather we had to unzip files
     */
    fun unzipSGFifNeeded(intent_after: Intent): Boolean {
        // we check for the tsumego path as the base path could already be there but  no valid tsumego

        val tsumegoPath = env.tsumegoPath
        if (!tsumegoPath.isDirectory || GoPrefs.isVersionSeen(2)) {
            UnzipSGFsDialog(ctx, intent_after, env).show()
            return true
        }
        return false

    }

}
