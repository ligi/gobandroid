/**
 * gobandroid
 * by Marcus -Ligi- Bueschleb
 * http://ligi.de
 *
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation;
 *
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http:></http:>//www.gnu.org/licenses/>.
 */

package org.ligi.gobandroid_hd.ui.sgf_listing

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import org.ligi.gobandroid_hd.InteractionScope
import org.ligi.gobandroid_hd.InteractionScope.Mode.REVIEW
import org.ligi.gobandroid_hd.InteractionScope.Mode.TSUMEGO
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.ui.GobandroidNotifications
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity
import org.ligi.gobandroid_hd.ui.tsumego.fetch.DownloadProblemsDialog
import java.io.File

/**
 * Activity to load SGF's from SD card
 */

class SGFFileSystemListActivity : GobandroidFragmentActivity() {

    private var list_fragment: SGFListFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.list)

        if (intent.getBooleanExtra(GobandroidNotifications.BOOL_FROM_NOTIFICATION_EXTRA_KEY, false)) {
            GobandroidNotifications(this).cancelNewTsumegosNotification()
        }

        val sgfPath = sgfPath
        val sgfPathString = sgfPath.absolutePath

        if (sgfPathString.substring(sgfPathString.indexOf('/')).startsWith(env.tsumegoPath.absolutePath.substring(sgfPathString.indexOf('/')))) {
            interactionScope.mode = InteractionScope.Mode.TSUMEGO
        }

        if (sgfPathString.substring(sgfPathString.indexOf('/')).startsWith(env.reviewPath.absolutePath.substring(sgfPathString.indexOf('/')))) {
            interactionScope.mode = REVIEW
        }


        setActionbarProperties(sgfPath)

        list_fragment = SGFListFragment.newInstance(sgfPath)
        supportFragmentManager.beginTransaction().replace(R.id.list_fragment, list_fragment).commit()
    }

    private val sgfPath: File
        get() {
            if (intent.data != null) {
                return File(intent.data.path)
            }

            return env.SGFBasePath
        }

    private fun setActionbarProperties(dir: File) {
        when (interactionScope.mode) {
            TSUMEGO -> setTitle(R.string.load_tsumego)
            REVIEW -> setTitle(R.string.load_game)
            else -> {
                // we can only show stuff for tsumego and review - if in doubt -
                // trade as review
                interactionScope.mode = REVIEW
                setTitle(R.string.load_game)
            }
        }

        if (supportActionBar != null) {
            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            supportActionBar!!.subtitle = dir.absolutePath
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (interactionScope.mode === TSUMEGO) {
            menuInflater.inflate(R.menu.refresh_tsumego, menu)
        }

        if (interactionScope.mode === REVIEW) {
            menuInflater.inflate(R.menu.review_menu, menu)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_refresh -> {
                DownloadProblemsDialog(this, list_fragment).show()

                return true
            }
            R.id.menu_del_sgfmeta -> {
                list_fragment!!.delete_sgfmeta()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}
