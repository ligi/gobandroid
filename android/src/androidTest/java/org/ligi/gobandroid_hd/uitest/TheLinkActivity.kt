package org.ligi.gobandroid_hd.uitest

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.junit.Rule
import org.junit.Test
import org.ligi.gobandroid_hd.ui.links.LinksActivity
import org.ligi.trulesk.TruleskActivityRule

class TheLinkActivity {

    @get:Rule
    val rule = TruleskActivityRule(LinksActivity::class.java)

    @Test
    fun testThatProjectPageEntryIsVisible() {
        rule.screenShot("link_list")
        onView(withText("Gobandroid Project Page")).check(matches(isDisplayed()))
    }
}
