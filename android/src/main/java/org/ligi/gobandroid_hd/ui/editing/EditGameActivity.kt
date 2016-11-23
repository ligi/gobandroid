package org.ligi.gobandroid_hd.ui.editing

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.WindowManager
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.logic.Cell
import org.ligi.gobandroid_hd.logic.GoDefinitions.*
import org.ligi.gobandroid_hd.logic.GoGame.MoveStatus
import org.ligi.gobandroid_hd.logic.markers.*
import org.ligi.gobandroid_hd.logic.markers.functions.findFirstFreeNumber
import org.ligi.gobandroid_hd.logic.markers.functions.findNextLetter
import org.ligi.gobandroid_hd.ui.GoActivity
import org.ligi.gobandroid_hd.ui.editing.model.EditGameMode

/**
 * Activity to edit a Game
 */
class EditGameActivity : GoActivity() {

    val statefulEditModeItems = StatefulEditModeItems()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO the next line works but needs investigation - i thought more of
        // getBoard().requestFocus(); - but that was not working ..
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    override fun doAutoSave()= true

    override fun doMoveWithUIFeedback(cell: Cell?): MoveStatus {
        if (cell == null) {
            return MoveStatus.INVALID_NOT_ON_BOARD
        }
        when (mode) {
            EditGameMode.PLAY ->
                super.doMoveWithUIFeedback(cell)

            EditGameMode.BLACK ->
                setOrRemoveStone(cell, STONE_BLACK)

            EditGameMode.WHITE ->
                setOrRemoveStone(cell, STONE_WHITE)

            EditGameMode.TRIANGLE ->
                setOrRemoveMarker(cell, { TriangleMarker(cell) })

            EditGameMode.SQUARE ->
                setOrRemoveMarker(cell, { SquareMarker(cell) })

            EditGameMode.CIRCLE ->
                setOrRemoveMarker(cell, { CircleMarker(cell) })

            EditGameMode.NUMBER ->
                setOrRemoveMarker(cell, {
                    TextMarker(cell, game.actMove.markers.findFirstFreeNumber().toString())
                })

            EditGameMode.LETTER ->
                setOrRemoveMarker(cell, {
                    TextMarker(cell, game.actMove.markers.findNextLetter())
                })
        }
        return MoveStatus.VALID
    }

    private fun setOrRemoveMarker(cell: Cell, marker: () -> GoMarker): Boolean {
        val markers = game.actMove.markers

        if (markers.removeAll { it.isInCell(cell) }) {
            return true
        }

        markers.add(marker())
        return false
    }

    private fun setOrRemoveStone(cell: Cell, kind: Byte) {
        val newKind = if (game.handicapBoard.isCellKind(cell, kind)) STONE_NONE else kind
        game.handicapBoard.setCell(cell, newKind)
        game.jump(game.actMove) // we need to totally refresh the board
    }

    private val mode: EditGameMode
        get() = statefulEditModeItems.mode

    override val gameExtraFragment: Fragment
        get() = EditGameExtrasFragment()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.ingame_edit, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
