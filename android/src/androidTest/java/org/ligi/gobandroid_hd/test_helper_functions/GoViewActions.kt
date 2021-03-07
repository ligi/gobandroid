package org.ligi.gobandroid_hd.test_helper_functions

import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.CoordinatesProvider
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Tap
import androidx.test.espresso.matcher.ViewMatchers
import android.view.View
import android.widget.SeekBar

import org.hamcrest.Matcher
import org.ligi.gobandroid_hd.logic.Cell
import org.ligi.gobandroid_hd.logic.GoGame


fun placeStone(cell: Cell, game: GoGame): ViewAction {
    return GeneralClickAction(
            Tap.SINGLE,
            CoordinatesProvider { view ->
                val screenPos = IntArray(2)
                view.getLocationOnScreen(screenPos)
                val gameSize = game.size

                val screenX = screenPos[0] + (0.5f + cell.x) * (view.width / gameSize)
                val screenY = screenPos[1] + (0.5f + cell.y) * (view.height / gameSize)

                floatArrayOf(screenX, screenY)
            },
            Press.FINGER)
}

fun tapStone(cell: Cell, game: GoGame): ViewAction {
    return placeStone(cell, game)
}

fun setProgress(progress: Int): ViewAction {
    return object : ViewAction {
        override fun perform(uiController: UiController, view: View) {
            val seekBar = view as SeekBar
            seekBar.progress = progress
        }

        override fun getDescription(): String {
            return "Set a progress on a SeekBar"
        }

        override fun getConstraints(): Matcher<View> {
            return ViewMatchers.isAssignableFrom(SeekBar::class.java)
        }
    }
}
