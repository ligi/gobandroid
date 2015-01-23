package org.ligi.gobandroid_hd.ui.scoring;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGame.GoGameChangeListener;
import org.ligi.gobandroid_hd.ui.fragments.GobandroidFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GameScoringExtrasFragment extends GobandroidFragment implements GoGameChangeListener {

    @InjectView(R.id.territory_black)
    TextView territory_black;

    @InjectView(R.id.territory_white)
    TextView territory_white;

    @InjectView(R.id.captures_black)
    TextView captures_black;

    @InjectView(R.id.captures_white)
    TextView captures_white;

    @InjectView(R.id.final_black)
    TextView final_black;

    @InjectView(R.id.final_white)
    TextView final_white;

    @InjectView(R.id.komi)
    TextView komi;

    @InjectView(R.id.result_txt)
    TextView result_txt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        App.getGame().addGoGameChangeListener(this);

        final View view = inflater.inflate(R.layout.game_result, container, false);
        ButterKnife.inject(this, view);
        refresh();
        return view;
    }


    @Override
    public void onDestroyView() {
        App.getGame().removeGoGameChangeListener(this);
        super.onDestroyView();
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

    private void refresh() {
        final GoGame game = App.getGame();

        result_txt.setText(getFinTXT(game));

        territory_black.setText(Integer.toString(game.territory_black));
        territory_white.setText(Integer.toString(game.territory_white));

        captures_black.setText(Integer.toString(game.getCapturesBlack()));
        captures_white.setText(Integer.toString(game.getCapturesWhite()));

        komi.setText(Float.toString(game.getKomi()));

        final_black.setText(Float.toString(game.getPointsBlack()));
        final_white.setText(Float.toString(game.getPointsWhite()));
    }

    private String getFinTXT(GoGame game) {
        if (game.getPointsBlack() > game.getPointsWhite()) {
            return (getString(R.string.black_won_with) + (game.getPointsBlack() - game.getPointsWhite()) + getString(R.string._points_));
        }

        if (game.getPointsWhite() > game.getPointsBlack()) {
            return (getString(R.string.white_won_with_) + (game.getPointsWhite() - game.getPointsBlack()) + getString(R.string._points_));
        }
        return getResources().getString(R.string.game_ended_in_draw);
    }
}
