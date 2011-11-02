package org.ligi.gobandroid_hd.ui;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGameProvider;
import org.ligi.tracedroid.logging.Log;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class InGameActionBarView extends View implements GoGame.GoGameChangeListener {

    private Bitmap white_stone_bitmap=null;
    private Bitmap black_stone_bitmap=null;
 
    private Bitmap getScaledRes(float size,int resID) {
    	Bitmap unscaled_bitmap=BitmapFactory.decodeResource(this.getResources(),resID);
    	return   Bitmap.createScaledBitmap(unscaled_bitmap, (int)size, (int)size, true);
    }
    
	public InGameActionBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		 init();
	}

	public InGameActionBarView(Context context) {
		super(context);
		 init();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		black_stone_bitmap=getScaledRes(h,R.drawable.stone_black);
		white_stone_bitmap=getScaledRes(h,R.drawable.stone_white);
		
	}

	public void init() {
		getGame().addGoGameChangeListener(this);
	}

	private GoGame getGame() {
		return GoGameProvider.getGame();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		//canvas.drawColor(Color.RED);
	    if (getGame().isBlackToMove())
	    	canvas.drawBitmap(black_stone_bitmap, 0, 0,null);
	    else
	    	canvas.drawBitmap(white_stone_bitmap, 0, 0,null);
		super.onDraw(canvas);
	}

	@Override
	public void onGoGameChange() {
		Log.i("game changed");
		
		
		
		
		
		this.postInvalidate();
	}
	
}
