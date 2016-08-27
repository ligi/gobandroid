package org.ligi.gobandroid_hd.uitest

import android.content.Intent
import android.net.Uri
import android.support.test.runner.AndroidJUnit4
import com.jraska.falcon.FalconSpoon
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.ligi.gobandroid_hd.base.EnvironmentPreparingTestRule
import org.ligi.gobandroid_hd.ui.go_terminology.GoTerminologyViewActivity

@RunWith(AndroidJUnit4::class)
class TheGoTerminologyActivity {

    @get:Rule
    val rule = EnvironmentPreparingTestRule(GoTerminologyViewActivity::class.java, false)

    @Test
    fun testThatGoBoardIsThere() {

        val intent = Intent()
        intent.data = Uri.parse("goterm://miai")

        rule.launchActivity(intent)

        FalconSpoon.screenshot(rule.activity, "go_terms")
    }

}
