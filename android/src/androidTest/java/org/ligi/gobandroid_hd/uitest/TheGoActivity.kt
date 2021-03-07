package org.ligi.gobandroid_hd.uitest

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.junit.Rule
import org.junit.Test
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.ui.GoActivity
import org.ligi.trulesk.TruleskActivityRule

class TheGoActivity {

    @get:Rule
    val rule = TruleskActivityRule(GoActivity::class.java)

    @Test
    fun testThatGoBoardIsThere() {
        rule.screenShot("go_activity")
        onView(withId(R.id.go_board)).check(matches(isDisplayed()))
    }

}
