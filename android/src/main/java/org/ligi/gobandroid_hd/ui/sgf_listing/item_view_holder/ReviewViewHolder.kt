package org.ligi.gobandroid_hd.ui.sgf_listing.item_view_holder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.sgf_review_game_details_list_item.view.*
import org.ligi.gobandroid_hd.FileEncodeDetector
import org.ligi.gobandroid_hd.logic.MetaDataFormatter
import org.ligi.gobandroid_hd.logic.sgf.SGFReader
import org.ligi.gobandroid_hd.ui.review.SGFMetaData
import org.ligi.gobandroid_hd.ui.sgf_listing.GoLink
import java.io.File
import java.io.IOException

class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), ViewHolderInterface {

    override fun apply(fileToApply: File) {
        var file = fileToApply

        itemView.title.text = file.name.replace(".sgf", "")

        try {
            if (GoLink.isGoLink(file)) {
                val gl = GoLink(file)
                file = File(gl.fileName)
                itemView.game_link_extra_infos.text = "Move #" + gl.moveDepth
            } else {
                itemView.game_link_extra_infos.visibility = View.GONE
            }

            val sgf_str = file.bufferedReader(FileEncodeDetector.detect(file)).readText()
            val game = SGFReader.sgf2game(sgf_str, null, SGFReader.BREAKON_FIRSTMOVE)
            val sgf_meta = SGFMetaData(file.absolutePath)

            if (game != null) {
                val metaFormatter = MetaDataFormatter(game)

                if (metaFormatter.getWhitePlayerString().isEmpty()) {
                    itemView.player_white_stone_img.visibility = View.GONE
                    itemView.player_white.visibility = View.GONE
                } else {
                    itemView.player_white.text = metaFormatter.getWhitePlayerString()
                }

                if (metaFormatter.getBlackPlayerString().isEmpty()) {
                    itemView.player_black_stone_img.visibility = View.GONE
                    itemView.player_black.visibility = View.GONE
                } else {
                    itemView.player_black.text = metaFormatter.getBlackPlayerString()
                }

                itemView.game_extra_infos.text = metaFormatter.extrasString

                if (!sgf_meta.hasData()) {
                    itemView.game_rating.visibility = View.GONE
                } else if (sgf_meta.rating != null) {
                    itemView.game_rating.visibility = View.VISIBLE
                    itemView.game_rating.rating = .5f * sgf_meta.rating!!
                }
            }

        } catch (e: IOException) {
        }

    }


}
