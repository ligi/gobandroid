package org.ligi.gobandroid_hd.ui

import android.os.Bundle
import kotlinx.android.synthetic.withCloud.profile.*
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity

class BaseProfileActivity : GobandroidFragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)
        setTitle(R.string.profile)

        supportActionBar?.setDisplayShowTitleEnabled(true)

        rank_edit.setText(GoPrefs.rank)
        username_edit.setText(GoPrefs.username)
    }


    override fun onPause() {
        GoPrefs.rank = rank_edit.text.toString()
        GoPrefs.username = username_edit.text.toString()
        super.onPause()
    }

}
