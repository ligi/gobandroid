package org.ligi.gobandroid_hd.ui.application.navigation

import android.content.Intent
import android.net.Uri
import android.support.design.widget.NavigationView
import org.greenrobot.eventbus.EventBus
import org.ligi.axt.AXT
import org.ligi.gobandroid_hd.App
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.events.GameChangedEvent
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.model.GameProvider
import org.ligi.gobandroid_hd.ui.BaseProfileActivity
import org.ligi.gobandroid_hd.ui.GoPrefsActivity
import org.ligi.gobandroid_hd.ui.UnzipSGFsDialog
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity
import org.ligi.gobandroid_hd.ui.application.GobandroidSettings
import org.ligi.gobandroid_hd.ui.links.LinksActivity
import org.ligi.gobandroid_hd.ui.recording.GameRecordActivity
import org.ligi.gobandroid_hd.ui.sgf_listing.SGFFileSystemListActivity
import java.io.File
import javax.inject.Inject

class NavigationDrawerHandler(private val ctx: GobandroidFragmentActivity) {
    private val navigationView: NavigationView

    @Inject
    lateinit internal var settings: GobandroidSettings

    @Inject
    lateinit internal var gameProvider: GameProvider

    init {
        App.component().inject(this)

        navigationView = ctx.findViewById(R.id.left_drawer) as NavigationView

    }

    fun handle() {
        navigationView.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.menu_drawer_empty -> {
                    val act_game = gameProvider.get()

                    gameProvider.set(GoGame(act_game.size.toByte().toInt(), act_game.handicap.toByte().toInt()))

                    EventBus.getDefault().post(GameChangedEvent.INSTANCE)

                    ctx.closeDrawers()
                    ctx.startActivity(Intent(ctx, GameRecordActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))
                    return@OnNavigationItemSelectedListener true
                }

                R.id.menu_drawer_links -> {
                    ctx.closeDrawers()
                    ctx.startActivity(Intent(ctx, LinksActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))
                    return@OnNavigationItemSelectedListener true
                }

                R.id.menu_drawer_settings -> {
                    ctx.closeDrawers()
                    ctx.startActivity(Intent(ctx, GoPrefsActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))
                    return@OnNavigationItemSelectedListener true
                }

                R.id.menu_drawer_tsumego -> {
                    val next = startSGFListForPath(settings.tsumegoPath)

                    if (!unzipSGFifNeeded(next)) {
                        ctx.startActivity(next)
                    }

                    return@OnNavigationItemSelectedListener true
                }

                R.id.menu_drawer_review -> {
                    val next2 = startSGFListForPath(settings.reviewPath)

                    if (!unzipSGFifNeeded(next2)) {
                        ctx.startActivity(next2)
                    }

                    return@OnNavigationItemSelectedListener true
                }

                R.id.menu_drawer_bookmark -> {
                    ctx.startActivity(startSGFListForPath(settings.bookmarkPath))

                    return@OnNavigationItemSelectedListener true
                }


                R.id.menu_drawer_profile -> {
                    ctx.startActivity(Intent(ctx, BaseProfileActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }


                R.id.menu_drawer_beta -> {
                    AXT.at(ctx).startCommonIntent().openUrl("https://play.google.com/apps/testing/org.ligi.gobandroid_hd")
                    return@OnNavigationItemSelectedListener true
                }

                else -> return@OnNavigationItemSelectedListener false
            }

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

        val tsumegoPath = settings.tsumegoPath
        if (!tsumegoPath.isDirectory || settings.isVersionSeen(1)) {
            UnzipSGFsDialog(ctx, intent_after, settings).show()
            return true
        }
        return false

    }

}
