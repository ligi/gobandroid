package org.ligi.gobandroid_hd.ui.go_terminology

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.util.Linkify
import android.widget.TextView
import org.ligi.gobandroid_hd.R
import java.util.regex.Pattern

class GoTerminologyViewActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.empty)
        // NaDra setBehindContentView(R.layout.empty);

        val term = this.intent.data.toString().substringAfterLast("/")

        val dialog = GoTerminologyDialog(this, term)
        dialog.setPositiveButton(android.R.string.ok,  { dialog ->
            dialog.dismiss()
            finish()
        })
        dialog.setOnCancelListener { finish() }
        dialog.show()

    }

    companion object {

        val Term2resMap = mapOf(
                "joseki" to R.string.goterm_joseki,
                "miai" to R.string.goterm_miai,
                "shape" to R.string.goterm_shape,
                "tesuji" to R.string.goterm_tesuji
                // TODO add missing mojo
        )

        fun linkifyTextView(myTextView: TextView) {

            Linkify.addLinks(myTextView, Linkify.ALL)

            val mentionFilter: Linkify.TransformFilter = Linkify.TransformFilter { matcher, url ->
                matcher.group(1).toLowerCase()
            }

            Term2resMap.keys.forEach {
                val wikiWordMatcher = Pattern.compile("[\\. ]($it)[\\. ]", Pattern.CASE_INSENSITIVE)
                val wikiViewURL = "goterm://org.ligi.gobandroid_hd.goterms/"
                Linkify.addLinks(myTextView, wikiWordMatcher, wikiViewURL, null, mentionFilter)
            }

        }
    }
}
