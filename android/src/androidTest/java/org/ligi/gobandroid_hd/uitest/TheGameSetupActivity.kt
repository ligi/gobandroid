package org.ligi.gobandroid_hd.uitest

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import com.squareup.spoon.Spoon
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.endsWith
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.test_helper_functions.setProgress
import org.ligi.gobandroid_hd.ui.game_setup.GoSetupActivity
import org.ligi.trulesk.TruleskActivityRule

@RunWith(AndroidJUnit4::class)
class TheGameSetupActivity {

    @get:Rule
    val rule = TruleskActivityRule(GoSetupActivity::class.java)

    @Test
    fun testThatGoBoardIsThere() {
        Spoon.screenshot(rule.activity, "setup")
        onView(withId(R.id.go_board)).check(matches(isDisplayed()))
    }

    @Test
    fun testThatClickOnButtonOpensCorrectGame() {
        onView(withId(R.id.size_button9x9)).perform(click())
        onView(withId(R.id.game_size_label)).check(matches(withText(containsString("9x9"))))
        Spoon.screenshot(rule.activity, "setup_9x9")

        onView(withId(R.id.size_button13x13)).perform(click())
        onView(withId(R.id.game_size_label)).check(matches(withText(containsString("13x13"))))
        Spoon.screenshot(rule.activity, "setup_13x13")

        onView(withId(R.id.size_button19x19)).perform(click())
        onView(withId(R.id.game_size_label)).check(matches(withText(containsString("19x19"))))
        Spoon.screenshot(rule.activity, "setup_19x19")
    }

    @Test
    fun testThatSeekBarSetsSizeCorrectlyAndHandicapControlsBehaveAsWanted() {

        onView(withId(R.id.size_seek)).perform(setProgress(1))
        onView(withId(R.id.game_size_label)).check(matches(withText(endsWith("3x3"))))
        onView(withId(R.id.handicap_seek)).check(matches(not(isEnabled())))
        onView(withId(R.id.handicap_label)).check(matches(withText(R.string.handicap_only_for)))
        Spoon.screenshot(rule.activity, "setup_3x3_slider")

        onView(withId(R.id.size_seek)).perform(setProgress(0))
        onView(withId(R.id.game_size_label)).check(matches(withText(endsWith("2x2"))))
        onView(withId(R.id.handicap_seek)).check(matches(not(isEnabled())))
        onView(withId(R.id.handicap_label)).check(matches(withText(R.string.handicap_only_for)))
        Spoon.screenshot(rule.activity, "setup_2x2_slider")

        onView(withId(R.id.size_seek)).perform(setProgress(21))
        onView(withId(R.id.game_size_label)).check(matches(withText(containsString("23x23"))))
        onView(withId(R.id.handicap_seek)).check(matches(not(isEnabled())))
        onView(withId(R.id.handicap_label)).check(matches(withText(R.string.handicap_only_for)))
        Spoon.screenshot(rule.activity, "setup_23x23_slider")

        onView(withId(R.id.size_seek)).perform(setProgress(7))
        onView(withId(R.id.game_size_label)).check(matches(withText(containsString("9x9"))))
        onView(withId(R.id.handicap_seek)).check(matches(isEnabled()))
        onView(withId(R.id.handicap_label)).check(matches(not(withText(R.string.handicap_only_for))))
        Spoon.screenshot(rule.activity, "setup_9x9_slider")
    }

}
