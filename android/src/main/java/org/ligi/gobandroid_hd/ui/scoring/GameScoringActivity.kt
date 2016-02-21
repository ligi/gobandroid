package org.ligi.gobandroid_hd.ui.scoring

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.WindowManager
import org.ligi.axt.AXT
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.logic.Cell
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.logic.StatelessBoardCell
import org.ligi.gobandroid_hd.logic.cell_gatherer.LooseConnectedCellGatherer
import org.ligi.gobandroid_hd.logic.cell_gatherer.MustBeConnectedCellGatherer
import org.ligi.gobandroid_hd.ui.GoActivity
import org.ligi.gobandroid_hd.ui.gnugo.PlayAgainstGnuGoActivity
import org.ligi.gobandroid_hd.ui.recording.GameRecordActivity
import java.util.*

/**
 * Activity to score a Game
 */
class GameScoringActivity : GoActivity() {

    private var gameScoringExtrasFragment: GameScoringExtrasFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO the next line works but needs investigation - i thought more of
        // getBoard().requestFocus(); - but that was not working ..
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        for (boardCells in inclusiveGroups) {
            // TODO find a nicer approach to detect dead stones - this does only cover the common cases
            if (boardCells.size <= 4) {
                for (boardCell in boardCells) {
                    game.calcBoard.toggleCellDead(boardCell)
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        game.initScorer()
        if (gameScoringExtrasFragment != null) {
            gameScoringExtrasFragment!!.refresh()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menuInflater.inflate(R.menu.ingame_score, menu)
        return super.onCreateOptionsMenu(menu)
    }


    private val inclusiveGroups: Set<Set<StatelessBoardCell>>
        get() {
            val allProcessed = HashSet<Cell>()
            val allGroups = HashSet<Set<StatelessBoardCell>>()

            game.calcBoard.statelessGoBoard.withAllCells {
                val cell = game.calcBoard.statelessGoBoard.getCell(it)
                val inGroup = LooseConnectedCellGatherer(game.calcBoard, cell)
                allProcessed.addAll(inGroup)
                if (!game.calcBoard.isCellFree(it)) {
                    allGroups.add(inGroup)
                }
            }

            return allGroups
        }

    override fun doTouch(event: MotionEvent) {
        //super.doTouch(event); - Do not call! Not needed and breaks marking dead stones

        eventForZoomBoard(event)
        val touchCell = board.pixel2cell(event.x, event.y)
        interactionScope.touchCell = touchCell

        // calculate position on the field by position on the touchscreen

        if (event.action == MotionEvent.ACTION_UP && touchCell != null) {
            doMoveWithUIFeedback(touchCell)
            interactionScope.touchCell = null
        }

    }

    override fun doMoveWithUIFeedback(cell: Cell): GoGame.MoveStatus {
        do_score_touch(cell)
        game.notifyGameChange()
        return GoGame.MoveStatus.VALID
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_game_again -> {
                val metaData = game.metaData
                gameProvider.set(GoGame(game.size))
                if (metaData.blackName.toLowerCase() == "gnugo" || metaData.whiteName.toLowerCase() == "gnugo") {
                    AXT.at(this).startCommonIntent().activityFromClass(PlayAgainstGnuGoActivity::class.java)
                } else {

                    AXT.at(this).startCommonIntent().activityFromClass(GameRecordActivity::class.java)
                }

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        // if we go back to other modes we want to have them alive again ( Zombies ?)
        game.calcBoard.statelessGoBoard.withAllCells {
            if (game.calcBoard.isCellDead(it)) {
                game.calcBoard.toggleCellDead(it)
            }
        }

        game.copyVisualBoard()
        game.removeScorer()
    }

    override fun getGameExtraFragment(): GameScoringExtrasFragment? {
        if (gameScoringExtrasFragment == null) {
            gameScoringExtrasFragment = GameScoringExtrasFragment()
        }
        return gameScoringExtrasFragment
    }


    fun do_score_touch(cell: Cell) {

        val calcBoard = game.calcBoard
        if (!calcBoard.isCellFree(cell) || calcBoard.isCellDead(cell)) {
            // if there is a stone/cellGathering
            val cellGathering = MustBeConnectedCellGatherer(calcBoard, calcBoard.statelessGoBoard.getCell(cell))
            for (groupCell in cellGathering) {
                calcBoard.toggleCellDead(groupCell)
            }
        }

        if (game.scorer != null) {
            game.scorer!!.calculateScore()
        }
    }
}
