package org.ligi.gobandroid_hd.uitest

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.squareup.spoon.Spoon
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.ligi.gobandroid_hd.App
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.logic.CellImpl
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.logic.markers.GoMarker
import org.ligi.gobandroid_hd.logic.markers.SquareMarker
import org.ligi.gobandroid_hd.logic.markers.TextMarker
import org.ligi.gobandroid_hd.logic.markers.TriangleMarker
import org.ligi.gobandroid_hd.model.GameProvider
import org.ligi.gobandroid_hd.test_helper_functions.getAllCellsForRect
import org.ligi.gobandroid_hd.test_helper_functions.tapStone
import org.ligi.gobandroid_hd.ui.editing.EditGameActivity
import org.ligi.trulesk.TruleskActivityRule

class TheEditGameActivity {

    @get:Rule
    val rule = TruleskActivityRule(EditGameActivity::class.java, false)

    val gameProvider: GameProvider by App.kodein.lazy.instance()

    @Test
    fun testThatGoBoardIsThere() {
        rule.launchActivity(null)

        onView(withId(R.id.go_board)).check(matches(isDisplayed()))
        rule.screenShot("edit")
    }

    @Test
    fun testThatLettersWork() {
        gameProvider.set(GoGame(9))

        val activity = rule.launchActivity(null)

        onView(withContentDescription(activity.getString(R.string.letter))).perform(click())

        tap9x3Field()

        Spoon.screenshot(activity, "letters")
    }

    private fun tap9x3Field() {
        for (cell in getAllCellsForRect(9, 3)) {
            onView(withId(R.id.go_board)).perform(tapStone(cell, gameProvider.get()))
        }
    }

    @Test
    fun testThatNumbersWork() {
        gameProvider.set(GoGame(9))

        val activity = rule.launchActivity(null)

        onView(withContentDescription(activity.getString(R.string.number))).perform(click())

        tap9x3Field()

        rule.screenShot("numbers")

        for (cell in getAllCellsForRect(9, 3)) {
            assertThat(gameProvider.get().actMove.markers).contains(TextMarker(cell, "" + (3 * cell.y + cell.x + 1)))
        }
    }


    @Test
    fun testThatSquareWorks() {
        gameProvider.set(GoGame(9))

        val activity = rule.launchActivity(null)

        onView(withContentDescription(activity.getString(R.string.square))).perform(click())

        val cell = CellImpl(1, 2)
        onView(withId(R.id.go_board)).perform(tapStone(cell, gameProvider.get()))

        assertThat<GoMarker>(gameProvider.get().actMove.markers).contains(SquareMarker(cell))

        rule.screenShot("square")
    }


    @Test
    fun testThatTriangleWorks() {
        gameProvider.set(GoGame(9))

        val activity = rule.launchActivity(null)

        onView(withContentDescription(activity.getString(R.string.triangle))).perform(click())

        onView(withId(R.id.go_board)).perform(tapStone(CellImpl(1, 2), gameProvider.get()))

        assertThat<GoMarker>(gameProvider.get().actMove.markers).contains(TriangleMarker(CellImpl(1, 2)))

        rule.screenShot("triangle")
    }
}
