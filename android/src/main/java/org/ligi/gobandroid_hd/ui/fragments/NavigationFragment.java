package org.ligi.gobandroid_hd.ui.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.alerts.GameForwardAlert;

public class NavigationFragment extends GobandroidGameAwareFragment {

    private ImageView next_btn, prev_btn, first_btn, last_btn;
    private Handler gameChangeHandler = new Handler();

    @Override
    View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View res = inflater.inflate(R.layout.nav_button_container, container, false);
        first_btn = findById(res, R.id.btn_first);
        last_btn = findById(res, R.id.btn_last);
        next_btn = findById(res, R.id.btn_next);
        prev_btn = findById(res, R.id.btn_prev);

        first_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                game.jumpFirst();
            }

        });

        last_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                game.jumpLast();
            }

        });

        next_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                gameNavNext();
            }

        });

        prev_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                gameNavPrev();
            }

        });

        updateButtonStates();
        return res;
    }

    @Override
    public void onGoGameChange() {
        gameChangeHandler.post(new Runnable() {

            @Override
            public void run() {
                updateButtonStates();
            }

        });

    }

    private void updateButtonStates() {
        adjustImageBtn(first_btn, R.drawable.nav_first, game.canUndo());
        adjustImageBtn(prev_btn, R.drawable.nav_prev, game.canUndo());

        adjustImageBtn(next_btn, R.drawable.nav_next, game.canRedo());
        adjustImageBtn(last_btn, R.drawable.nav_last, game.canRedo());
    }

    public void adjustImageBtn(ImageView img, int res, boolean enabled) {

        if (getActivity() == null) {
            return;
        }

        if (img.isEnabled() == enabled) { // all good - no work here
            return;
        }

        Bitmap bm = BitmapFactory.decodeResource(getResources(), res);
        if (!enabled) {
            img.setEnabled(false);
            img.setFocusable(false);
            bm = adjustOpacity(bm, 128);
        } else {
            img.setFocusable(true);
            img.setEnabled(true);
        }
        img.setImageDrawable(new BitmapDrawable(bm));
    }

    //and here's where the magic happens
    private static Bitmap adjustOpacity(Bitmap bitmap, int opacity) {
        //make sure bitmap is mutable (copy of needed)
        Bitmap mutableBitmap = bitmap.isMutable()
                ? bitmap
                : bitmap.copy(Bitmap.Config.ARGB_8888, true);

        //draw the bitmap into a canvas
        Canvas canvas = new Canvas(mutableBitmap);

        //create a color with the specified opacity
        int colour = (opacity & 0xFF) << 24;

        //draw the colour over the bitmap using PorterDuff mode DST_IN
        canvas.drawColor(colour, PorterDuff.Mode.DST_IN);

        //now return the adjusted bitmap
        return mutableBitmap;
    }

    public void gameNavNext() {
        GameForwardAlert.showIfNeeded(getActivity(), game);
    }

    public void gameNavPrev() {
        if (!game.canUndo())
            return;

        // dont do it if the mover has to move at the moment
        if (game.getGoMover().isMoversMove())
            return;

        game.getGoMover().paused = true;
        game.undo();

        // undo twice if there is a mover
        if (game.canUndo() && (game.getGoMover().isMoversMove()))
            game.undo();

        game.getGoMover().paused = false;
    }

}
