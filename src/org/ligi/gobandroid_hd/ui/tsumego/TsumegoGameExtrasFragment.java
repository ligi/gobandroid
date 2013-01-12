package org.ligi.gobandroid_hd.ui.tsumego;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.ligi.gobandroid_beta.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.ui.fragments.CommentHelper;
import org.ligi.gobandroid_hd.ui.fragments.GobandroidFragment;
import org.ligi.tracedroid.logging.Log;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TsumegoGameExtrasFragment extends GobandroidFragment {

    private TextView correct_view;
    private View off_path_view, res;
    private boolean off_path_visible = false, correct_visible = false;
    private TextView comment;
    private GoGame game;

    private String replaceLast(String string, String from, String to) {
        int lastIndex = string.lastIndexOf(from);
        if (lastIndex < 0) return string;
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
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(fname);

        String old_index = "";
        while (m.find()) {
            old_index = m.group();
        }
        
        // found no index -> exit 
        if (old_index.equals(""))
        	return null;
        
        int index = Integer.parseInt(old_index);

        String new_index = "";
        // add the leading zeroes
        for (int i = 0; i < old_index.length() - ((index + 1) / 10 + 1); i++)
            new_index += "0";

        new_index += "" + (index + 1);

        String guessed_fname_str=replaceLast(fname, old_index, new_index);
        
        // check if it exists
        if (!new File(guessed_fname_str).exists())
        	return null;
        
        return guessed_fname_str;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("creating tsumego");
        res = inflater.inflate(R.layout.game_extra_tsumego, null);
        correct_view = (TextView) res.findViewById(R.id.tsumego_correct_view);
        off_path_view = res.findViewById(R.id.tsumego_off_path_view);
        comment = (TextView) res.findViewById(R.id.game_comment);


        // hide both views by default
        setOffPathVisibility(off_path_visible);
        setCorrectVisibility(correct_visible);

        game = getGame();


        String next_tsumego_url_str = calcNextTsumego(game.getMetaData().getFileName().replaceFirst("file://", ""));

        if (next_tsumego_url_str != null ) {

            correct_view.setMovementMethod(LinkMovementMethod.getInstance());

            String text = getString(R.string.tsumego_correct) + " <a href='tsumego://" + next_tsumego_url_str + "'>" + getString(R.string.next_tsumego) + "</a>";
            correct_view.setText(Html.fromHtml(text));
        }


        // the 10 is a bit of a magic number - just want to show comments that
        // have extras here to prevent double comment written - but sometimes
        // there is more info in the comment
        if (!correct_visible || game.getActMove().getComment().length() > 10) {
            comment.setText(game.getActMove().getComment());
            if (!game.getActMove().getComment().equals(""))
                CommentHelper.linkifyCommentTextView(comment);
        }
        return res;
    }

    public void setOffPathVisibility(boolean visible) {
        off_path_visible = visible;
        Log.i("visible" + visible);
        if (off_path_view != null)
            off_path_view.setVisibility(visible ? TextView.VISIBLE : TextView.GONE);
    }

    public void setCorrectVisibility(boolean visible) {
        correct_visible = visible;
        if (correct_view != null)
            correct_view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

}
