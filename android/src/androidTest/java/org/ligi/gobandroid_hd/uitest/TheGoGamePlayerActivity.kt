package org.ligi.gobandroid_hd.uitest

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.runner.AndroidJUnit4
import com.squareup.spoon.Spoon
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.TestApp
import org.ligi.gobandroid_hd.base.AssetReader.readGame
import org.ligi.gobandroid_hd.model.GameProvider
import org.ligi.gobandroid_hd.ui.review.GoGamePlayerActivity
import org.ligi.trulesk.TruleskActivityRule
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class TheGoGamePlayerActivity {

    @get:Rule
    val rule = TruleskActivityRule(GoGamePlayerActivity::class.java, false)

    @Inject
    lateinit var gameProvider: GameProvider

    fun startActivity() {
        rule.launchActivity(null)
    }

    @Before
    fun setUp() {
        TestApp.component().inject(this)
    }

    @Test
    fun testThatGamePlayerStartsWithNoGame() {
        startActivity()

        onView(withId(R.id.go_board)).check(matches(isDisplayed()))

        Spoon.screenshot(rule.activity, "gogameplayer_empty")
    }

    @Test
    fun testThatGamePlayerStartsWithCommentedGame() {
        gameProvider.set(readGame("commented"))

        startActivity()

        Spoon.screenshot(rule.activity, "gogameplayer_commented")
        onView(withId(R.id.go_board)).check(matches(isDisplayed()))
    }

}
