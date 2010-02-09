package org.ligi.gobandroid.ui;

import org.ligi.gobandroid.logic.GoDefinitions;
import org.ligi.gobandroid.logic.GoGame;

import org.ligi.gobandroid.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * Class to visualy represent a Go Board in Android
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         
 * This software is licenced with GPLv3 
 */
public class GoBoardView extends View implements OnTouchListener{
   	private GoGame game;

	private Paint whitePaint;
	private Paint blackPaint;
	private Paint boardPaint;
	private Paint gridPaint;
	private Paint gridPaint_h; // highlighted for cursor
    
    private Paint textPaint;
    
    private float stone_size;
    private float stone_size_zoomed;
    private float stone_size_normal;
    
    private float offset_x=0.0f;
    private float offset_y=0.0f;
    
    //TODO rename - nami is now missleading
    public byte touch_x=-1;
    public byte touch_y=-1;

    
    public boolean do_skin=false;
    public boolean do_zoom=false;
    public String skin_name="";
    
    public String skin_path="/sdcard/gobandroid/skins/";
    private Bitmap bg_bitmap=null;
    
    
    private Bitmap white_stone_bitmap=null;
    private Bitmap black_stone_bitmap=null;
    private Bitmap white_stone_bitmap_small=null;
    private Bitmap black_stone_bitmap_small=null;
    
    
    public GoBoardView( Context context,GoGame game ) {
        super( context );
        this.game=game;
      //  this.board=game.getVisualBoard();
        
        whitePaint=new Paint();
        whitePaint.setColor(0xFFCCCCCC);
        whitePaint.setAntiAlias(true);
        
        blackPaint=new Paint();
        blackPaint.setColor(0xFF000000);
        blackPaint.setTextAlign(Paint.Align.CENTER  );
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
    	
    	regenerate_stone_images();	
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
    	
    	if (bg_bitmap!=null)
    		canvas.drawBitmap(bg_bitmap,0.0f, 0.0f, boardPaint);
    	else
    		canvas.drawRect(new RectF(0,0,this.getWidth(),this.getHeight()),boardPaint );
    	
    	
		if (!isZoomed()) {
			int txt_anchor_x = 0;
			int txt_anchor_y = 0;

			if (width_is_max) {
				txt_anchor_x = 10;
				txt_anchor_y = this.getWidth() + (int) textPaint.getTextSize()
						* 2;
				;
			} else {
				txt_anchor_x = this.getHeight() + (int) textPaint.getTextSize()
						* 2;
				txt_anchor_y = 20;
			}
			float spacer = textPaint.getTextSize() * 1.5f;
			
			if (game.isFinished())
				canvas.drawText("Game is finished - Mark Dead Stones",
						txt_anchor_x, txt_anchor_y + 0 * spacer, textPaint);
			else {
				if (game.isLastActionPass())
					canvas.drawText((game.isBlackToMove() ? "black" : "white")
							+ " to move ("
							+ (!game.isBlackToMove() ? "black" : "white")
							+ " passed)", txt_anchor_x, txt_anchor_y + 0
							* spacer, textPaint);
				else
					canvas.drawText((game.isBlackToMove() ? "black" : "white")
							+ " to move", txt_anchor_x, txt_anchor_y + 0
							* spacer, textPaint);
			}
			txt_anchor_y+=spacer;
			canvas.drawText("Move: " + (game.moves.size() + 1), txt_anchor_x,
					txt_anchor_y , textPaint);
			txt_anchor_y+=spacer;
			
			if (!game.isFinished()) {
				canvas.drawText("Captures black: " + game.getCapturesBlack(),
						txt_anchor_x, txt_anchor_y, textPaint);
				txt_anchor_y+=spacer;
				canvas.drawText("Captures white: " + game.getCapturesWhite(),
						txt_anchor_x, txt_anchor_y, textPaint);
			
				txt_anchor_y+=spacer;	
			}
			else
			{
			canvas.drawText("Black: " + game.territory_black + " (Territory) + " + game.getCapturesBlack() + " (Captures) =" + game.getPointsBlack() ,
					txt_anchor_x, txt_anchor_y , textPaint);
			txt_anchor_y+=spacer;	
			canvas.drawText("White: " + game.territory_white + " (Territory) + "+ game.getCapturesWhite() + " (Captures) + " + game.getKomi() + " (Komi) =" + game.getPointsWhite(),
					txt_anchor_x, txt_anchor_y , textPaint);
			txt_anchor_y+=spacer;	
			
			if (game.getPointsBlack()==game.getPointsWhite())
				canvas.drawText("The Game ended in a draw",
						txt_anchor_x, txt_anchor_y , textPaint);
			if (game.getPointsBlack()>game.getPointsWhite())
				canvas.drawText("Black won with " + (game.getPointsBlack()-game.getPointsWhite()) + " Points.",
						txt_anchor_x, txt_anchor_y , textPaint);
			
			if (game.getPointsWhite()>game.getPointsBlack())
				canvas.drawText("White won with " + (game.getPointsWhite()-game.getPointsBlack()) + " Points.",
						txt_anchor_x, txt_anchor_y , textPaint);
			txt_anchor_y+=spacer;	
			}
			if (touch_x != -1)
				canvas.drawText("Touch: " + (char) ('A' + touch_x)
						+ (touch_y + 1), txt_anchor_x, txt_anchor_y 
						, textPaint);
			txt_anchor_y+=spacer;	
		}
        
        canvas.translate(offset_x, offset_y);
        for(int x=0;x<game.getVisualBoard().getSize();x++)
        	{
        	if (touch_x==x)
        		canvas.drawLine(stone_size/2.0f   + x*stone_size , stone_size/2.0f, stone_size/2.0f+ x*stone_size,stone_size*(float)(game.getVisualBoard().getSize()-1) +stone_size/2.0f,gridPaint_h);	
            else
            	canvas.drawLine(stone_size/2.0f   + x*stone_size , stone_size/2.0f, stone_size/2.0f+ x*stone_size,stone_size*(float)(game.getVisualBoard().getSize()-1) +stone_size/2.0f,gridPaint);
            
        	
        	}
                
        for(int x=0;x<game.getVisualBoard().getSize();x++)
        {
            if (touch_y==x)
            	canvas.drawLine(stone_size/2.0f , stone_size/2.0f + x*stone_size , stone_size*(float)(game.getVisualBoard().getSize()-1)+stone_size/2.0f ,stone_size/2.0f+ x*stone_size,gridPaint_h);
            else
            	canvas.drawLine(stone_size/2.0f , stone_size/2.0f + x*stone_size , stone_size*(float)(game.getVisualBoard().getSize()-1)+stone_size/2.0f ,stone_size/2.0f+ x*stone_size,gridPaint);
            
            canvas.drawText("" + (1+x) , 6+ stone_size*(float)(game.getVisualBoard().getSize()-1)+stone_size/2.0f ,stone_size/2.0f+ x*stone_size+gridPaint.getTextSize()/3,gridPaint);
            canvas.drawText("" + (char)('A'+x) , stone_size/2.0f+ x*stone_size,stone_size*(float)(game.getVisualBoard().getSize()-1) +stone_size/2.0f + 1 + gridPaint.getTextSize() ,gridPaint);
        }
                
        
        
        for(byte x=0;x<game.getVisualBoard().getSize();x++)
            for(byte y=0;y<game.getVisualBoard().getSize();y++)
            {
            	blackPaint.setColor(0xFF000000);
            	blackPaint.setStrokeWidth(stone_size/12);
            	//blackPaint.setStyle(Paint.Style) .setStrokeWidth(stone_size/12);
            	
            	if (game.isPosHoschi(x, y))
            			canvas.drawCircle( stone_size/2.0f+ x*stone_size +0.5f ,stone_size/2.0f+y*stone_size+0.5f,stone_size/10,blackPaint );
            	
            	 
                if (game.isFinished()) {

            		blackPaint.setColor(0x77000000);
            		whitePaint.setColor(0x77CCCCCC);
            		
                	if (game.area_assign[x][y]==GoDefinitions.PLAYER_BLACK)
                			{
                    	if (black_stone_bitmap!=null)
                			canvas.drawBitmap(black_stone_bitmap, x*stone_size  ,y*stone_size,whitePaint );
                		else
                			canvas.drawCircle( x*stone_size + stone_size/2.0f ,y*stone_size+stone_size/2.0f,stone_size/2,blackPaint );
                        //canvas.drawText( "" + game.getGroup(x,y), x*stone_size + stone_size/2.0f ,y*stone_size+stone_size/2.0f ,whitePaint );
                        }		
             
                   	if (game.area_assign[x][y]==GoDefinitions.PLAYER_WHITE)
        			{
                		if (white_stone_bitmap!=null)
                			canvas.drawBitmap(white_stone_bitmap, x*stone_size  ,y*stone_size,whitePaint );
                		else
                			canvas.drawCircle( x*stone_size + stone_size/2.0f ,y*stone_size+stone_size/2.0f,stone_size/2,whitePaint );
                
        			}
                	
                			
                }
                
            	
            	
            	blackPaint.setColor(0xFF000000);
        		whitePaint.setColor(0xFFCCCCCC);
        		
            	if (game.getCalcBoard().isCellDead(x,y))
            	{
            		if (game.getVisualBoard().isCellWhite(x,y))
                    {
            		if (white_stone_bitmap_small!=null)
            			canvas.drawBitmap(white_stone_bitmap_small, x*stone_size  + (stone_size-white_stone_bitmap_small.getWidth())/2 ,y*stone_size + (stone_size-white_stone_bitmap_small.getHeight())/2,whitePaint );
            		else
            			canvas.drawCircle( x*stone_size + stone_size/2.0f ,y*stone_size+stone_size/2.0f,stone_size/5,whitePaint );
                    }
                if (game.getVisualBoard().isCellBlack(x,y))
                    {
                	if (black_stone_bitmap_small!=null)
                		canvas.drawBitmap(black_stone_bitmap_small, x*stone_size  + (stone_size-black_stone_bitmap_small.getWidth())/2 ,y*stone_size + (stone_size-black_stone_bitmap_small.getHeight())/2,whitePaint );
            		else
            			canvas.drawCircle( x*stone_size + stone_size/2.0f ,y*stone_size+stone_size/2.0f,stone_size/5,blackPaint );
                    }
            	}
            	else
            	{
            	if (game.getVisualBoard().isCellWhite(x,y))
                    {
            		if (white_stone_bitmap!=null)
            			canvas.drawBitmap(white_stone_bitmap, x*stone_size  ,y*stone_size,whitePaint );
            		else
            			canvas.drawCircle( x*stone_size + stone_size/2.0f ,y*stone_size+stone_size/2.0f,stone_size/2,whitePaint );
                    }
                if (game.getVisualBoard().isCellBlack(x,y))
                    {
                	if (black_stone_bitmap!=null)
            			canvas.drawBitmap(black_stone_bitmap, x*stone_size  ,y*stone_size,whitePaint );
            		else
            			canvas.drawCircle( x*stone_size + stone_size/2.0f ,y*stone_size+stone_size/2.0f,stone_size/2,blackPaint );
                    }
            	}
                
               
                //canvas.drawText( "" + game.area_groups[x][y], x*stone_size + stone_size/2.0f ,y*stone_size+stone_size/2.0f ,whitePaint );
                
            }
    
        canvas.restore();
    } // end of onDraw
    
