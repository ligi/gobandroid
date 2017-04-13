package org.ligi.gobandroid_hd.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.events.GameChangedEvent
import org.ligi.gobandroid_hd.ui.go_terminology.GoTerminologyViewActivity

class NavigationAndCommentFragment : GobandroidGameAwareFragment() {


    private val gameChangeHandler = Handler()
    private var myTextView: TextView? = null

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val res = inflater.inflate(R.layout.game_extra_review, container, false)

        myTextView = res.findViewById(R.id.comments_textview) as TextView
        myTextView!!.isFocusable = false

        res.findViewById(R.id.scrollview).isFocusable = false

        onGoGameChanged(null)
        return res
    }

    override fun onGoGameChanged(gameChangedEvent: GameChangedEvent?) {
        super.onGoGameChanged(gameChangedEvent)
        gameChangeHandler.post {
            if (myTextView != null) {
                myTextView!!.text = game.actMove.comment
                GoTerminologyViewActivity.linkifyTextView(myTextView!!)
            }
        }
    }

}
