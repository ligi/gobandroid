package org.ligi.gobandroid_hd.uitest

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import com.squareup.spoon.Spoon
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.TestApp
import org.ligi.gobandroid_hd.base.AssetReader.readGame
import org.ligi.gobandroid_hd.base.placeStone
import org.ligi.gobandroid_hd.logic.CellImpl
import org.ligi.gobandroid_hd.model.GameProvider
import org.ligi.gobandroid_hd.ui.tsumego.TsumegoActivity
import org.ligi.trulesk.TruleskActivityRule
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class TheTsumegoActivity {

    @get:Rule
    val rule = TruleskActivityRule(TsumegoActivity::class.java, false)

    @Inject
    lateinit var gameProvider: GameProvider

    @Before
    fun setUp() {
        TestApp.component().inject(this)
    }

    @Test
    fun testThatNoTsumegoWarningComes() {
        gameProvider.set(readGame("default_marker"))
        val activity = rule.launchActivity(null)

        onView(withText(R.string.tsumego_sgf_no_solution)).check(matches(isDisplayed()))
        Spoon.screenshot(activity, "tsumego_fail")
    }

    @Test
    fun testThatOffPathMessageIsNotThereInBeginning() {
        gameProvider.set(readGame("tsumego"))
        val activity = rule.launchActivity(null)

        onView(withId(R.id.tsumego_off_path_view)).check(matches(not(isDisplayed())))
        Spoon.screenshot(activity, "tsumego_on_path")
    }

    @Test
    fun testThatOffPathMessageComes() {
        gameProvider.set(readGame("tsumego"))
        val activity = rule.launchActivity(null)

        onView(withId(R.id.go_board)).perform(placeStone(CellImpl(1, 1), gameProvider.get()))

        onView(withId(R.id.tsumego_off_path_view)).check(matches(isDisplayed()))
        Spoon.screenshot(activity, "tsumego_off_path")
    }

    @Test
    fun testThatCommentComesAndGoes() {
        gameProvider.set(readGame("tsumego"))
        val activity = rule.launchActivity(null)
        onView(withId(R.id.game_comment)).check(matches(withText("testing comment")))

        Spoon.screenshot(activity, "tsumego_comment")
        onView(withId(R.id.go_board)).perform(placeStone(CellImpl(1, 1), gameProvider.get()))

        onView(withId(R.id.game_comment)).check(matches(not(isDisplayed())))
        Spoon.screenshot(activity, "tsumego_comment_gone")
    }

}
