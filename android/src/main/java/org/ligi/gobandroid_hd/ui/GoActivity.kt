/**
 * gobandroid
 * by Marcus -Ligi- Bueschleb
 * http://ligi.de
 *
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation;
 *
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http:></http:>//www.gnu.org/licenses/>.
 */

package org.ligi.gobandroid_hd.ui

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.print.PrintManager
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.*
import android.view.View.OnKeyListener
import android.view.View.OnTouchListener
import android.widget.Toast
import kotlinx.android.synthetic.main.game.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.ligi.gobandroid_hd.App.Companion.env
import org.ligi.gobandroid_hd.BuildConfig
import org.ligi.gobandroid_hd.InteractionScope
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.R.id.*
import org.ligi.gobandroid_hd.R.layout.game
import org.ligi.gobandroid_hd.events.GameChangedEvent
import org.ligi.gobandroid_hd.events.OptionsItemClickedEvent
import org.ligi.gobandroid_hd.logic.Cell
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.logic.GoGame.MoveStatus.*
import org.ligi.gobandroid_hd.logic.sgf.SGFWriter
import org.ligi.gobandroid_hd.print.GoGamePrintDocumentAdapter
import org.ligi.gobandroid_hd.ui.GoSoundManager.Sound.*
import org.ligi.gobandroid_hd.ui.alerts.GameInfoDialog
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity
import org.ligi.gobandroid_hd.ui.fragments.DefaultGameExtrasFragment
import org.ligi.gobandroid_hd.ui.recording.SaveSGFDialog
import org.ligi.gobandroid_hd.ui.review.BookmarkDialog
import org.ligi.gobandroid_hd.ui.scoring.GameScoringActivity
import org.ligi.gobandroid_hd.ui.share.ShareSGFDialog
import org.ligi.kaxt.disableRotation
import org.ligi.snackengage.SnackEngage
import org.ligi.snackengage.conditions.AfterNumberOfOpportunities
import org.ligi.snackengage.conditions.NeverAgainWhenClickedOnce
import org.ligi.snackengage.conditions.locale.IsOneOfTheseLocales
import org.ligi.snackengage.snacks.RateSnack
import org.ligi.snackengage.snacks.TranslateSnack
import org.ligi.tracedroid.logging.Log
import org.ligi.tracedroid.sending.TraceDroidEmailSender
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*

/**
 * Activity for a Go Game
 */
open class GoActivity : GobandroidFragmentActivity(), OnTouchListener, OnKeyListener {

    var sound_man: GoSoundManager? = null

    private var info_toast: Toast? = null
    private var last_processed_move_change_num = 0

    open val isBoardFocusWanted = true
    open val gameExtraFragment: Fragment = DefaultGameExtrasFragment()
    protected val bus = EventBus.getDefault()


