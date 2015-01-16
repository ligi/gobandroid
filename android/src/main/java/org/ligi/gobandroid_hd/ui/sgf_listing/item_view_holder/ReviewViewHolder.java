package org.ligi.gobandroid_hd.ui.sgf_listing.item_view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.ligi.axt.AXT;
import org.ligi.gobandroid_hd.FileEncodeDetector;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.MetaDataFormatter;
import org.ligi.gobandroid_hd.logic.sgf.SGFReader;
import org.ligi.gobandroid_hd.ui.review.SGFMetaData;
import org.ligi.gobandroid_hd.ui.sgf_listing.GoLink;

import java.io.File;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ReviewViewHolder extends RecyclerView.ViewHolder implements ViewHolderInterface {

    @InjectView(R.id.game_link_extra_infos)
    TextView game_link_extra_info;

    @InjectView(R.id.filename)
    TextView title;

    @InjectView(R.id.game_extra_infos)
    TextView game_extra_info;

    @InjectView(R.id.player_white)
    TextView player_white;

    @InjectView(R.id.player_black)
    TextView player_black;

    @InjectView(R.id.player_white_stone_img)
    ImageView player_white_stone_img;

    @InjectView(R.id.player_black_stone_img)
    ImageView player_black_stone_img;

    @InjectView(R.id.game_rating)
    RatingBar game_rating;

    public ReviewViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
    }

    @Override
    public void apply(File file) {

        title.setText(file.getName().replace(".sgf", ""));

        try {
            if (GoLink.isGoLink(file)) {
                final GoLink gl = new GoLink(file);
                file = new File(gl.getFileName());
                game_link_extra_info.setText("Move #" + gl.getMoveDepth());
            } else {
                game_link_extra_info.setVisibility(View.GONE);
            }

            final String sgf_str = AXT.at(file).readToString(FileEncodeDetector.detect(file));
            final GoGame game = SGFReader.sgf2game(sgf_str, null, SGFReader.BREAKON_FIRSTMOVE);
            final SGFMetaData sgf_meta = new SGFMetaData(file.getAbsolutePath());

            if (game != null) {
                final MetaDataFormatter metaFormatter = new MetaDataFormatter(game);

                if (metaFormatter.getWhitePlayerString().isEmpty()) {
                    player_white_stone_img.setVisibility(View.GONE);
                    player_white.setVisibility(View.GONE);
                } else {
                    player_white.setText(metaFormatter.getWhitePlayerString());
                }

                if (metaFormatter.getBlackPlayerString().isEmpty()) {
                    player_black_stone_img.setVisibility(View.GONE);
                    player_black.setVisibility(View.GONE);
                } else {
                    player_black.setText(metaFormatter.getBlackPlayerString());
                }

                game_extra_info.setText(metaFormatter.getExtrasString());

                if (!sgf_meta.hasData()) {
                    game_rating.setVisibility(View.GONE);
                } else if (sgf_meta.getRating() != null) {
                    game_rating.setVisibility(View.VISIBLE);
                    game_rating.setRating(.5f * sgf_meta.getRating());
                }
            }

        } catch (IOException e) {
        }

    }


}
