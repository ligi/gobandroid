package org.ligi.gobandroid_hd.ui.fragments;

import android.text.util.Linkify;
import android.text.util.Linkify.TransformFilter;
import android.widget.TextView;

import org.ligi.gobandroid_hd.ui.go_terminology.GoTerminologyViewActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentHelper {
    public static void linkifyCommentTextView(TextView myTextView) {
        Linkify.addLinks(myTextView, Linkify.ALL);

        TransformFilter mentionFilter = new TransformFilter() {
            public final String transformUrl(final Matcher match, String url) {
                return match.group(1).toLowerCase();
            }
        };

        for (String key : GoTerminologyViewActivity.Term2resMap.keySet()) {
            Pattern wikiWordMatcher = Pattern.compile("[\\. ](" + key + ")[\\. ]", Pattern.CASE_INSENSITIVE);
            String wikiViewURL = "goterm://org.ligi.gobandroid_hd.goterms/";
            Linkify.addLinks(myTextView, wikiWordMatcher, wikiViewURL, null, mentionFilter);
        }
    }
}