    /**
     * @return true if we want to ask the user - different in modes
     */
    open fun isAsk4QuitEnabled() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.game)


        if (!BuildConfig.DEBUG) {
            // if there where stacktraces collected -> give the user the option to send them
            if (!TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this)) {
                SnackEngage.from(go_board)
                        .withSnack(RateSnack().withConditions(NeverAgainWhenClickedOnce(), AfterNumberOfOpportunities(42)))
                        .withSnack(TranslateSnack("https://www.transifex.com/ligi/gobandroid/").withConditions(AfterNumberOfOpportunities(4),
                                IsOneOfTheseLocales(Locale.KOREA,
                                        Locale.KOREAN),
                                NeverAgainWhenClickedOnce()))
                        .build()
                        .engageWhenAppropriate()
            }
        }

        supportActionBar!!.setHomeButtonEnabled(true)

        disableRotation()

        if (GoPrefs.isConstantLightWanted) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        if (game == null) { // cannot do anything without a game
            Log.w("finish()ing " + this + " cuz getGame()==null")
            finish()
            return
        }

        if (sound_man == null) {
            sound_man = GoSoundManager(this, env)
        }

        val customNav = CustomActionBar(this)

        val fragmentTransAction = supportFragmentManager.beginTransaction()
        fragmentTransAction.add(R.id.game_extra_container, gameExtraFragment).commit()
        supportFragmentManager.executePendingTransactions()


        supportActionBar!!.customView = customNav
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        customNav.isFocusable = false

        createInfoToast()

        setupBoard()

        game2ui()
    }

    @SuppressLint("ShowToast")
    private // this is correct - we do not want to show the toast at this stage
    fun createInfoToast() {
        info_toast = Toast.makeText(this.baseContext, "", Toast.LENGTH_LONG)
    }

    /**
     * find the go board widget and set up some properties
     */
    private fun setupBoard() {
        go_board.setOnTouchListener(this)
        go_board.setOnKeyListener(this)
        go_board.move_stone_mode = false
    }


    /**
     * set some preferences on the go board - intended to be called in onResume
     */
    private fun setBoardPreferences() {
        if (go_board == null) {
            Log.w("setBoardPreferences() called with go_board==null - means setupBoard() was propably not called - skipping to not FC")
            return
        }

        go_board.do_legend = GoPrefs.isLegendEnabled
        go_board.legend_sgf_mode = GoPrefs.isSGFLegendEnabled
        go_board.setLineSize(GoPrefs.boardLineWidth.toFloat())
    }

    override fun onResume() {
        super.onResume()

        if (isBoardFocusWanted) {
            go_board.isFocusableInTouchMode = true
            go_board.isFocusable = true
            go_board.requestFocus()
        } else {
            go_board.isFocusableInTouchMode = false
            go_board.isFocusable = false
        }
        setBoardPreferences()


        bus.register(this)
    }

    override fun doFullScreen() = GoPrefs.isFullscreenEnabled or resources.getBoolean(R.bool.force_fullscreen)

    override fun onCreateOptionsMenu(menu: Menu) = super.onCreateOptionsMenu(menu.apply {
        menuInflater.inflate(R.menu.ingame_common, this)
    })


    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val item = menu.findItem(R.id.menu_game_print)

        if (item != null) {
            item.isVisible = Build.VERSION.SDK_INT >= 19
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        bus.post(OptionsItemClickedEvent(item.itemId))
        when (item.itemId) {

            R.id.menu_game_print -> {
                doPrint()
                return true
            }

            R.id.menu_game_info -> {
                GameInfoDialog(this, game).show()
                return true
            }

            R.id.menu_game_undo -> {
                if (game.canUndo()) {
                    requestUndo()
                    return true
                }
            }

            R.id.menu_game_pass -> {
                game.pass()

                bus.post(GameChangedEvent)

                return true
            }

            R.id.menu_write_sgf -> {
                SaveSGFDialog(this).show()
                return true
            }

            R.id.menu_bookmark -> {
                BookmarkDialog(this).show()
                return true
            }

            R.id.menu_game_share -> {
                //new ShareWithMultipleOptionsDialog(this).show();
                ShareSGFDialog(this).show()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    @TargetApi(19)
    fun doPrint() {
        val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager
        val jobName = getString(R.string.app_name)
        printManager.print(jobName, GoGamePrintDocumentAdapter(this, jobName), null)
    }

    fun switchToCounting() {
        interactionScope.mode = InteractionScope.Mode.COUNT
        startActivity(Intent(this, GameScoringActivity::class.java))
        finish()
    }


    open fun quit(toHome: Boolean) {
        if (!isAsk4QuitEnabled()) {
            finish()
        } else {
            AlertDialog.Builder(this).setTitle(R.string.end_game_quesstion_title)
                    .setMessage(R.string.quit_confirm)
                    .setPositiveButton(R.string.yes) { dialog, whichButton -> finish() }
                    .setCancelable(true)
                    .setNegativeButton(R.string.no, null)
                    .show()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                quit(false)
                return true
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    /**
     * show a the info toast with a specified text from a resource ID
     */
    protected fun showInfoToast(@StringRes resId: Int) {
        info_toast!!.setText(resId)
        info_toast!!.show()
    }

    protected open fun doMoveWithUIFeedback(cell: Cell?): GoGame.MoveStatus {
        if (cell == null) {
            return INVALID_NOT_ON_BOARD
        }

        val res = game.do_move(cell)

        if (res== INVALID_IS_KO || res == INVALID_CELL_NO_LIBERTIES) {
            showInfoToast(getToastForResult(res))
        }

        return res
    }

    @StringRes
    private fun getToastForResult(res: GoGame.MoveStatus) = when (res) {
        INVALID_IS_KO -> R.string.invalid_move_ko
        INVALID_CELL_NO_LIBERTIES -> R.string.invalid_move_no_liberties
        else -> throw RuntimeException("Illegal game result " + res)
    }


    fun game2ui() {
        go_board.postInvalidate()
        refreshZoomFragment()
    }

    protected fun eventForZoomBoard(event: MotionEvent) {
        interactionScope.touchCell = go_board.pixel2cell(event.x, event.y)

        if (!app.isTesting) {
            if (event.action == MotionEvent.ACTION_UP) {
                game_extra_container.visibility = View.VISIBLE
                zoom_board!!.visibility = View.GONE
            } else if (event.action == MotionEvent.ACTION_DOWN) {
                game_extra_container.visibility = View.GONE
                zoom_board!!.visibility = View.VISIBLE
            }
        }
        refreshZoomFragment()
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {

        eventForZoomBoard(event)

        if (event.action == MotionEvent.ACTION_UP) {
            if (resources.getBoolean(R.bool.small)) {
                supportActionBar!!.show()
            }

        } else if (event.action == MotionEvent.ACTION_DOWN) {

            // for very small devices we want to hide the ActionBar to actually
            // see something in the Zoom-Fragment
            if (resources.getBoolean(R.bool.small)) {
                supportActionBar!!.hide()
            }

            if (game.isBlackToMove) {
                sound_man!!.playSound(PICKUP1)
            } else {
                sound_man!!.playSound(PICKUP2)
            }
        }

        doTouch(event)

        return true
    }

    public override fun onPause() {

        go_board.move_stone_mode = false

        if (doAutoSave()) {
            try {
                val f = File(env.SGFSavePath.toString() + "/autosave.sgf")
                f.createNewFile()

                val sgf_writer = FileWriter(f)

                val out = BufferedWriter(sgf_writer)

                out.write(SGFWriter.game2sgf(game))
                out.close()
                sgf_writer.close()

            } catch (e: IOException) {
                Log.i("" + e)
            }

        }
        bus.unregister(this)
        super.onPause()
    }

    open fun doAutoSave()= false

    open fun doTouch(event: MotionEvent) {

        // calculate position on the field by position on the touchscreen

        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> interactionScope.touchCell = go_board.pixel2cell(event.x, event.y)

            MotionEvent.ACTION_OUTSIDE -> interactionScope.touchCell = null

            MotionEvent.ACTION_UP -> {

                if (go_board.move_stone_mode) {
                    // TODO check if this is an illegal move ( e.g. in variants )

                    if (interactionScope.touchCell != null && game.visualBoard.isCellFree(interactionScope.touchCell!!)) {
                        game.repositionActMove(interactionScope.touchCell!!)
                    }
                    go_board.move_stone_mode = false // moving of stone done
                } else if (game.actMove.isOnCell(interactionScope.touchCell)) {
                    initializeStoneMove()
                } else {
                    doMoveWithUIFeedback(interactionScope.touchCell)
                }

                interactionScope.touchCell = null
            }
        }

        bus.post(GameChangedEvent)
    }


    open fun initializeStoneMove() {

        if (go_board.move_stone_mode) { // already in the mode
            return  // -> do nothing
        }

        go_board.move_stone_mode = true

        // TODO check if we only want this in certain modes
        if (GoPrefs.isAnnounceMoveActive) {

            AlertDialog.Builder(this).setMessage(R.string.hint_stone_move).setPositiveButton(R.string.ok

            ) { _, _ -> GoPrefs.isAnnounceMoveActive = false }.show()
        }
    }

    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            val ensuredTouchPosition = interactionScope.touchCell ?: game.statelessGoBoard.getCell(0, 0)
            val boardCell = game.statelessGoBoard.getCell(ensuredTouchPosition)
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_UP -> if (boardCell.up != null) {
                    interactionScope.touchCell = boardCell.up
                } else {
                    return false
                }

                KeyEvent.KEYCODE_DPAD_LEFT -> if (boardCell.left != null) {
                    interactionScope.touchCell = boardCell.left
                } else {
                    return false
                }

                KeyEvent.KEYCODE_DPAD_DOWN -> if (boardCell.down != null) {
                    interactionScope.touchCell = boardCell.down
                } else {
                    return false
                }

                KeyEvent.KEYCODE_DPAD_RIGHT -> if (boardCell.right != null) {
                    interactionScope.touchCell = boardCell.right
                } else {
                    return false
                }

                KeyEvent.KEYCODE_DPAD_CENTER -> doMoveWithUIFeedback(boardCell)

                else -> return false
            }

            go_board.postInvalidate()
            refreshZoomFragment()
            return true
        }
        return false
    }

    fun refreshZoomFragment() {
        zoom_board!!.postInvalidate()
    }

    open fun requestUndo() {

        go_board.move_stone_mode = false

        UndoWithVariationDialog.userInvokedUndo(this, interactionScope, game)
    }

    @Subscribe
    open fun onGameChanged(gameChangedEvent: GameChangedEvent) {
        Log.i("onGoGameChange in GoActivity")
        if (game.actMove.movePos > last_processed_move_change_num) {
            if (game.isBlackToMove) {
                sound_man!!.playSound(PLACE1)
            } else {
                sound_man!!.playSound(PLACE2)
            }
        }
        last_processed_move_change_num = game.actMove.movePos

        game2ui()
    }

    protected fun notifyGoGameChange() {
        EventBus.getDefault().post(GameChangedEvent)
    }
}