package org.ligi.gobandroid_hd.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.comment_textview.*
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.events.GameChangedEvent
import org.ligi.gobandroid_hd.ui.go_terminology.GoTerminologyViewActivity

class CommentAndNowPlayingFragment : GobandroidGameAwareFragment() {

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val res = inflater.inflate(R.layout.game_extra_review, container, false)
        onGoGameChanged(null)
        return res
    }

    override fun onGoGameChanged(gameChangedEvent: GameChangedEvent?) {
        super.onGoGameChanged(gameChangedEvent)
        activity.runOnUiThread {
            comments_textview?.let {
                it.text = game.actMove.comment
                GoTerminologyViewActivity.linkifyTextView(it)
            }
        }
    }

}