    boolean width_is_max;
    
    public void regenerate_stone_images() {
    	if (do_skin){
    		int size_append=17;
    		if (stone_size>23)
    			size_append=32;
    		if (stone_size>50)
    			size_append=64;
    		
    		
    		white_stone_bitmap=Bitmap.createScaledBitmap(BitmapFactory.decodeFile(skin_path +"/" + skin_name + "/white" + size_append + ".png"
				), (int)stone_size, (int)stone_size, true);
    		
    		black_stone_bitmap=Bitmap.createScaledBitmap(BitmapFactory.decodeFile(skin_path +"/" + skin_name + "/black" + size_append + ".png"
				), (int)stone_size, (int)stone_size, true);

    		float SMALL_STONE_SCALER=0.6f;	
    		 size_append=17;
     		if (stone_size/SMALL_STONE_SCALER>23)
     			size_append=32;
     		if (stone_size/SMALL_STONE_SCALER>50)
     			size_append=64;

    		white_stone_bitmap_small=Bitmap.createScaledBitmap(BitmapFactory.decodeFile(skin_path +"/" + skin_name + "/white" + size_append + ".png"
    		), (int)(stone_size/SMALL_STONE_SCALER), (int)(stone_size/SMALL_STONE_SCALER), true);
    		
    		black_stone_bitmap_small=Bitmap.createScaledBitmap(BitmapFactory.decodeFile(skin_path +"/"+ skin_name + "/black" + size_append + ".png"
    		), (int)(stone_size/SMALL_STONE_SCALER), (int)(stone_size/SMALL_STONE_SCALER), true);

    	}
    	else 
    	{
    		black_stone_bitmap=null;
    		white_stone_bitmap=null;
    	}
    }	
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	width_is_max=(w<=h);
       
