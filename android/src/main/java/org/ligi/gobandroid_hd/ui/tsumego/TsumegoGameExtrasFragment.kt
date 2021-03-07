package org.ligi.gobandroid_hd.ui.tsumego

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import kotlinx.android.synthetic.main.game_extra_tsumego.*
import org.ligi.compat.HtmlCompat
import org.ligi.gobandroid_hd.App
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.model.GameProvider
import org.ligi.gobandroid_hd.ui.go_terminology.GoTerminologyViewActivity

class TsumegoGameExtrasFragment : Fragment() {

    internal val gameProvider: GameProvider  by App.kodein.lazy.instance()


    private var off_path_visible = false
    private var correct_visible = false

    private fun updateUI() {
        if (tsumego_off_path_view == null || tsumego_correct_view == null || activity == null) { // views not yet created
            return  // will come back later
        }

        val game = gameProvider.get()

        requireActivity().runOnUiThread {
            tsumego_off_path_view.visibility = if (off_path_visible) TextView.VISIBLE else TextView.GONE

            if (correct_visible) {
                tsumego_correct_view!!.visibility = View.VISIBLE
                val optionalNextTsumegoURLString = NextTsumegoFileFinder.calcNextTsumego(game.metaData
                        .fileName
                        .replaceFirst("file://".toRegex(), ""))

                if (optionalNextTsumegoURLString != null) {

                    tsumego_correct_view!!.movementMethod = LinkMovementMethod.getInstance()

                    val text = getString(R.string.tsumego_correct) +
                            " <a href='tsumego://" +
                            optionalNextTsumegoURLString +
                            "'>" +
                            getString(R.string.next_tsumego) +
                            "</a>"
                    tsumego_correct_view!!.text = HtmlCompat.fromHtml(text)
                } else {
                    tsumego_correct_view.text = getString(R.string.correct_but_no_more_tsumegos)
                }
            } else {
                tsumego_correct_view.visibility = View.GONE
            }

            // the 10 is a bit of a magic number - just want to show comments that
            // have extras here to prevent double commentView written - but sometimes
            // there is more info in the commentView
            if (!correct_visible && game.actMove.comment.length > 10) {
                game_comment!!.visibility = View.VISIBLE
                game_comment!!.text = game.actMove.comment
                if (!TextUtils.isEmpty(game.actMove.comment)) {
                    GoTerminologyViewActivity.linkifyTextView(game_comment!!)
                }
            } else {
                game_comment!!.visibility = View.GONE
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val res = inflater.inflate(R.layout.game_extra_tsumego, container, false)

        updateUI()
        return res
    }

    fun setOffPathVisibility(visible: Boolean) {
        off_path_visible = visible
        updateUI()
    }

    fun setCorrectVisibility(visible: Boolean) {
        correct_visible = visible
        updateUI()
    }

}
