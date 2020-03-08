package org.ligi.gobandroid_hd.ui.scoring

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.game_result.view.*
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.events.GameChangedEvent
import org.ligi.gobandroid_hd.logic.GoGameScorer
import org.ligi.gobandroid_hd.ui.fragments.GobandroidGameAwareFragment

class GameScoringExtrasFragment : GobandroidGameAwareFragment() {

    lateinit var myView: View

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        myView = inflater.inflate(R.layout.game_result, container, false)
        refresh()
        return myView
    }

    override fun onGoGameChanged(gameChangedEvent: GameChangedEvent?) {
        super.onGoGameChanged(gameChangedEvent)
        requireActivity().runOnUiThread { refresh() }
    }

    private fun getCapturesString(captures: Int, deadStones: Int): String {
        val result = Integer.toString(captures)

        if (deadStones > 0) {
            return result + " + " + deadStones
        }

        return result
    }

    fun refresh() {
        if (activity == null) {
            return
        }
        val game = gameProvider.get()
        val scorer = game.scorer ?: return

        myView.result_txt.text = getFinTXT(scorer)

        myView.territory_black.text = String.format("%d", scorer.territory_black)
        myView.territory_white.text = String.format("%d", scorer.territory_white)

        myView.captures_black.text = getCapturesString(game.capturesBlack, scorer.dead_white)
        myView.captures_white.text = getCapturesString(game.capturesWhite, scorer.dead_black)

        myView.komi.text = String.format("%.1f", game.komi)

        myView.final_black.text = String.format("%.1f", scorer.pointsBlack)
        myView.final_white.text = String.format("%.1f", scorer.pointsWhite)
    }

    private fun getFinTXT(scorer: GoGameScorer): String {
        if (scorer.pointsBlack > scorer.pointsWhite) {
            val finalPoints = scorer.pointsBlack - scorer.pointsWhite
            return getString(R.string.black_won_with) + String.format("%.1f", finalPoints) + getString(R.string._points_)
        }

        if (scorer.pointsWhite > scorer.pointsBlack) {
            val finalPoints = scorer.pointsWhite - scorer.pointsBlack
            return getString(R.string.white_won_with_) + String.format("%.1f", finalPoints) + getString(R.string._points_)
        }
        return resources.getString(R.string.game_ended_in_draw)
    }
}
