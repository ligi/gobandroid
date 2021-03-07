package org.ligi.gobandroid_hd.uitest

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import org.junit.Rule
import org.junit.Test
import org.ligi.gobandroid_hd.App
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.model.GameProvider
import org.ligi.gobandroid_hd.test_helper_functions.readGame
import org.ligi.gobandroid_hd.ui.review.GoGamePlayerActivity
import org.ligi.trulesk.TruleskActivityRule

class TheGoGamePlayerActivity {

    @get:Rule
    val rule = TruleskActivityRule(GoGamePlayerActivity::class.java, false)

    val gameProvider: GameProvider by App.kodein.lazy.instance()

    fun startActivity() {
        rule.launchActivity(null)
    }

    @Test
    fun testThatGamePlayerStartsWithNoGame() {
        startActivity()

        onView(withId(R.id.go_board)).check(matches(isDisplayed()))

        rule.screenShot("gogameplayer_empty")
    }

    @Test
    fun testThatGamePlayerStartsWithCommentedGame() {
        val readGame = readGame("commented")
        gameProvider.set(readGame!!)

        startActivity()

        rule.screenShot("gogameplayer_commented")
        onView(withId(R.id.go_board)).check(matches(isDisplayed()))
    }

}
