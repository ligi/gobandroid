package org.ligi.gobandroid_hd.uitest

import androidx.test.InstrumentationRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Fail.fail
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.ligi.gobandroid_hd.App
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.model.GameProvider
import org.ligi.gobandroid_hd.test_helper_functions.readAssetHowItShouldBe
import org.ligi.gobandroid_hd.test_helper_functions.readGame
import org.ligi.gobandroid_hd.ui.review.GameReviewActivity
import org.ligi.trulesk.TruleskActivityRule

class TheReviewActivity {

    @get:Rule
    val rule = TruleskActivityRule(GameReviewActivity::class.java, false)

    val gameProvider: GameProvider by App.kodein.lazy.instance()

    @Before
    fun setUp() {
        gameProvider.set(readGame("small_19x19")!!)
        rule.launchActivity(null)
    }

    @Test
    fun testThatGoBoardIsThere() {
        rule.screenShot("review")
        onView(withId(R.id.go_board)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_next)).check(matches(isDisplayed()))
    }


    @Test
    fun testThatNextAndLastButNotPrevAndFirstControlsAreThereOnBeginning() {
        onView(withId(R.id.btn_next)).check(matches(isEnabled()))
        onView(withId(R.id.btn_last)).check(matches(isEnabled()))

        onView(withId(R.id.btn_prev)).check(matches(not(isEnabled())))
        onView(withId(R.id.btn_first)).check(matches(not(isEnabled())))
    }


    @Test
    fun testThatAllControlsAreThereInTheMiddleOfTheGame() {
        onView(withId(R.id.btn_next)).perform(click())

        onView(withId(R.id.btn_next)).check(matches(isEnabled()))
        onView(withId(R.id.btn_last)).check(matches(isEnabled()))

        onView(withId(R.id.btn_prev)).check(matches(isEnabled()))
        onView(withId(R.id.btn_first)).check(matches(isEnabled()))

    }

    @Test
    fun testThatNextAndLastButtonsButNotPrevAndFirstAreThereOnEndOfGame() {
        onView(withId(R.id.btn_last)).perform(click())

        onView(withId(R.id.btn_next)).check(matches(not(isEnabled())))
        onView(withId(R.id.btn_last)).check(matches(not(isEnabled())))

        onView(withId(R.id.btn_prev)).check(matches(isEnabled()))
        onView(withId(R.id.btn_first)).check(matches(isEnabled()))

    }


    @Test
    fun testThatNextWorks() {
        onView(withId(R.id.btn_next)).perform(click())

        assertThat(gameProvider.get().actMove.movePos).isEqualTo(1)
        onView(withId(R.id.btn_next)).perform(click())

        assertThat(gameProvider.get().actMove.movePos).isEqualTo(2)

    }


    @Test
    fun testThatLastAndFirstWorks() {
        onView(withId(R.id.btn_last)).perform(click())

        assertThat(gameProvider.get().actMove.nextMoveVariationCount).isEqualTo(0)

        onView(withId(R.id.btn_first)).perform(click())

        assertThat(gameProvider.get().actMove.parent).isEqualTo(null)

    }

    @Test
    fun TestIfWeCanUseBetterReadingOfAsset() {
        try {
            readAssetHowItShouldBe(InstrumentationRegistry.getInstrumentation().context, "sgf/small_19x19.sgf")

            fail("if this works again ( minify stripped it away) - happy failing test!")
        } catch (e: Throwable) {

        }
    }

}
