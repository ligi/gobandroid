package org.ligi.gobandroid_hd.ui.sgf_listing.item_view_holder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.sgf_tsumego_list_item.view.*
import org.ligi.gobandroid_hd.FileEncodeDetector
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.logic.sgf.SGFReader
import org.ligi.gobandroid_hd.ui.review.SGFMetaData
import org.ligi.gobandroid_hd.ui.sgf_listing.GoLink
import org.ligi.gobandroid_hd.ui.tsumego.TsumegoHelper
import java.io.File
import java.io.IOException

class TsumegoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), ViewHolderInterface {

    override fun apply(file: File) {

        itemView.title.text = file.name.replace(".sgf", "")

        var sgf_str = ""

        if (GoLink.isGoLink(file)) {
            val gl = GoLink(file)
            sgf_str = gl.sgfString
        } else {
            try {
                sgf_str = file.bufferedReader(FileEncodeDetector.detect(file)).readText()
            } catch (e: IOException) {
            }

        }

        val hints_used_fmt = itemView.hints_tv.context.getString(R.string.hints_used)
        var game: GoGame? = SGFReader.sgf2game(sgf_str, null, SGFReader.BREAKON_FIRSTMOVE)

        if (game != null) {
            val meta = SGFMetaData(file.absolutePath)

            itemView.hints_tv.text = String.format(hints_used_fmt, meta.hintsUsed)

            val transform = TsumegoHelper.calcTransform(game)

            if (transform != SGFReader.DEFAULT_SGF_TRANSFORM) {
                game = SGFReader.sgf2game(sgf_str, null, SGFReader.BREAKON_FIRSTMOVE, transform)
            }

            game!!.jump(game.findFirstMove())
            itemView.previewView?.game = game

        }

        if (SGFMetaData(file.absolutePath).isSolved) {
            itemView.solve_status_image.setImageResource(R.drawable.ic_social_school)
        } else {
            itemView.solve_status_image.setImageResource(R.drawable.ic_action_extension)
        }
    }
}
