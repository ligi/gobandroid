package org.ligi.gobandroid_hd.ui.tsumego;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.ui.fragments.CommentHelper;
import org.ligi.gobandroid_hd.ui.fragments.GobandroidFragment;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TsumegoGameExtrasFragment extends GobandroidFragment {

    private TextView correctView;
    private View OffPathView, res;
    private boolean off_path_visible = false, correct_visible = false;
    private TextView commentView;

    private String replaceLast(String string, String from, String to) {
        int lastIndex = string.lastIndexOf(from);
        if (lastIndex < 0) {
            return string;
        }
        String tail = string.substring(lastIndex).replaceFirst(from, to);
        return string.substring(0, lastIndex) + tail;
    }

    /**
     * try to find next tsumego based on filename
     * searching the last number and incrementing it
     *
     * @param fname
     * @return the filename found
     */
    private String calcNextTsumego(String fname) {
        String old_index = getLastNumberInStringOrNull(fname);

        if (old_index == null) {
            return null;
        }

        int index = Integer.parseInt(old_index);

        String new_index = "";
        // add the leading zeroes
        for (int i = 0; i < old_index.length() - ((index + 1) / 10 + 1); i++) {
            new_index += "0";
        }

        new_index += "" + (index + 1);

        String guessed_fname_str = replaceLast(fname, old_index, new_index);

        // check if it exists
        if (!new File(guessed_fname_str).exists()) {
            return null;
        }

        return guessed_fname_str;

    }

    private String getLastNumberInStringOrNull(String fname) {
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(fname);

        String old_index = "";
        while (m.find()) {
            old_index = m.group();
        }

        // found no index -> exit
        if (old_index.equals("")) {
            return null;
        }

        return old_index;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        if (OffPathView == null || correctView == null || getActivity() == null) { // views not yet created
            return; // will come back later
        }

        final GoGame game = getGame();

        OffPathView.setVisibility(off_path_visible ? TextView.VISIBLE : TextView.GONE);

        if (correct_visible) {
            correctView.setVisibility(View.VISIBLE);
            String next_tsumego_url_str = calcNextTsumego(game.getMetaData().getFileName().replaceFirst("file://", ""));

            if (next_tsumego_url_str != null) {

                correctView.setMovementMethod(LinkMovementMethod.getInstance());

                String text = getString(R.string.tsumego_correct) + " <a href='tsumego://" + next_tsumego_url_str + "'>" + getString(R.string.next_tsumego) + "</a>" + next_tsumego_url_str;
                correctView.setText(Html.fromHtml(text));
            } else {
                correctView.setText("Correct !-) But sadly no more tsumegos in this folder");
            }
        } else {
            correctView.setVisibility(View.GONE);
        }

        // the 10 is a bit of a magic number - just want to show comments that
        // have extras here to prevent double commentView written - but sometimes
        // there is more info in the commentView
        if (!correct_visible && game.getActMove().getComment().length() > 10) {
            commentView.setText(game.getActMove().getComment());
            if (!TextUtils.isEmpty(game.getActMove().getComment())) {
                CommentHelper.linkifyCommentTextView(commentView);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        res = inflater.inflate(R.layout.game_extra_tsumego, null);

        correctView = (TextView) res.findViewById(R.id.tsumego_correct_view);
        OffPathView = res.findViewById(R.id.tsumego_off_path_view);
        commentView = (TextView) res.findViewById(R.id.game_comment);

        return res;
    }

    public void setOffPathVisibility(boolean visible) {
        off_path_visible = visible;
        updateUI();
    }

    public void setCorrectVisibility(boolean visible) {
        correct_visible = visible;
        updateUI();
    }

}
