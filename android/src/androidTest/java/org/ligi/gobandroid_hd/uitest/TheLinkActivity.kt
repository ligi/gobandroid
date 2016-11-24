package org.ligi.gobandroid_hd.uitest

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.runner.AndroidJUnit4
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
