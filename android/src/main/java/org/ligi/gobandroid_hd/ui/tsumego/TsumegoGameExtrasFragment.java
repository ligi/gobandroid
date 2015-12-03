package org.ligi.gobandroid_hd.ui.tsumego;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.model.GameProvider;
import org.ligi.gobandroid_hd.ui.fragments.CommentHelper;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TsumegoGameExtrasFragment extends Fragment {

    @Inject
    GameProvider gameProvider;

    @Bind(R.id.tsumego_correct_view)
    TextView correctView;

    @Bind(R.id.tsumego_off_path_view)
    View OffPathView;

    @Bind(R.id.game_comment)
    TextView commentView;

    private boolean off_path_visible = false, correct_visible = false;

    private void updateUI() {
        if (OffPathView == null || correctView == null || getActivity() == null) { // views not yet created
            return; // will come back later
        }

        final GoGame game = gameProvider.get();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                OffPathView.setVisibility(off_path_visible ? TextView.VISIBLE : TextView.GONE);

                if (correct_visible) {
                    correctView.setVisibility(View.VISIBLE);
                    final String optionalNextTsumegoURLString = NextTsumegoFileFinder.calcNextTsumego(game.getMetaData()
                                                                                                          .getFileName()
                                                                                                          .replaceFirst("file://", ""));

                    if (optionalNextTsumegoURLString != null) {

                        correctView.setMovementMethod(LinkMovementMethod.getInstance());

                        final String text = getString(R.string.tsumego_correct) +
                                            " <a href='tsumego://" +
                                            optionalNextTsumegoURLString +
                                            "'>" +
                                            getString(R.string.next_tsumego) +
                                            "</a>";
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
                    commentView.setVisibility(View.VISIBLE);
                    commentView.setText(game.getActMove().getComment());
                    if (!TextUtils.isEmpty(game.getActMove().getComment())) {
                        CommentHelper.linkifyCommentTextView(commentView);
                    }
                } else {
                    commentView.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        App.component().inject(this);

        final View res = inflater.inflate(R.layout.game_extra_tsumego, container, false);

        ButterKnife.bind(this, res);

        updateUI();
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
