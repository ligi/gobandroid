package org.ligi.gobandroid_hd.uitest

import android.os.SystemClock
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.scrollTo
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.squareup.spoon.Spoon
import org.hamcrest.Matchers.containsString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.ligi.gobandroid_hd.App
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.logic.Cell
import org.ligi.gobandroid_hd.logic.CellImpl
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.model.GameProvider
import org.ligi.gobandroid_hd.test_helper_functions.tapStone
import org.ligi.gobandroid_hd.ui.scoring.GameScoringActivity
import org.ligi.trulesk.TruleskActivityRule


@RunWith(AndroidJUnit4::class)
class TheGameScoringActivity {

    @get:Rule
    val rule = TruleskActivityRule(GameScoringActivity::class.java, false)

    val gameProvider: GameProvider  by App.kodein.lazy.instance()


    @Test
    fun testThatGoBoardIsThere() {
        val activity = rule.launchActivity(null)

        onView(withId(R.id.go_board)).check(matches(isDisplayed()))
        Spoon.screenshot(activity, "count")
    }

    @Test
    fun testThatOneStoneOn9x9are80pt() {
        gameProvider.set(GoGame(9.toByte().toInt()))
        gameProvider.get().do_move(CellImpl(0, 0))

        val activity = rule.launchActivity(null)

        onView(withId(R.id.go_board)).perform(tapStone(CellImpl(0, 0), gameProvider.get()))

        sleep()

        onView(withId(R.id.final_black)).perform(scrollTo())
        onView(withId(R.id.final_black)).check(matches(withText("80.0")))
        onView(withId(R.id.captures_black)).check(matches(withText("0")))
        onView(withId(R.id.result_txt)).check(matches(withText(containsString("Black"))))

        Spoon.screenshot(activity, "one_stone_count")
    }


    @Test
    fun testThatTapToMarkWorks() {
        gameProvider.set(GoGame(9.toByte().toInt()))
        gameProvider.get().do_move(CellImpl(0, 0))
        gameProvider.get().do_move(CellImpl(0, 1))

        val activity = rule.launchActivity(null)

        onView(withId(R.id.go_board)).perform(tapStone(CellImpl(0, 0), gameProvider.get()))

        sleep()

        onView(withId(R.id.final_black)).perform(scrollTo())
        onView(withId(R.id.final_black)).check(matches(withText("81.0")))
        onView(withId(R.id.captures_black)).check(matches(withText("0 + 1")))
        onView(withId(R.id.result_txt)).check(matches(withText(containsString("Black"))))

        Spoon.screenshot(activity, "count_mark_one")
    }

    fun sleep() {
        SystemClock.sleep(1000)
    }

    @Test
    fun testThatWhiteCanWin() {
        gameProvider.set(GoGame(9.toByte().toInt()))
        gameProvider.get().do_move(CellImpl(0, 0))
        gameProvider.get().do_move(CellImpl(0, 1))

        val activity = rule.launchActivity(null)

        onView(withId(R.id.go_board)).perform(tapStone(CellImpl(0, 1), gameProvider.get()))

        sleep()

        onView(withId(R.id.final_white)).perform(scrollTo())
        onView(withId(R.id.final_white)).check(matches(withText("87.5")))
        onView(withId(R.id.captures_black)).check(matches(withText("0")))
        onView(withId(R.id.captures_white)).check(matches(withText("0 + 1")))
        onView(withId(R.id.result_txt)).check(matches(withText(containsString("White"))))

        Spoon.screenshot(activity, "count_white_wins")
    }

    @Test
            // https://github.com/ligi/gobandroid/issues/143
    fun testThatScoringWorksWhenTerritoryIsInsideDeadStones() {
        gameProvider.set(GoGame(3.toByte().toInt()))
        gameProvider.get().do_move(CellImpl(0, 1))
        gameProvider.get().do_move(CellImpl(2, 2))
        gameProvider.get().do_move(CellImpl(1, 0))

        val activity = rule.launchActivity(null)

        onView(withId(R.id.go_board)).perform(tapStone(CellImpl(2, 2), gameProvider.get()))

        onView(withId(R.id.final_white)).perform(scrollTo())
        onView(withId(R.id.final_white)).check(matches(withText("16.5")))

        Spoon.screenshot(activity, "count_scoring_territory_in_dead_stones")
    }

    @Test
    fun testThatGroupMarkingWorks() {
        gameProvider.set(GameBuilder(GoGame(9)).move(CellImpl(0, 0), CellImpl(0, 1), CellImpl(1, 0)).game)

        val activity = rule.launchActivity(null)

        onView(withId(R.id.go_board)).perform(tapStone(CellImpl(0, 1), gameProvider.get()))

        sleep()

        onView(withId(R.id.final_white)).check(matches(withText("88.5")))

        Spoon.screenshot(activity, "count_mark_group")
    }

    inner class GameBuilder constructor(val game: GoGame) {

        fun move(vararg cells: Cell): GameBuilder {
            for (cell in cells) {
                game.do_move(cell)
            }
            return this
        }
    }
}
