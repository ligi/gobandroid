package org.ligi.gobandroid_hd.ui

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.ligi.gobandroid_hd.ui.SGFLoadActivity.Companion.INTENT_EXTRA_MOVE_NUM
import org.ligi.gobandroid_hd.ui.sgf_listing.GoLink
import org.ligi.gobandroid_hd.ui.sgf_listing.SGFFileSystemListActivity
import timber.log.Timber

/**
 * Activity to load a go Link
 */
class GoLinkLoadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GobandroidNotifications(this).cancelGoLinkNotification()

        if (intent.data == null) {
            Timber.e("GoLinkLoadActivity with intent_uri==null")
            finish()
            return
        }

        val link = GoLink(intent.data.toString())

        val intent = intent
        intent.data = Uri.parse(link.fileName)

        if (link.linksToDirectory()) {
            intent.setClass(this, SGFFileSystemListActivity::class.java)
        } else {
            // we got some sgf - go to sgfload
            intent.putExtra(INTENT_EXTRA_MOVE_NUM, link.moveDepth)
            intent.setClass(this, SGFLoadActivity::class.java)
        }

        startActivity(intent)
        finish()

    }

}
