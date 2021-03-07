/**

 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation;

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http:></http:>//www.gnu.org/licenses/>.

 */

package org.ligi.gobandroid_hd.ui.review

import kotlinx.android.synthetic.main.end_review_dialog.view.*
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.ui.GobandroidDialog
import org.ligi.gobandroid_hd.ui.GobandroidNotifications
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity
import org.ligi.gobandroid_hd.ui.sgf_listing.GoLink

/**
 * Dialog to show when user wants to set a BookMark

 * @author [Marcus -Ligi- Bueschleb](http://ligi.de)
 * *
 *
 *
 * *         License: This software is licensed with GPLv3
 */
class EndReviewDialog(context: GobandroidFragmentActivity) : GobandroidDialog(context) {

    private val meta by lazy { SGFMetaData(gameProvider.get()) }

    init {
        setContentView(R.layout.end_review_dialog)

        setTitle(R.string.end_review)
        setIconResource(R.drawable.ic_action_help_outline)

        container.save_bookmark_cp.isChecked = true

        container.bookmark_name_et.setText(BookmarkDialog.getCleanEnsuredFilename(settings, gameProvider.get()))

        if (meta.rating != null) {
            container.game_rating.rating = .5f * meta.rating!!
        }

        container.save_bookmark_cp.setOnCheckedChangeListener { _, isChecked ->
            container.bookmark_notification_cb.isEnabled = isChecked
            container.bookmark_name_et.isEnabled = isChecked
        }

        setPositiveButton(R.string.end_review_ok_button, { dialog ->

            if (container.save_bookmark_cp.isChecked) {
                GoLink.saveGameToGoLink(gameProvider.get(), settings.bookmarkPath, container.bookmark_name_et.text.toString() + ".golink")
            }

            if (container.bookmark_notification_cb.isChecked) {
                GobandroidNotifications(context).addGoLinkNotification(settings.bookmarkPath.toString() + "/" + container.bookmark_name_et.text.toString() + ".golink")
            }

            saveSGFMeta()
            dialog.dismiss()
            context.finish()
        })

        setNegativeButton(R.string.end_review_stay_button, { dialog ->
            dialog.dismiss()
            saveSGFMeta()
        })
    }

    fun saveSGFMeta() {
        meta.rating = (container.game_rating.rating * 2).toInt()
        meta.persist()
    }
}
