/**
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

package org.ligi.gobandroid_hd.ui.review

import android.text.TextUtils
import kotlinx.android.synthetic.main.save_bookmark.view.*
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.logic.sgf.SGFWriter
import org.ligi.gobandroid_hd.ui.GobandroidDialog
import org.ligi.gobandroid_hd.ui.application.GoAndroidEnvironment
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity
import org.ligi.gobandroid_hd.ui.sgf_listing.GoLink
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Dialog to show when user wants to set a BookMark

 * @author [Marcus -Ligi- Bueschleb](http://ligi.de)
 * *
 *
 *
 * *         License: This software is licensed with GPLv3
 */
class BookmarkDialog(context: GobandroidFragmentActivity) : GobandroidDialog(context) {

    init {

        setTitle(R.string.bookmark)
        setIconResource(R.drawable.ic_toggle_star_border)
        setContentView(R.layout.save_bookmark)

        val innerFileName = getCleanEnsuredFilename(settings, gameProvider.get())

        container.message.text = context.resources.getString(R.string.bookmark_to_write_into) + " " + settings.bookmarkPath
        container.bookmark_name.setText(innerFileName)

        setPositiveButton(android.R.string.ok, {
            GoLink.saveGameToGoLink(gameProvider.get(), settings.bookmarkPath, container.bookmark_name.text.toString() + ".golink")
            it.dismiss()
        })
    }

    companion object {

        /**
         * sometimes for saving bookmarks we need an ensured saved file e.g. when
         * coming directly from recording - this should ensure this

         * @return the ensured Filename
         */
        private fun getEnsuredFilename(settings: GoAndroidEnvironment, game: GoGame): String {

            var fname = game.metaData.fileName
            if (TextUtils.isEmpty(fname)) {
                // was not saved before - do it now ( needed for a bookmark )

                fname = defaultFilename
                SGFWriter.saveSGF(game, File(settings.SGFSavePath, "autosave/" + fname))
            }

            return fname
        }

        fun getCleanEnsuredFilename(settings: GoAndroidEnvironment, goGame: GoGame): String {
            val path_components = getEnsuredFilename(settings, goGame).split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return path_components[path_components.size - 1].replace(".sgf", "")
        }

        private val defaultFilename: String
            get() {
                val date_formatter = SimpleDateFormat("dd.MMM.yyyy_HH_mm_ss")
                return date_formatter.format(Date()) + ".sgf"
            }
    }

}
