package org.ligi.gobandroid_hd.uitest

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.runner.AndroidJUnit4
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.squareup.spoon.Spoon
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.ligi.gobandroid_hd.App
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.model.GameProvider
import org.ligi.gobandroid_hd.test_helper_functions.readGame
import org.ligi.gobandroid_hd.ui.review.GoGamePlayerActivity
import org.ligi.trulesk.TruleskActivityRule

@RunWith(AndroidJUnit4::class)
class TheGoGamePlayerActivity {

    @get:Rule
    val rule = TruleskActivityRule(GoGamePlayerActivity::class.java, false)

    val gameProvider: GameProvider  by App.kodein.lazy.instance()

    fun startActivity() {
        rule.launchActivity(null)
    }

    @Test
    fun testThatGamePlayerStartsWithNoGame() {
        startActivity()

        onView(withId(R.id.go_board)).check(matches(isDisplayed()))

        Spoon.screenshot(rule.activity, "gogameplayer_empty")
    }

    @Test
    fun testThatGamePlayerStartsWithCommentedGame() {
        val readGame = readGame("commented")
        gameProvider.set(readGame!!)

        startActivity()

        Spoon.screenshot(rule.activity, "gogameplayer_commented")
        onView(withId(R.id.go_board)).check(matches(isDisplayed()))
    }

}
