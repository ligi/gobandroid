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

import org.ligi.gobandroid.logic.GoDefinitions;
import org.ligi.gobandroid.logic.GoGame;
import org.ligi.gobandroid.logic.GoMarker;
import org.ligi.gobandroid.logic.Logger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.FontMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


/**
 * Class to visually represent a Go Board in Android
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         
 * This software is licensed with GPLv3 
 */
public class GoBoardView extends View {
   	private GoGame game;

	private Paint whitePaint;
	private Paint blackPaint;
	private Paint blackTextPaint;
	private Paint whiteTextPaint;
	private Paint boardPaint;
	private Paint gridPaint;
	private Paint gridPaint_h; // highlighted for cursor
    
    private Paint textPaint;
    
    private float stone_size;
    private float stone_size_zoomed;
    private float stone_size_normal;
    
    private float offset_x=0.0f;
    private float offset_y=0.0f;
    
    //TODO rename - name is now misleading
    public byte touch_x=-1;
    public byte touch_y=-1;

    //public boolean do_zoom=false;
    
    private Bitmap bg_bitmap=null;
    
    private Bitmap white_stone_bitmap=null;
    private Bitmap black_stone_bitmap=null;
    private Bitmap white_stone_bitmap_small=null;
    private Bitmap black_stone_bitmap_small=null;
    
