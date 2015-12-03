package org.ligi.gobandroid_hd.ui.scoring;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGameScorer;
import org.ligi.gobandroid_hd.ui.fragments.GobandroidGameAwareFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GameScoringExtrasFragment extends GobandroidGameAwareFragment {

    @Bind(R.id.territory_black)
    TextView territory_black;

    @Bind(R.id.territory_white)
    TextView territory_white;

    @Bind(R.id.captures_black)
    TextView captures_black;

    @Bind(R.id.captures_white)
    TextView captures_white;

    @Bind(R.id.final_black)
    TextView final_black;

    @Bind(R.id.final_white)
    TextView final_white;

    @Bind(R.id.komi)
    TextView komi;

    @Bind(R.id.result_txt)
    TextView result_txt;

    @Override
    public View createView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.game_result, container, false);
        ButterKnife.bind(this, view);
        refresh();
        return view;
    }

    @Override
    public void onGoGameChange() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refresh();
            }

        });
    }

    private static String getCapturesString(int captures, int deadStones) {
        final String result = Integer.toString(captures);

        if (deadStones > 0) {
            return result + " + " + deadStones;
        }

        return result;
    }

    public void refresh() {
        final GoGame game = gameProvider.get();
        final GoGameScorer scorer = game.getScorer();

        if (scorer == null) {
            return;
        }

        result_txt.setText(getFinTXT(scorer));

        territory_black.setText(String.format("%d", scorer.territory_black));
        territory_white.setText(String.format("%d", scorer.territory_white));

        captures_black.setText(getCapturesString(game.getCapturesBlack(), scorer.dead_white));
        captures_white.setText(getCapturesString(game.getCapturesWhite(), scorer.dead_black));

        komi.setText(String.format("%.1f", game.getKomi()));

        final_black.setText(String.format("%.1f", scorer.getPointsBlack()));
        final_white.setText(String.format("%.1f", scorer.getPointsWhite()));
    }

    private String getFinTXT(final GoGameScorer scorer) {
        if (scorer.getPointsBlack() > scorer.getPointsWhite()) {
            final float finalPoints = scorer.getPointsBlack() - scorer.getPointsWhite();
            return (getString(R.string.black_won_with) + String.format("%.1f", finalPoints) + getString(R.string._points_));
        }

        if (scorer.getPointsWhite() > scorer.getPointsBlack()) {
            final float finalPoints = scorer.getPointsBlack() - scorer.getPointsWhite();
            return (getString(R.string.white_won_with_) + String.format("%.1f", finalPoints) + getString(R.string._points_));
        }
        return getResources().getString(R.string.game_ended_in_draw);
    }
}
