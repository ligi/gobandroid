package org.ligi.gobandroid_hd.uitest

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.runner.AndroidJUnit4
import com.jraska.falcon.FalconSpoon
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.ui.GoActivity
import org.ligi.trulesk.TruleskActivityRule

@RunWith(AndroidJUnit4::class)
class TheGoActivity {

    @get:Rule
    val rule = TruleskActivityRule(GoActivity::class.java)

    @Test
    fun testThatGoBoardIsThere() {
        FalconSpoon.screenshot(rule.activity, "go_activity")
        onView(withId(R.id.go_board)).check(matches(isDisplayed()))
    }

}
