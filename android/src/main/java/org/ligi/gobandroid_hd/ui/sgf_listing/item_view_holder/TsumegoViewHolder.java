package org.ligi.gobandroid_hd.ui.sgf_listing.item_view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.ligi.axt.AXT;
import org.ligi.gobandroid_hd.FileEncodeDetector;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.sgf.SGFReader;
import org.ligi.gobandroid_hd.ui.PreviewView;
import org.ligi.gobandroid_hd.ui.review.SGFMetaData;
import org.ligi.gobandroid_hd.ui.sgf_listing.GoLink;
import org.ligi.gobandroid_hd.ui.tsumego.TsumegoHelper;

import java.io.File;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TsumegoViewHolder extends RecyclerView.ViewHolder implements ViewHolderInterface {

    @InjectView(R.id.previewView)
    PreviewView previewView;

    @InjectView(R.id.solve_status_image)
    ImageView solvedStatusImage;

    @InjectView(R.id.filename)
    TextView title;

    @InjectView(R.id.hints_tv)
    TextView hints_tv;


    public TsumegoViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
    }

    @Override
    public void apply(final File file) {

        title.setText(file.getName().replace(".sgf", ""));

        String sgf_str = "";

        if (GoLink.isGoLink(file)) {
            GoLink gl = new GoLink(file);
            sgf_str = gl.getSGFString();
        } else {
            try {
                sgf_str = AXT.at(file).readToString(FileEncodeDetector.detect(file));
            } catch (IOException e) {
            }
        }

        final String hints_used_fmt = hints_tv.getContext().getString(R.string.hints_used);
        GoGame game = SGFReader.sgf2game(sgf_str, null, SGFReader.BREAKON_FIRSTMOVE);

        if (game != null) {
            final SGFMetaData meta = new SGFMetaData(file.getAbsolutePath());

            hints_tv.setText(String.format(hints_used_fmt, meta.getHintsUsed()));

            final int transform = TsumegoHelper.calcTransform(game);

            if (transform != SGFReader.DEFAULT_SGF_TRANSFORM) {
                game = SGFReader.sgf2game(sgf_str, null, SGFReader.BREAKON_FIRSTMOVE, transform);
            }

            game.jump(game.getFirstMove());
            if (previewView != null) {
                previewView.setGame(game);
            }
        }

        if (new SGFMetaData(file.getAbsolutePath()).getIsSolved()) {
            solvedStatusImage.setImageResource(R.drawable.solved);
        } else {
            solvedStatusImage.setImageResource(R.drawable.dashboard_tsumego);
        }
    }
}
