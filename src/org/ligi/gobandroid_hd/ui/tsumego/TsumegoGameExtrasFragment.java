package org.ligi.gobandroid_hd.ui.tsumego;

import org.ligi.gobandroid_beta.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.ui.fragments.CommentHelper;
import org.ligi.gobandroid_hd.ui.fragments.GobandroidFragment;
import org.ligi.tracedroid.logging.Log;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TsumegoGameExtrasFragment extends GobandroidFragment {

	private View off_path_view, correct_view, res;
	private boolean off_path_visible = false, correct_visible = false;
	private TextView comment;
	private GoGame game;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i("creating tsumego");
		res = inflater.inflate(R.layout.game_extra_tsumego, null);
		correct_view = res.findViewById(R.id.tsumego_correct_view);
		off_path_view = res.findViewById(R.id.tsumego_off_path_view);
		comment = (TextView) res.findViewById(R.id.game_comment);

		// hide both views by default
		setOffPathVisibility(off_path_visible);
		setCorrectVisibility(correct_visible);

		game = getGame();

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
