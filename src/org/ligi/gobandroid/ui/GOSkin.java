package org.ligi.gobandroid.ui;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class GOSkin {

	private static boolean do_skin=false;
	private static String skin_name="none";

	public final static String skin_path="/sdcard/gobandroid/skins/";
	
	public static String getSkinName() {
		return skin_name;
		
	}
	
	public static boolean setSkin(String name) {
		if (useSkin())
			{
			if ((new File(skin_path+name).exists())) 
				{
				skin_name=name;
				return true; 
				}
			else 
				setEnabled(false);
			}
	
		
		return false;
	}

	public static void setEnabled(boolean enabled) {
		do_skin=enabled;
	}
	
	public static boolean useSkin() {
		return do_skin;
	}
	public static String getFullPath() {
		return skin_path+skin_name+"/";
	}
	
	public static Bitmap getBoard(int width,int height) {
		if (do_skin)
			return Bitmap.createScaledBitmap(BitmapFactory.decodeFile(getFullPath()+"board.jpg"), width,height, true);
		else
			return null;
	}
	
	
	public static Bitmap getWhiteStone(float size) {
				return getStone("white",size);
	}
	
	public static Bitmap getBlackStone(float size) {
		return getStone("black",size);
}
	public static Bitmap getStone(String name,float size) {
		if (do_skin) {
				
		int size_append=17;
		if (size>23)
			size_append=32;
		if (size>50)
			size_append=64;

		Log.i("gobandroid", "scale to size" + size);	
		return Bitmap.createScaledBitmap(BitmapFactory.decodeFile(getFullPath()+name + size_append + ".png"
			), (int)size, (int)size, true);
		}
		else {
			Bitmap btm=Bitmap.createBitmap((int)size,(int)size,Bitmap.Config.ARGB_4444);
			
			Canvas c=new Canvas(btm);
			Paint mPaint=new Paint();
			mPaint.setAntiAlias(true);
			if (name.equals("white"))
				mPaint.setColor(Color.WHITE);
			else
				mPaint.setColor(Color.BLACK);
			
			c.drawCircle(c.getWidth()/2, c.getHeight()/2, size/2.0f, mPaint);
			return btm;
		}
	}
	
}
