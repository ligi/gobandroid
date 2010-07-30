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

import java.io.File;

import org.ligi.tracedroid.logging.Log;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class GOSkin {

	private static boolean do_board_skin=false;
	private static String board_skin_name="none";
	
	private static boolean do_stone_skin=false;
	private static String stone_skin_name="none";
	

	public final static String skin_base_path="/sdcard/gobandroid/skins/";
	
	public static void setBoardSkin(String name) {
		if ((new File(skin_base_path+name).exists())) 
		{
			board_skin_name=name;
			do_board_skin=true;
		}
		else 
			do_board_skin=false;
	}


	public static void setStoneSkin(String name) {
		if ((new File(skin_base_path+name).exists())) 
		{
			stone_skin_name=name;
			do_stone_skin=true;
		}
		else 
			do_stone_skin=false;
	}

	
	public static String getBoardFname() {
		return skin_base_path+board_skin_name+"/board.jpg";
	}
	
	public static Bitmap getBoard() {
		return BitmapFactory.decodeFile(getBoardFname());
	}
	
	public static Bitmap getBoard(int width,int height) {
		if (do_board_skin)
			return Bitmap.createScaledBitmap(getBoard(), width,height, true);
		else
			return null;
	}
	
	
	public static Bitmap getWhiteStone(float size) {
		return getStone("white",size);
	}
	
	public static Bitmap getBlackStone(float size) {
		return getStone("black",size);
	}
	
	/**
	 * returns a image for a go stone - either a 
	 * selected ( by color/size ) and scaled image from the skin
	 * or a drawn circle in the right color ( fallback / no skin )   
	 * 
	 * @param name "white" or "black"
	 * @param size the size in pixels
	 * 
	 * @return the scaled image
	 */
	public static Bitmap getStone(String name,float size) {
		
		if (size<1)
			size=1;	
		
		if (do_stone_skin) try {
				
			int size_append=17;
			
			if (size>50)
				size_append=64;
			else if (size>23)
				size_append=32;
			
			Log.i("scale to size" + size);	
			Bitmap unscaled_bitmap=BitmapFactory.decodeFile(skin_base_path+stone_skin_name+"/"+name + size_append + ".png");
			return Bitmap.createScaledBitmap(unscaled_bitmap, (int)size, (int)size, true);
			}
		catch (Exception e) {
			Log.w("problem scaling the " + name + " stone bitmap to" + size );
		}
		
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
