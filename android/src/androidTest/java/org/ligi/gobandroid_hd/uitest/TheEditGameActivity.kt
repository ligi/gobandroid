package org.ligi.gobandroid_hd.uitest

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import com.squareup.spoon.Spoon
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.TestApp
import org.ligi.gobandroid_hd.base.tapStone
import org.ligi.gobandroid_hd.logic.CellImpl
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.logic.markers.GoMarker
import org.ligi.gobandroid_hd.logic.markers.SquareMarker
import org.ligi.gobandroid_hd.logic.markers.TextMarker
import org.ligi.gobandroid_hd.logic.markers.TriangleMarker
import org.ligi.gobandroid_hd.model.GameProvider
import org.ligi.gobandroid_hd.ui.editing.EditGameActivity
import org.ligi.gobandroidhd.helper.CellFactory
import org.ligi.trulesk.TruleskActivityRule
import javax.inject.Inject


@RunWith(AndroidJUnit4::class)
class TheEditGameActivity {

    @get:Rule
    val rule = TruleskActivityRule(EditGameActivity::class.java, false)

    @Inject
    lateinit var gameProvider: GameProvider

    @Before
    fun setUp() {
        TestApp.component().inject(this)
    }

    @Test
    fun testThatGoBoardIsThere() {
        val activity = rule.launchActivity(null)

        onView(withId(R.id.go_board)).check(matches(isDisplayed()))
        Spoon.screenshot(activity, "edit")
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
        for (cell in CellFactory.getAllCellsForRect(9, 3)) {
            onView(withId(R.id.go_board)).perform(tapStone(cell, gameProvider.get()))
        }
    }

    @Test
    fun testThatNumbersWork() {
        gameProvider.set(GoGame(9))

        val activity = rule.launchActivity(null)

        onView(withContentDescription(activity.getString(R.string.number))).perform(click())

        tap9x3Field()

        for (cell in CellFactory.getAllCellsForRect(9, 3)) {
            assertThat<GoMarker>(gameProvider.get().actMove.markers).contains(TextMarker(cell, "" + (9 * cell.y + cell.x + 1)))
        }

        Spoon.screenshot(activity, "numbers")
    }


    @Test
    fun testThatSquareWorks() {
        gameProvider.set(GoGame(9))

        val activity = rule.launchActivity(null)

        onView(withContentDescription(activity.getString(R.string.square))).perform(click())

        val cell = CellImpl(1, 2)
        onView(withId(R.id.go_board)).perform(tapStone(cell, gameProvider.get()))

        assertThat<GoMarker>(gameProvider.get().actMove.markers).contains(SquareMarker(cell))

        Spoon.screenshot(activity, "square")
    }


    @Test
    fun testThatTriangleWorks() {
        gameProvider.set(GoGame(9))

        val activity = rule.launchActivity(null)

        onView(withContentDescription(activity.getString(R.string.triangle))).perform(click())

        onView(withId(R.id.go_board)).perform(tapStone(CellImpl(1, 2), gameProvider.get()))

        assertThat<GoMarker>(gameProvider.get().actMove.markers).contains(TriangleMarker(CellImpl(1, 2)))

        Spoon.screenshot(activity, "triangle")
    }
}
