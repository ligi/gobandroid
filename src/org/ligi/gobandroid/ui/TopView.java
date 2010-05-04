/**
 * gobandroid 
 * by Marcus -Ligi- Bueschleb 
 * http://ligi.de
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as 
 * published by the Free Software Foundation; 
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 **/

package org.ligi.gobandroid.ui;

import org.ligi.gobandroid.R;
import org.ligi.gobandroid.logic.GoGame;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.*;

/**
 * draw status info for a go game ( act move ; captures ; act turn )
 * @author ligi
 *
 */
public class TopView extends View implements Runnable
{
	private GoGame game;
	
	private Paint mPaint = new Paint();
	private Paint mTextPaintWhite = new Paint();
	private Paint mTextPaintBlack = new Paint();

	private int alpha=200;
	private int alpha_dir=-2;

	private Bitmap black_stone;
	private Bitmap white_stone;
	
	public TopView(Context context, AttributeSet attrs) {
		super(context, attrs);
		new Thread(this).start();
	}

	public TopView(Activity context) {
		super(context);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		black_stone=GOSkin.getBlackStone(h*0.9f);
		white_stone=GOSkin.getWhiteStone(h*0.9f);

		// set up the Paint's
		mTextPaintWhite.setColor(Color.WHITE);
		mTextPaintWhite.setAntiAlias(true); // text looks better without alias
		mTextPaintWhite.setFakeBoldText(true);
		mTextPaintWhite.setShadowLayer(2, 2, 2, Color.BLACK);

		mTextPaintWhite.setTextSize(this.getHeight());
		
		mTextPaintBlack.setColor(Color.BLACK);
		mTextPaintBlack.setAntiAlias(true); // text looks better without alias
		mTextPaintBlack.setFakeBoldText(true);
		mTextPaintBlack.setShadowLayer(2, 2, 2, Color.WHITE);

		mTextPaintBlack.setTextSize(this.getHeight());

	}

	
	private float getTextWidth(String text) {

		float[] widths = new float[text.length()];
		mTextPaintWhite.getTextWidths(text, widths);
		float res = 0;
		for (int i = 0; i < widths.length; i++)
			res += widths[i];
		return res;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		
		if (game!=null) {
		if (game.isBlackToMove()||game.isFinished())
			mPaint.setAlpha(alpha);
		else
			mPaint.setAlpha(255);
		
		canvas.drawBitmap(black_stone,0,(this.getHeight()-black_stone.getHeight())/2,mPaint);
		
		if (!game.isBlackToMove()||game.isFinished())
			mPaint.setAlpha(alpha);
		else
			mPaint.setAlpha(255);
		
		canvas.drawBitmap(white_stone,this.getWidth()-white_stone.getWidth(),(this.getHeight()-white_stone.getHeight())/2,mPaint);
		
		mPaint.setAlpha(255);
		
		String white_points_str="" + game.getCapturesBlack();
		
		canvas.drawText(white_points_str ,this.getWidth()-white_stone.getWidth()*1.2f-getTextWidth(white_points_str), this
				.getHeight() - 5, mTextPaintWhite);

		String move_txt=   this.getResources().getString(R.string.move ) + " " + game.getActMove().getMovePos();
		
		canvas.drawText(move_txt ,this.getWidth()/2-getTextWidth(move_txt)/2.0f, this
				.getHeight() - 5, mTextPaintWhite);

		
		canvas.drawText(""+game.getCapturesWhite() ,black_stone.getWidth()*1.2f, this
				.getHeight() - 5, mTextPaintWhite);
		}
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}

		invalidate();
	}

	public Bitmap resize_to_screen(Bitmap orig, float x_scale_, float y_scale_) {
		// createa matrix for the manipulation
		Matrix matrix = new Matrix();
		float x_scale, y_scale;
		if (y_scale_ != 0f)
			y_scale = (getHeight() * y_scale_) / orig.getHeight();
		else
			// take x_scale
			y_scale = (getWidth() * x_scale_) / orig.getWidth();

		if (x_scale_ != 0f)
			x_scale = (getWidth() * x_scale_) / orig.getWidth();
		else
			x_scale = (getHeight() * y_scale_) / orig.getHeight();

		matrix.postScale(x_scale, y_scale);
		return Bitmap.createBitmap(orig, 0, 0, (int) (orig.getWidth()),
				(int) (orig.getHeight()), matrix, true);// BitmapContfig.ARGB_8888
		// );
	}

	@Override
	public void run() {
		while (true) {
			alpha+=alpha_dir;
			if (alpha<70||alpha>253)
				alpha_dir*=-1;
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
	}

	public void setGame(GoGame game) {
		this.game = game;
	}

	public GoGame getGame() {
		return game;
	}

}