    private boolean width_is_max;
    private boolean regenerate_stones_flag=true;
    
    
    public GoBoardView( Context context,GoGame game ) {
        super( context );
        this.game=game;
      //  this.board=game.getVisualBoard();
        
        whitePaint=new Paint();
        whitePaint.setColor(0xFFCCCCCC);
        whitePaint.setAntiAlias(true);
        
        whiteTextPaint=new Paint();
        whiteTextPaint.setColor(0xFFFFFFFF);
        whiteTextPaint.setAntiAlias(true);
        whiteTextPaint.setTextAlign(Paint.Align.CENTER );
        whiteTextPaint.setShadowLayer(2, 1, 1, 0xFF000000);
        
        blackTextPaint=new Paint();
        blackTextPaint.setColor(0xFF000000);
        blackTextPaint.setAntiAlias(true);
        blackTextPaint.setTextAlign(Paint.Align.CENTER  ); 
        blackTextPaint.setShadowLayer(2, 1, 1, 0xFFFFFFFF);
        
        blackPaint=new Paint();
        blackPaint.setColor(0xFF000000);
       
        blackPaint.setAntiAlias(true);
        
        boardPaint=new Paint();
        //boardPaint.setColor(0xFFc6b460);
        boardPaint.setColor(0xFFA77E3D);
        //boardPaint.setColor(0xFFA68064);
        gridPaint=new Paint();
        
        gridPaint_h=new Paint();
        gridPaint_h.setColor(0xFF0000FF);
        gridPaint_h.setShadowLayer(1,1,1,0xFFFFFFFF );
        //gridPaint.setColor(0xFFFFFFFF);
        //gridPaint.setShadowLayer(1,1,1,0xFF000000 );
        
        gridPaint.setColor(0xFF000000);
        gridPaint.setShadowLayer(1,1,1,0xFFFFFFFF );
        gridPaint.setTextAlign(Paint.Align.CENTER );
        gridPaint.setTextSize(12.0f );
    
        textPaint=new Paint();
        textPaint.setColor(0xFF000000);
        textPaint.setAntiAlias(false);
        
        setFocusable(true);   
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

    
    public void prepare_keyinput() {
    	if (touch_x==-1) touch_x=0;
    	if (touch_y==-1) touch_y=0;
    }
    

    public void setZoom(boolean zoom_flag) {
    	if (zoom_flag) 
    		{
    		stone_size=stone_size_zoomed;
			
			if (touch_x>= (2*game.getVisualBoard().getSize())/3)
				offset_x=-stone_size*game.getVisualBoard().getSize()+this.getWidth();
			else if (touch_x> (game.getVisualBoard().getSize()/3))
				offset_x=(-stone_size*game.getVisualBoard().getSize()+this.getWidth())/2;	
			
			if (touch_y>= (2*game.getVisualBoard().getSize())/3)
				offset_y=-stone_size*game.getVisualBoard().getSize()+this.getHeight();
			else if (touch_y> (game.getVisualBoard().getSize()/3))
				offset_y=(-stone_size*game.getVisualBoard().getSize()+this.getHeight())/2;	
    		}
    	else 
    		{
			stone_size=stone_size_normal;
			offset_x=0;
			offset_y=0;
    		}
    	
    	
    	regenerate_images();	
    	invalidate();
    }

    /**
     * check if the Board is Zoomed
     * 
     * @return true if zoomed
     */
    public boolean isZoomed() {
    	return (stone_size==stone_size_zoomed);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
    
    	
    	if (game==null)
    		return;
    	
    	if (regenerate_stones_flag)
    		regenerate_images();
    	
    	if (bg_bitmap!=null)
    		canvas.drawBitmap(bg_bitmap,0.0f, 0.0f, boardPaint);
    	else
    		canvas.drawRect(new RectF(0,0,this.getWidth(),this.getHeight()),boardPaint );
    	
    	
        canvas.translate(offset_x, offset_y);
        
        // draw the vertical lines
        for(int x=0;x<game.getVisualBoard().getSize();x++)
        	canvas.drawLine(stone_size/2.0f   + x*stone_size , stone_size/2.0f, stone_size/2.0f+ x*stone_size,stone_size*(float)(game.getVisualBoard().getSize()-1) +stone_size/2.0f,(touch_x==x)?gridPaint_h:gridPaint);	
        	
        // draw the horizontal lines and the legend
        for(int x=0;x<game.getVisualBoard().getSize();x++)
        {
            canvas.drawLine(stone_size/2.0f , stone_size/2.0f + x*stone_size , stone_size*(float)(game.getVisualBoard().getSize()-1)+stone_size/2.0f ,stone_size/2.0f+ x*stone_size, (touch_y==x)?gridPaint_h:gridPaint);
            
            if (GoPrefs.getLegendEnabled()) {
            	canvas.drawText("" + (1+x) , 6+ stone_size*(float)(game.getVisualBoard().getSize()-1)+stone_size/2.0f ,stone_size/2.0f+ x*stone_size+gridPaint.getTextSize()/3,gridPaint);
            	
            	if ((x>7)&&GoPrefs.getLegendSGFMode())
            		canvas.drawText("" + (char)('A'+(x+1)) , stone_size/2.0f+ x*stone_size,stone_size*(float)(game.getVisualBoard().getSize()-1) +stone_size/2.0f + 1 + gridPaint.getTextSize() ,gridPaint);
            	else
            		canvas.drawText("" + (char)('A'+x) , stone_size/2.0f+ x*stone_size,stone_size*(float)(game.getVisualBoard().getSize()-1) +stone_size/2.0f + 1 + gridPaint.getTextSize() ,gridPaint);
            }
        }
                
        
        
        for(byte x=0;x<game.getVisualBoard().getSize();x++)
            for(byte y=0;y<game.getVisualBoard().getSize();y++)
            {
            	blackPaint.setColor(0xFF000000);
            	blackPaint.setStrokeWidth(stone_size/12);
            	//blackPaint.setStyle(Paint.Style) .setStrokeWidth(stone_size/12);
            	
            	if (game.isPosHoschi(x, y))
            		canvas.drawCircle( stone_size/2.0f+ x*stone_size +0.5f ,stone_size/2.0f+y*stone_size+0.5f,stone_size/10,blackPaint );
            	
            	 
            	// paint the territory with alpha transparent stones
                if (game.isFinished()) { 

            		blackPaint.setColor(0x77000000);
            		whitePaint.setColor(0x77CCCCCC);
            		
                	if (game.area_assign[x][y]==GoDefinitions.PLAYER_BLACK)
                		canvas.drawBitmap(black_stone_bitmap, x*stone_size  ,y*stone_size,whitePaint );
                		
                        
                   	if (game.area_assign[x][y]==GoDefinitions.PLAYER_WHITE)
                		canvas.drawBitmap(white_stone_bitmap, x*stone_size  ,y*stone_size,whitePaint );
                   		
                			
                }
                
            	
            	
            	blackPaint.setColor(0xFF000000);
        		whitePaint.setColor(0xFFCCCCCC);
        		
            	if (game.getCalcBoard().isCellDead(x,y))
            	{
            		if (game.getVisualBoard().isCellWhite(x,y))
            			canvas.drawBitmap(white_stone_bitmap_small, x*stone_size  + (stone_size-white_stone_bitmap_small.getWidth())/2 ,y*stone_size + (stone_size-white_stone_bitmap_small.getHeight())/2,whitePaint );
            		
            		if (game.getVisualBoard().isCellBlack(x,y))
            			canvas.drawBitmap(black_stone_bitmap_small, x*stone_size  + (stone_size-black_stone_bitmap_small.getWidth())/2 ,y*stone_size + (stone_size-black_stone_bitmap_small.getHeight())/2,whitePaint );
            		
            	}
            	else
            	{
            		if (game.getVisualBoard().isCellWhite(x,y))
            			canvas.drawBitmap(white_stone_bitmap, x*stone_size  ,y*stone_size,whitePaint );
            		if (game.getVisualBoard().isCellBlack(x,y))
            			canvas.drawBitmap(black_stone_bitmap, x*stone_size  ,y*stone_size,whitePaint );
            
            		if (GoPrefs.getMarkLastStone()) { // if the last stone should be marked
            			blackPaint.setStyle(Paint.Style.STROKE);
            			whitePaint.setStyle(Paint.Style.STROKE);
            			whitePaint.setStrokeWidth(2.0f);
            			blackPaint.setStrokeWidth(2.0f);
            		
            			if ((game.getActMove().getX()==x)&&(game.getActMove().getY()==y))
            			{
            				if (game.getVisualBoard().isCellWhite(x,y))
            					canvas.drawCircle( stone_size/2.0f+ x*stone_size  ,stone_size/2.0f+y*stone_size,stone_size/4.0f,blackPaint );
                			if (game.getVisualBoard().isCellBlack(x,y))
            					canvas.drawCircle( stone_size/2.0f+ x*stone_size  ,stone_size/2.0f+y*stone_size,stone_size/4.0f,whitePaint );
            	
            			}
            		}
            	}

            	
            }

        FontMetrics fm=whiteTextPaint.getFontMetrics();
        
        // paint the markers
        for (GoMarker marker:game.getActMove().getMarkers())
        	        
        if (game.getVisualBoard().isCellBlack(marker.getX(),marker.getY()))
    		canvas.drawText( marker.getText() , marker.getX()*stone_size + stone_size/2.0f ,marker.getY()*stone_size-(fm.top+fm.bottom) ,whiteTextPaint );
    	else
    		canvas.drawText( marker.getText() , marker.getX()*stone_size + stone_size/2.0f ,marker.getY()*stone_size-(fm.top+fm.bottom) ,blackTextPaint );
        
        
        canvas.restore();
    } // end of onDraw
    
    
    
    public void regenerate_images() {
   
    	try {

    	if (game==null)
    		return;
    	    	
    	width_is_max=(this.getWidth()<=this.getHeight());
       
        if (this.getWidth()<=this.getHeight())
            stone_size_normal=this.getWidth()/(float)game.getVisualBoard().getSize();
        else
            stone_size_normal=this.getHeight()/(float)game.getVisualBoard().getSize();
       
        stone_size=stone_size_normal;
        stone_size_zoomed=stone_size_normal*2;

        
    	Log.i("gobandrod","regenerating images to stone size " + stone_size + "  " +regenerate_stones_flag); 
    	float SMALL_STONE_SCALER=0.6f;	
    	white_stone_bitmap=GOSkin.getWhiteStone(stone_size);
    	black_stone_bitmap=GOSkin.getBlackStone(stone_size);
    	white_stone_bitmap_small=GOSkin.getWhiteStone(SMALL_STONE_SCALER*stone_size);
    	black_stone_bitmap_small=GOSkin.getBlackStone(SMALL_STONE_SCALER*stone_size);
    
    	bg_bitmap=GOSkin.getBoard(this.getWidth(),this.getHeight());
    	regenerate_stones_flag=false;
    	
    	whiteTextPaint.setTextSize(stone_size);
    	blackTextPaint.setTextSize(stone_size);
    	
    	invalidate();
    	}
    	catch (Exception e) {
    		
    		Logger.i("Exception " + e);
    	}
    }	
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	regenerate_images();        
    }


    public void doTouch( MotionEvent event) {
    	
    	if (game==null)
    		return;
    	
    	float virtualTouchX=event.getX()-offset_x;
    	float virtualTouchY=event.getY()-offset_y;
    	
    	
    	float board_size=stone_size*game.getVisualBoard().getSize();

    	if ((virtualTouchY<board_size)&&(virtualTouchX<board_size)) // if user put his finger on the board
        {
        	touch_x=(byte)(virtualTouchX/stone_size);
    		touch_y=(byte)(virtualTouchY/stone_size);
    		if (event.getAction()==MotionEvent.ACTION_UP)
    		{
    		if (isZoomed()||(!GoPrefs.getFatFingerEnabled()))
    		{
    			if (!game.do_move(touch_x,touch_y))	;
        		touch_x=-1;
        		touch_y=-1;
        		
        		setZoom(false);
        		stone_size=stone_size_normal;
        		
    		}
    		else
    			setZoom(true);
    		}
        }
        invalidate();  // the board looks diffrent after a move
     }

    public void setGame(GoGame game) {
    	this.game=game;
    	Logger.i("set game" + game);
    	regenerate_images();
    	
    }
}