package org.ligi.gobandroid_hd.ui.go_terminology;

import android.app.Activity;
import android.text.util.Linkify;
import android.widget.TextView;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.GobandroidDialog;
import org.ligi.tracedroid.logging.Log;

import java.util.Map;

public class GoTerminologyDialog extends GobandroidDialog {


    public GoTerminologyDialog(Activity context, String term) {
        super(context);

        setTitle(term);
        setIconResource(R.drawable.ic_action_info_outline_wrapped);
        setContentView(R.layout.go_terms_view);

        final TextView tv = (TextView) this.findViewById(R.id.go_terms_text);

        final Map<String, Integer> termMap = GoTerminologyViewActivity.Companion.getTerm2resMap();
        if (termMap.containsKey(term)) {
            tv.setText(termMap.get(term));
        } else {
            tv.setText(R.string.no_definition_found);
            Log.w("no definition found for " + term);
        }

        Linkify.addLinks(tv, Linkify.ALL);

    }

}
