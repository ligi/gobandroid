package org.ligi.gobandroid_hd.ui.tsumego

import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.game.*
import org.ligi.gobandroid_hd.App
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.events.GameChangedEvent
import org.ligi.gobandroid_hd.events.TsumegoSolved
import org.ligi.gobandroid_hd.logic.Cell
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.logic.GoMove
import org.ligi.gobandroid_hd.ui.GoActivity
import org.ligi.gobandroid_hd.ui.review.SGFMetaData
import org.ligi.tracedroid.logging.Log
import java.util.*

class TsumegoActivity : GoActivity() {

    private var finishing_move: GoMove? = null
    //private var myTsumegoExtrasFragment: TsumegoGameExtrasFragment? = null
    private var on_path_moves: MutableList<GoMove>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setTitle(R.string.tsumego)
    }

    private val finishingMove by lazy { getCorrectMove(game.findFirstMove()) }

    private fun isFinishingMoveKnown() = finishingMove != null

    private fun recursive_add_on_path_moves(act: GoMove) {
        if (on_path_moves == null) {
            on_path_moves = ArrayList<GoMove>()
        }
        on_path_moves!!.add(act)
        if (act.hasNextMove()) {
            for (child in act.nextMoveVariations)
                recursive_add_on_path_moves(child)
        }
    }

    private // build a on path List to do a fast isOnPath() later
    val isOnPath: Boolean
        get() {
            if (on_path_moves == null) {
                Log.i("isOnPath null")
                recursive_add_on_path_moves(game.findFirstMove())
            }
            Log.i("isOnPath null" + on_path_moves!!.contains(game.actMove) + " " + on_path_moves!!.size)
            return on_path_moves!!.contains(game.actMove)
        }

    public override fun doMoveWithUIFeedback(cell: Cell?): GoGame.MoveStatus {

        val res = super.doMoveWithUIFeedback(cell)

        // if the move was valid and we have a counter move -> we will play it
        if (res === GoGame.MoveStatus.VALID) {
            if (game.actMove.hasNextMove()) {
                game.jump(game.actMove.getnextMove(0))
            }
        }

        bus.post(GameChangedEvent)
        return res
    }

    private fun getCorrectMove(act_mve: GoMove): GoMove? {
        if (isCorrectMove(act_mve)) {
            return act_mve
        }

        return act_mve.nextMoveVariations
                .map { getCorrectMove(it) }
                .firstOrNull { it != null }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        if (game == null) { // there was no game - fallback to main menu
            App.getTracker().trackException("tsumego start getGame() returned null in onCreate", false)
            finish()
            // startActivity(new Intent(this, gobandroid.class));
            return super.onCreateOptionsMenu(menu)
        }

        this.menuInflater.inflate(R.menu.ingame_tsumego, menu)
        menu.findItem(R.id.menu_game_hint).isVisible = isFinishingMoveKnown() && isOnPath
        menu.findItem(R.id.menu_game_undo).isVisible = !game.actMove.isFirstMove
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (!super.onOptionsItemSelected(item)) {
            when (item.itemId) {

                R.id.menu_game_hint ->

                    TsumegoHintAlert.show(this, finishingMove)
            }
        }

        return false
    }

    override fun initializeStoneMove() {
        // disable stone move - not wanted in tsumego
    }

    override fun requestUndo() {
        go_board.move_stone_mode = false
        // we do not want to keep user-variations in tsumego mode- but we want
        // to keep tsumego variation
        game.undo(isOnPath)

        // remove the counter-move if any
        if (!game.isBlackToMove) {
            game.undo(isOnPath)
        }
    }

    override val gameExtraFragment: Fragment by lazy {
        TsumegoGameExtrasFragment()
    }

    private fun isCorrectMove(move: GoMove): Boolean {
        return move.comment.trim { it <= ' ' }.toUpperCase().startsWith("CORRECT") || // gogameguru style
                move.comment.toUpperCase().contains("RIGHT")
    }

    override fun onResume() {
        super.onResume()

        finishing_move = null

        if (game == null) { // there was no game - fallback to main menu
            App.getTracker().trackException("tsumego start getGame() returned null in onCreate", false)
            finish()
            // startActivity(new Intent(this, gobandroid.class));
            return
        }

        recursive_add_on_path_moves(game.findFirstMove())

        // try to find the correct solution
        if (!isFinishingMoveKnown()) {
            AlertDialog.Builder(this).setMessage(R.string.tsumego_sgf_no_solution)
                    .setNegativeButton(R.string.ok, null)
                    .setPositiveButton(R.string.go_back, { dialogInterface: DialogInterface, i: Int ->
                        dialogInterface.dismiss()
                        finish()
                    }).show()
        }

        val myZoom = TsumegoHelper.calcZoom(game, true)

        go_board.zoom = myZoom
        go_board.setZoomPOI(TsumegoHelper.calcPOI(game, true))
        onGameChanged(GameChangedEvent)

        //NaDraChg getSlidingMenu().showContent();
    }

    private var last_move: GoMove? = null

    override fun onGameChanged(gameChangedEvent: GameChangedEvent) {
        super.onGameChanged(gameChangedEvent)
        if (game.actMove == last_move) {
            // TODO find the real cause why we got here to often for the same move
            // mainly a problem for writing the moments - otherwise too many moments where written for the same
            // solved tsumego

            return
        }
        last_move = game.actMove

        (gameExtraFragment as TsumegoGameExtrasFragment).setOffPathVisibility(!isOnPath)
        (gameExtraFragment as TsumegoGameExtrasFragment).setCorrectVisibility(isCorrectMove(game.actMove))

        if (isCorrectMove(game.actMove)) {
            val meta = SGFMetaData(game.metaData.fileName)
            meta.isSolved = true
            meta.persist()

            bus.post(TsumegoSolved(game))
        }
        this.runOnUiThread { supportInvalidateOptionsMenu() }
    }

    override val isAsk4QuitEnabled: Boolean
        get() = false

}
