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

import org.ligi.gobandroid.logic.GoGame;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.*;

public class TopView extends View implements Runnable

{
	private GoGame game;
	
	public String tmp="bar";
	
	private Paint mPaint = new Paint();
	private Paint mTextPaintWhite = new Paint();
	private Paint mTextPaintBlack = new Paint();
	public TopView(Context context, AttributeSet attrs) {
		super(context, attrs);
		new Thread(this).start();
	}

	public TopView(Activity context) {
		super(context);
	}

	Bitmap black_stone;
	Bitmap white_stone;

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		black_stone=GOSkin.getBlackStone(h*0.9f);
		white_stone=GOSkin.getWhiteStone(h*0.9f);
		/*
		// load and scale the images
		black_img = resize_to_screen(BitmapFactory.decodeResource(
				getResources(), R.drawable.bluetooth_off), 0.0f, 1f);

		bt_on_img = resize_to_screen(BitmapFactory.decodeResource(
				getResources(), R.drawable.bluetooth_on), 0.0f, 1f);
		
		bt_on_highlight_img =resize_to_screen(BitmapFactory.decodeResource(
				getResources(), R.drawable.bluetooth_on_highlight), 0.0f, 1f);

		batt_img = resize_to_screen(BitmapFactory.decodeResource(
				getResources(), R.drawable.batt), 0.0f, 1f);

		sats_img = resize_to_screen(BitmapFactory.decodeResource(
				getResources(), R.drawable.sats), 0.0f, 1f);

		rc_img = resize_to_screen(BitmapFactory.decodeResource(getResources(),
				R.drawable.rc), 0.0f, 1f);
		alert_img = resize_to_screen(BitmapFactory.decodeResource(getResources(),
				R.drawable.alert), 0.0f, 1f);
*/
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

	public void symbol_paint(Canvas c, Bitmap img) {
/*
		c.drawBitmap(img, act_symbol_pos, 0, mPaint);

		act_symbol_pos += img.getWidth();
*/
	}

	int act_symbol_pos = 0;

	
	
	private float getTextWidth(String text) {

		float[] widths = new float[text.length()];
		mTextPaintWhite.getTextWidths(text, widths);
		float res = 0;
		for (int i = 0; i < widths.length; i++)
			res += widths[i];
		return res;
	}

	int spacer_items = 5;

	@Override
	protected void onDraw(Canvas canvas) {
		
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

		String move_txt="Move " + game.getActMove().getMovePos();
		
		canvas.drawText(move_txt ,this.getWidth()/2-getTextWidth(move_txt)/2.0f, this
				.getHeight() - 5, mTextPaintWhite);

		
		canvas.drawText(""+game.getCapturesWhite() ,black_stone.getWidth()*1.2f, this
				.getHeight() - 5, mTextPaintWhite);

		
	/*	MKCommunicator mk = MKProvider.getMK();

		act_symbol_pos = 0;

		// connection

		if (mk.connected){
			if (((mk.stats.bytes_in>>4)&1)==1)
				symbol_paint(canvas, bt_on_img);
			else
				symbol_paint(canvas, bt_on_highlight_img);

			act_symbol_pos += spacer_items;

			// if (mk.UBatt()!=-1)
			// mPaint.getFontMetrics().
			
			if (mk.UBatt() != -1) {
				symbol_paint(canvas, batt_img);
				canvas.drawText("" + mk.UBatt() / 10.0, act_symbol_pos, this
						.getHeight() - 5, mTextPaint);
				act_symbol_pos += getTextWidth("" + mk.UBatt() / 10.0);
				act_symbol_pos += spacer_items;
			}

			if (mk.SenderOkay() >190) {
				symbol_paint(canvas, rc_img);
				act_symbol_pos += spacer_items;
			}

			if (mk.is_navi() || mk.is_fake()) {
				if (mk.SatsInUse() != -1) {
					symbol_paint(canvas, sats_img);
					canvas.drawText("" + mk.SatsInUse(), act_symbol_pos, this
							.getHeight() - 5, mTextPaint);
					act_symbol_pos += getTextWidth("" + mk.SatsInUse());
					act_symbol_pos += spacer_items;
				}
				if (mk.gps_position.ErrorCode != 0) {
					symbol_paint(canvas, alert_img);
					act_symbol_pos += spacer_items;
				}
			}

		}
		else
			symbol_paint(canvas, bt_off_img);

		
		// spend some cpu time ( Top doesnt need to be updated that often )
		//TODO make timing editable
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			
		}
		*/
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

	int alpha=200;
	int alpha_dir=-2;
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