        if (w<=h)
            stone_size_normal=w/(float)game.getVisualBoard().getSize();
        else
            stone_size_normal=h/(float)game.getVisualBoard().getSize();
       
        stone_size=stone_size_normal;
        stone_size_zoomed=stone_size_normal*2;


        if (do_skin)
        	bg_bitmap=Bitmap.createScaledBitmap(BitmapFactory.decodeFile("/sdcard/gobandroid/skins/" + skin_name + "/board.jpg"), this.getWidth(), this.getHeight(), true);
        else
        	bg_bitmap=null;
        
        regenerate_stone_images();

        invalidate(); // needed here or automaticaly called?
    }

    
    public boolean onTouch( View v, MotionEvent event ) {
    	
    	float virtualTouchX=event.getX()-offset_x;
    	float virtualTouchY=event.getY()-offset_y;
    	
    	
    	float board_size=stone_size*game.getVisualBoard().getSize();

    	if ((virtualTouchY<board_size)&&(virtualTouchX<board_size)) // if user put his finger on the board
        {
        	touch_x=(byte)(virtualTouchX/stone_size);
    		touch_y=(byte)(virtualTouchY/stone_size);
    		if (event.getAction()==MotionEvent.ACTION_UP)
    		{
    		if (isZoomed()||(!do_zoom))
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
        return true;
    }
    
}