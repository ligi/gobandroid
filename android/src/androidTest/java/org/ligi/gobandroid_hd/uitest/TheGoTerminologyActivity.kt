package org.ligi.gobandroid_hd.uitest

import android.content.Intent
import android.net.Uri
import org.junit.Rule
import org.junit.Test
import org.ligi.gobandroid_hd.ui.go_terminology.GoTerminologyViewActivity
import org.ligi.trulesk.TruleskActivityRule

class TheGoTerminologyActivity {

    @get:Rule
    val rule = TruleskActivityRule(GoTerminologyViewActivity::class.java, false)

    @Test
    fun testThatGoBoardIsThere() {

        val intent = Intent()
        intent.data = Uri.parse("goterm://miai")

        rule.launchActivity(intent)

        rule.screenShot("go_terms")
    }

}
