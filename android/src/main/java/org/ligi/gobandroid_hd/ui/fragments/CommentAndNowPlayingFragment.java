package org.ligi.gobandroid_hd.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.text.util.Linkify;
import android.text.util.Linkify.TransformFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.go_terminology.GoTerminologyViewActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentAndNowPlayingFragment extends GobandroidGameAwareFragment {

    private TextView myTextView;

    @Override
    View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View res = inflater.inflate(R.layout.game_extra_review, container, false);

        myTextView = (TextView) res.findViewById(R.id.comments_textview);

        onGoGameChange();
        return res;
    }

    @Override
    public void onGoGameChange() {
        gameChangeHandler.post(new Runnable() {

            @Override
            public void run() {
                if (myTextView != null) {
                    myTextView.setText(game.getActMove().getComment());
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

        });
    }

    private Handler gameChangeHandler = new Handler();

}
