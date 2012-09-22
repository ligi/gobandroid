package org.ligi.gobandroid_hd.ui;

import org.ligi.gobandroid_beta.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.ui.tsumego.TsumegoHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
/**
 * A lighter Board-View to use as preview ( e.g. in game listings
 * 
 * @author <a href="http://ligi.de">Marcus -LiGi- Bueschleb </a>
 * 
 *         This software is licenced with GPLv3
 */
public class PreviewView extends View {

	private GoGame game;
	private Bitmap white_stone_bitmap, black_stone_bitmap;
	private int span;
	private int stone_size;
	private Paint black_line_paint;

	public PreviewView(Context context, GoGame game) {
		super(context);

		span = TsumegoHelper.calcSpan(game) + 1;

		this.game = game;
		black_line_paint = new Paint();
		black_line_paint.setColor(Color.BLACK);
	}

	private Bitmap getScaledRes(float size, int resID) {
		Bitmap unscaled_bitmap = BitmapFactory.decodeResource(
				this.getResources(), resID);
		return Bitmap.createScaledBitmap(unscaled_bitmap, (int) size,
				(int) size, true);
	}

	public GoGame getGame() {
		return game;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		stone_size = (int) (h / span);
		white_stone_bitmap = getScaledRes(stone_size, R.drawable.stone_white);
		black_stone_bitmap = getScaledRes(stone_size, R.drawable.stone_black);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// int offset=game.getSize()-span;

		for (int x = 0; x < span; x++) {
			canvas.drawLine(0.5f * stone_size, (0.5f + x) * stone_size,
					(0.5f + span) * stone_size, (0.5f + x) * stone_size,
					black_line_paint);
			canvas.drawLine((0.5f + x) * stone_size, 0.5f * stone_size,
					(0.5f + x) * stone_size, (.5f + span) * stone_size,
					black_line_paint);
		}

		for (int x = 0; x < span; x++)
			for (int y = 0; y < span; y++) {
				if (game.getVisualBoard().isCellBlack(x, y))
					canvas.drawBitmap(black_stone_bitmap, x * stone_size, y
							* stone_size, null);
				if (game.getVisualBoard().isCellWhite(x, y))
					canvas.drawBitmap(white_stone_bitmap, x * stone_size, y
							* stone_size, null);
			}
	}

}
