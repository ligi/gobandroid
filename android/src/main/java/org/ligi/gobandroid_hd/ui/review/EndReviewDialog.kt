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

import android.widget.CheckBox
import android.widget.EditText
import android.widget.RatingBar
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

    private val bookmark_notification: CheckBox
    private val save_bookmark_cp: CheckBox
    private val bookmark_name: EditText
    private val rating_bar: RatingBar
    private val meta: SGFMetaData

    init {

        meta = SGFMetaData(gameProvider.get())
        setContentView(R.layout.end_review_dialog)

        setTitle(R.string.end_review)
        setIconResource(R.drawable.ic_action_help_outline)

        save_bookmark_cp = findViewById(R.id.save_bookmark_cp) as CheckBox
        save_bookmark_cp.isChecked = true

        bookmark_notification = findViewById(R.id.bookmark_notification_cb) as CheckBox
        bookmark_name = findViewById(R.id.bookmark_name_et) as EditText
        bookmark_name.setText(BookmarkDialog.getCleanEnsuredFilename(settings, gameProvider.get()))

        rating_bar = findViewById(R.id.game_rating) as RatingBar

        if (meta.rating != null) {
            rating_bar.rating = .5f * meta.rating!!
        }

        save_bookmark_cp.setOnCheckedChangeListener { buttonView, isChecked ->
            bookmark_notification.isEnabled = isChecked
            bookmark_name.isEnabled = isChecked
        }

        setPositiveButton(R.string.end_review_ok_button, { dialog ->

            if (save_bookmark_cp.isChecked) {
                GoLink.saveGameToGoLink(gameProvider.get(), settings.bookmarkPath, bookmark_name.text.toString() + ".golink")
            }

            if (bookmark_notification.isChecked) {
                GobandroidNotifications(context).addGoLinkNotification(settings.bookmarkPath.toString() + "/" + bookmark_name.text.toString() + ".golink")
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
        meta.rating = (rating_bar.rating * 2).toInt()
        meta.persist()
    }
}
