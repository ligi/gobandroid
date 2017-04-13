package org.ligi.gobandroid_hd.ui

import android.os.Bundle
import android.widget.EditText
import butterknife.BindView
import butterknife.ButterKnife
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity

class BaseProfileActivity : GobandroidFragmentActivity() {

    @BindView(R.id.username_edit)
    internal var username_et: EditText? = null

    @BindView(R.id.rank_edit)
    internal var rank_et: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)
        setTitle(R.string.profile)

        supportActionBar!!.setDisplayShowTitleEnabled(true)

        ButterKnife.bind(this)

        rank_et!!.setText(GoPrefs.rank)
        username_et!!.setText(GoPrefs.username)
    }


    override fun onPause() {
        GoPrefs.rank = rank_et!!.text.toString()
        GoPrefs.username = username_et!!.text.toString()
        super.onPause()
    }

}
