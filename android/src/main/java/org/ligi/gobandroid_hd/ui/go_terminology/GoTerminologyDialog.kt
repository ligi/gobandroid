package org.ligi.gobandroid_hd.ui.go_terminology

import android.app.Activity
import android.text.util.Linkify
import android.widget.TextView

import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.ui.GobandroidDialog
import timber.log.Timber

class GoTerminologyDialog(context: Activity, term: String) : GobandroidDialog(context) {


    init {

        setTitle(term)
        setIconResource(R.drawable.ic_action_info_outline_wrapped)
        setContentView(R.layout.go_terms_view)

        val tv = this.findViewById(R.id.go_terms_text) as TextView

        val termMap = GoTerminologyViewActivity.Term2resMap
        if (termMap.containsKey(term)) {
            tv.setText(termMap[term]!!)
        } else {
            tv.setText(R.string.no_definition_found)
            Timber.w("no definition found for " + term)
        }

        Linkify.addLinks(tv, Linkify.ALL)

    }

}
