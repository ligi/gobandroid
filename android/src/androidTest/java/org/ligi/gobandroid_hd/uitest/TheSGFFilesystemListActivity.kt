package org.ligi.gobandroid_hd.uitest

import android.content.Intent
import android.net.Uri
import androidx.test.InstrumentationRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.ui.sgf_listing.SGFFileSystemListActivity
import org.ligi.trulesk.TruleskActivityRule
import java.io.File

class TheSGFFilesystemListActivity {

    @get:Rule
    val rule = TruleskActivityRule(SGFFileSystemListActivity::class.java, false)

    val path by lazy { File(InstrumentationRegistry.getTargetContext().cacheDir, "sgf_list_test") }

    @After
    fun cleanUp() {
        path.deleteRecursively()
    }

    @Test
    fun testThatErrorAppearsIfPathEmpty() {
        startListForPath()

        onView(withText(R.string.problem_listing_sgf)).check(matches(isDisplayed()))
        rule.screenShot("file_list_empty")
    }

    @Test
    fun testThatListIsThereWithDirectoryIfPathContainsDirectory() {
        val probe = "SGFDirProbe"
        File(path, probe).mkdirs()
        startListForPath()

        onView(withText(probe)).check(matches(isDisplayed()))
        rule.screenShot("file_list")
    }

    private fun startListForPath() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(path.toURI().toString())
        rule.launchActivity(intent)
    }

}
