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

public class TsumegoGameExtrasFragment extends GobandroidFragment {

    private TextView correctView;
    private View OffPathView, res;
    private boolean off_path_visible = false, correct_visible = false;
    private TextView commentView;

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
            String next_tsumego_url_str = NextTsumegoFileFinder.calcNextTsumego(game.getMetaData().getFileName().replaceFirst("file://", ""));

            if (next_tsumego_url_str != null) {

                correctView.setMovementMethod(LinkMovementMethod.getInstance());

                final String text = getString(R.string.tsumego_correct) + " <a href='tsumego://" + next_tsumego_url_str + "'>" + getString(R.string.next_tsumego) + "</a>";
                correctView.setText(Html.fromHtml(text));
            } else {
                correctView.setText(getString(R.string.correct_but_no_more_tsumegos));
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
