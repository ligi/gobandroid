package org.ligi.gobandroid_hd.ui.sgf_listing.item_view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import java.io.File;
import java.io.IOException;
import org.ligi.axt.AXT;
import org.ligi.gobandroid_hd.FileEncodeDetector;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.sgf.SGFReader;
import org.ligi.gobandroid_hd.ui.PreviewView;
import org.ligi.gobandroid_hd.ui.review.SGFMetaData;
import org.ligi.gobandroid_hd.ui.sgf_listing.GoLink;
import org.ligi.gobandroid_hd.ui.tsumego.TsumegoHelper;

public class TsumegoViewHolder extends RecyclerView.ViewHolder implements ViewHolderInterface {

    @BindView(R.id.previewView)
    PreviewView previewView;

    @BindView(R.id.solve_status_image)
    ImageView solvedStatusImage;

    @BindView(R.id.filename)
    TextView title;

    @BindView(R.id.hints_tv)
    TextView hints_tv;


    public TsumegoViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
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

            final int transform = TsumegoHelper.INSTANCE.calcTransform(game);

            if (transform != SGFReader.DEFAULT_SGF_TRANSFORM) {
                game = SGFReader.sgf2game(sgf_str, null, SGFReader.BREAKON_FIRSTMOVE, transform);
            }

            game.jump(game.findFirstMove());
            if (previewView != null) {
                previewView.setGame(game);
            }
        }

        if (new SGFMetaData(file.getAbsolutePath()).getIsSolved()) {
            solvedStatusImage.setImageResource(R.drawable.ic_social_school);
        } else {
            solvedStatusImage.setImageResource(R.drawable.ic_action_extension);
        }
    }
}
