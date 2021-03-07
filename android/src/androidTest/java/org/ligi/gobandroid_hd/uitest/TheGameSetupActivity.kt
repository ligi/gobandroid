package org.ligi.gobandroid_hd.uitest

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.endsWith
import org.junit.Rule
import org.junit.Test
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.test_helper_functions.setProgress
import org.ligi.gobandroid_hd.ui.game_setup.GoSetupActivity
import org.ligi.trulesk.TruleskActivityRule

class TheGameSetupActivity {

    @get:Rule
    val rule = TruleskActivityRule(GoSetupActivity::class.java)

    @Test
    fun testThatGoBoardIsThere() {
        rule.screenShot("setup")
        onView(withId(R.id.go_board)).check(matches(isDisplayed()))
    }

    @Test
    fun testThatClickOnButtonOpensCorrectGame() {
        onView(withId(R.id.size_button9x9)).perform(click())
        onView(withId(R.id.game_size_label)).check(matches(withText(containsString("9x9"))))
        rule.screenShot("setup_9x9")

        onView(withId(R.id.size_button13x13)).perform(click())
        onView(withId(R.id.game_size_label)).check(matches(withText(containsString("13x13"))))
        rule.screenShot("setup_13x13")

        onView(withId(R.id.size_button19x19)).perform(click())
        onView(withId(R.id.game_size_label)).check(matches(withText(containsString("19x19"))))
        rule.screenShot("setup_19x19")
    }

    @Test
    fun testThatSeekBarSetsSizeCorrectlyAndHandicapControlsBehaveAsWanted() {

        onView(withId(R.id.size_seek)).perform(setProgress(1))
        onView(withId(R.id.game_size_label)).check(matches(withText(endsWith("3x3"))))
        onView(withId(R.id.handicap_seek)).check(matches(not(isEnabled())))
        onView(withId(R.id.handicap_label)).check(matches(withText(R.string.handicap_only_for)))
        rule.screenShot("setup_3x3_slider")

        onView(withId(R.id.size_seek)).perform(setProgress(0))
        onView(withId(R.id.game_size_label)).check(matches(withText(endsWith("2x2"))))
        onView(withId(R.id.handicap_seek)).check(matches(not(isEnabled())))
        onView(withId(R.id.handicap_label)).check(matches(withText(R.string.handicap_only_for)))
        rule.screenShot("setup_2x2_slider")

        onView(withId(R.id.size_seek)).perform(setProgress(21))
        onView(withId(R.id.game_size_label)).check(matches(withText(containsString("23x23"))))
        onView(withId(R.id.handicap_seek)).check(matches(not(isEnabled())))
        onView(withId(R.id.handicap_label)).check(matches(withText(R.string.handicap_only_for)))
        rule.screenShot("setup_23x23_slider")

        onView(withId(R.id.size_seek)).perform(setProgress(7))
        onView(withId(R.id.game_size_label)).check(matches(withText(containsString("9x9"))))
        onView(withId(R.id.handicap_seek)).check(matches(isEnabled()))
        onView(withId(R.id.handicap_label)).check(matches(not(withText(R.string.handicap_only_for))))
        rule.screenShot("setup_9x9_slider")
    }

}
