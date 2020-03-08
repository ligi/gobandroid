package org.ligi.gobandroid_hd.uitest

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.runner.AndroidJUnit4
import com.jraska.falcon.FalconSpoon
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.ligi.gobandroid_hd.ui.links.LinksActivity
import org.ligi.trulesk.TruleskActivityRule

@RunWith(AndroidJUnit4::class)
class TheLinkActivity {

    @get:Rule
    val rule = TruleskActivityRule(LinksActivity::class.java)

    @Test
    fun testThatProjectPageEntryIsVisible() {
        FalconSpoon.screenshot(rule.activity, "link_list")
        onView(withText("Gobandroid Project Page")).check(matches(isDisplayed()))
    }
}
