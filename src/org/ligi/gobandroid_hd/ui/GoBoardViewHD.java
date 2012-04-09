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

package org.ligi.gobandroid_hd.ui;

import java.io.File;
import java.io.FileOutputStream;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoDefinitions;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGameProvider;
import org.ligi.gobandroid_hd.logic.GoMarker;
import org.ligi.tracedroid.logging.Log;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Class to visually represent a Go Board in Android
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         
 * This software is licensed with GPLv3 
 */
public class GoBoardViewHD extends View {
	
	private int zoom_poi=-1;
	
	//public boolean grid_embos=true; //  GoPrefs.getGridEmbossEnabled()
	public boolean do_legend=true; 
	public boolean do_line_highlight=true;
	public boolean do_mark_act=true;
	public boolean mark_last_stone=true;
	public boolean legend_sgf_mode=true;  //GoPrefs.getLegendSGFMode()
	
	private Paint whitePaint;
	private Paint blackPaint;
	private Paint blackTextPaint;
	private Paint whiteTextPaint;
	private Paint boardPaint;
	private Paint gridPaint=new Paint();
	private Paint gridPaint_h; // highlighted for cursor
    
    private Paint textPaint;
    private Paint bitmapPaint;
    private Paint placeStonePaint;
    
    public float stone_size;
    private float stone_size_normal;
    
    private Bitmap white_stone_bitmap=null;
    private Bitmap black_stone_bitmap=null;
    private Bitmap white_stone_bitmap_small=null;
    private Bitmap black_stone_bitmap_small=null;
    
    public boolean move_stone_mode=false;
    
    private boolean regenerate_stones_flag=true;
    
    private boolean enforce_square=true;
    
    private float zoom=1.0f;
    
    public GoBoardViewHD(Context context) {
    	super( context );
    	init();
    }
    
    public GoBoardViewHD(Context context,boolean square,float zoom) {
    	super( context );
    	this.zoom=zoom;
    	enforce_square=square;
    	init();
    }
    
    public GoBoardViewHD(Context context, AttributeSet attrs) {
    	super( context ,attrs);
    	init();
    }
    
    public void init() {
    	
        // set up the paints
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
        
        gridPaint_h=new Paint();
        gridPaint_h.setColor(0xFF0000FF);
        gridPaint_h.setShadowLayer(1,1,1,0xFFFFFFFF );
        
        gridPaint.setColor(0xFF000000);

        setGridEmboss(true); // default
        gridPaint.setTextAlign(Paint.Align.CENTER );
        gridPaint.setTextSize(12.0f );
    
        textPaint=new Paint();
        textPaint.setColor(0xFF000000);
        textPaint.setAntiAlias(false);

        bitmapPaint=new Paint();
        placeStonePaint=new Paint();
        placeStonePaint.setAlpha(127);
        
        setFocusable(true);   
        
        if (getGame()==null)
        	GoGameProvider.setGame(new GoGame((byte)19));
    }

    public GoGame getGame() {
    	return GoGameProvider.getGame();
    }
    
	public void prepare_keyinput() {
    	if (GoInteractionProvider.getTouchPosition()<0)
    		GoInteractionProvider.setTouchPosition(0);
    }


    /**
     * set the zoom factor - 1.0 ( default ) means no zoom 
     * 
     * @param zoom
     */
    public void setZoom(float zoom) {
    	this.zoom=zoom;
    	setSize(this.getWidth(),this.getHeight());
    }
    
    
    public PointF getZoomTranslate() {
    	if (zoom<=1.0f)
    		return new PointF(0,0);
    	
		int act_zoom_poi=0;
		
		if (zoom_poi>=0) {
			act_zoom_poi=zoom_poi;
		} else if (GoInteractionProvider.getTouchPosition()>=0) {
			act_zoom_poi=GoInteractionProvider.getTouchPosition();
		} else
			Log.w("zoom requested but no POI to center around");
		
		Point act_zoom_point=getGame().linear_coordinate2Point(act_zoom_poi);
		PointF res=new PointF( -stone_size*(act_zoom_point.x-getGame().getSize()/2.0f/zoom)
							  ,-stone_size*(act_zoom_point.y-getGame().getSize()/2.0f/zoom));
		
		return res;
    }
    public void screenshot(String sshot_name) {
    	Bitmap bmp=Bitmap.createBitmap(this.getWidth(), this.getHeight(), Config.ARGB_8888);
    	Canvas c=new Canvas(bmp);
    	draw2canvas(c);
    	
    	try {
    		if (sshot_name.indexOf("://")>0)
    			sshot_name=sshot_name.substring(sshot_name.indexOf("://")+3);
    		Log.i("writing screenshot " + sshot_name);	
    		new File (sshot_name.substring(0,sshot_name.lastIndexOf("/"))).mkdirs();
    		new File (sshot_name).createNewFile();
    		FileOutputStream out = new FileOutputStream(sshot_name);
    		bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
    		out.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

    @Override
    protected void onDraw(Canvas canvas) {
    	draw2canvas(canvas);
    }
    
    private int getGameSize() {
    	return getGame().getSize();
    }
    
    protected void draw2canvas(Canvas canvas) {
    	Log.i("draw");
    	canvas.save();
    	
    	// when we have zoomed in -  center translate the canvas around the POI
    	if (zoom>1.0f) {
    		canvas.translate(getZoomTranslate().x,getZoomTranslate().y);
    	}
    		
    	if (regenerate_stones_flag)
    		regenerate_images();

    	boolean line_highlight_condition=do_line_highlight&&GoInteractionProvider.hasValidTouchCoord()&&this.isFocused();
    	
        // draw semi transparent stone on current touch pos as a shadow
    	if ((!move_stone_mode)&&do_mark_act&&GoInteractionProvider.hasValidTouchCoord()&&this.isFocused()) {
            	canvas.drawBitmap(((getGame().isBlackToMove())?black_stone_bitmap:white_stone_bitmap),
           		GoInteractionProvider.getTouchX()*stone_size, 
           		GoInteractionProvider.getTouchY()*stone_size, 
           		placeStonePaint);
    	}
    	
    	
        // draw the vertical lines
        for(byte x=0;x<getGameSize();x++)
        	canvas.drawLine(stone_size/2.0f   + x*stone_size , stone_size/2.0f, stone_size/2.0f+ x*stone_size,stone_size*(float)(getGame().getVisualBoard().getSize()-1) +stone_size/2.0f,(line_highlight_condition&&(GoInteractionProvider.getTouchX()==x))?gridPaint_h:gridPaint);	
        	
        // draw the horizontal lines and the legend
        for(byte x=0;x<getGame().getVisualBoard().getSize();x++)
        {
            canvas.drawLine(stone_size/2.0f , stone_size/2.0f + x*stone_size , stone_size*(float)(getGame().getVisualBoard().getSize()-1)+stone_size/2.0f ,stone_size/2.0f+ x*stone_size, (line_highlight_condition&&(GoInteractionProvider.getTouchY()==x))?gridPaint_h:gridPaint);
            if (do_legend) {
            	canvas.drawText("" + (getGameSize()-x) , 6+ stone_size*(float)(getGameSize()-1)+stone_size/2.0f ,stone_size/2.0f+ x*stone_size+gridPaint.getTextSize()/3,gridPaint);
            	
            	if ((x>7)&&legend_sgf_mode)
            		canvas.drawText("" + (char)('A'+(x+1)) , stone_size/2.0f+ x*stone_size,stone_size*(float)(getGameSize()-1) +stone_size/2.0f + 1 + gridPaint.getTextSize() ,gridPaint);
            	else
            		canvas.drawText("" + (char)('A'+x) , stone_size/2.0f+ x*stone_size,stone_size*(float)(getGameSize()-1) +stone_size/2.0f + 1 + gridPaint.getTextSize() ,gridPaint);
            }
        }
                
        for(byte x=0;x<getGameSize();x++)
            for(byte y=0;y<getGameSize();y++) {
            	blackPaint.setColor(0xFF000000);
            	blackPaint.setStrokeWidth(stone_size/12);
            	//blackPaint.setStyle(Paint.Style) .setStrokeWidth(stone_size/12);
            	
            	if (getGame().isPosHoschi(x, y))
            		canvas.drawCircle( stone_size/2.0f+ x*stone_size +0.5f ,stone_size/2.0f+y*stone_size+0.5f,stone_size/10,blackPaint );
            	
            	 
            	// paint the territory with alpha transparent stones
                if (getGame().isFinished()) { 

            		blackPaint.setColor(0x77000000);
            		whitePaint.setColor(0x77CCCCCC);
            		
                	if (getGame().area_assign[x][y]==GoDefinitions.PLAYER_BLACK)
                		canvas.drawBitmap(black_stone_bitmap, x*stone_size  ,y*stone_size,whitePaint );
                		
                        
                   	if (getGame().area_assign[x][y]==GoDefinitions.PLAYER_WHITE)
                		canvas.drawBitmap(white_stone_bitmap, x*stone_size  ,y*stone_size,whitePaint );
                   		
                }
            	
            	blackPaint.setColor(0xFF000000);
        		whitePaint.setColor(0xFFCCCCCC);
        		
            	if (getGame().getCalcBoard().isCellDead(x,y)) {
            		if (getGame().getVisualBoard().isCellWhite(x,y))
            			canvas.drawBitmap(white_stone_bitmap_small, x*stone_size  + (stone_size-white_stone_bitmap_small.getWidth())/2 ,y*stone_size + (stone_size-white_stone_bitmap_small.getHeight())/2,whitePaint );
            		
            		if (getGame().getVisualBoard().isCellBlack(x,y))
            			canvas.drawBitmap(black_stone_bitmap_small, x*stone_size  + (stone_size-black_stone_bitmap_small.getWidth())/2 ,y*stone_size + (stone_size-black_stone_bitmap_small.getHeight())/2,whitePaint );
            		
            	}
            	else {
            	
            		if (move_stone_mode&&(x==getGame().getActMove().getX())&&(y==getGame().getActMove().getY()))
            			bitmapPaint.setAlpha(0x77);
            		else
            			bitmapPaint.setAlpha(0xFF);
            			
            		if (getGame().getVisualBoard().isCellWhite(x,y))
            			canvas.drawBitmap(white_stone_bitmap, x*stone_size  ,y*stone_size,bitmapPaint );
            		if (getGame().getVisualBoard().isCellBlack(x,y))
            			canvas.drawBitmap(black_stone_bitmap, x*stone_size  ,y*stone_size,bitmapPaint );
            	 
            		if (mark_last_stone) { // if the last stone should be marked
            			blackPaint.setStyle(Paint.Style.STROKE);
            			whitePaint.setStyle(Paint.Style.STROKE);
            			whitePaint.setStrokeWidth(2.0f);
            			blackPaint.setStrokeWidth(2.0f);
            		
            			/** mark the last move */
            			if ((getGame().getActMove().getX()==x)&&(getGame().getActMove().getY()==y)) {
            				if (getGame().getVisualBoard().isCellWhite(x,y))
            					canvas.drawCircle( stone_size/2.0f+ x*stone_size  ,stone_size/2.0f+y*stone_size,stone_size/4.0f,blackPaint );
                			if (getGame().getVisualBoard().isCellBlack(x,y))
            					canvas.drawCircle( stone_size/2.0f+ x*stone_size  ,stone_size/2.0f+y*stone_size,stone_size/4.0f,whitePaint );
            	
            			}
            		}
            	}

            	
            }

        FontMetrics fm=whiteTextPaint.getFontMetrics();
        
        // paint the markers
        for (GoMarker marker:getGame().getActMove().getMarkers())
        	        
        if (getGame().getVisualBoard().isCellBlack(marker.getX(),marker.getY()))
    		canvas.drawText( marker.getText() , marker.getX()*stone_size + stone_size/2.0f ,marker.getY()*stone_size-(fm.top+fm.bottom) ,whiteTextPaint );
    	else
    		canvas.drawText( marker.getText() , marker.getX()*stone_size + stone_size/2.0f ,marker.getY()*stone_size-(fm.top+fm.bottom) ,blackTextPaint );
        
        
        canvas.restore();
    } // end of onDraw
 
    /**
     * @return the width/height of the Board in Pixels
     */
    public float getBoardPixels() {
    	return stone_size*getGame().getVisualBoard().getSize();
    }
    
    private Bitmap getScaledRes(float size,int resID) {
    	Bitmap unscaled_bitmap=BitmapFactory.decodeResource(this.getResources(),resID);
    	return   Bitmap.createScaledBitmap(unscaled_bitmap, (int)size, (int)size, true);
    }
    /**
     * resize the images regarding to stone_size
     */
    public void regenerate_images() {
   
    	Log.i("regenerating images to stone size " + stone_size);
    	float SMALL_STONE_SCALER=0.6f;	
    	white_stone_bitmap=getScaledRes(stone_size,R.drawable.stone_white);
    	black_stone_bitmap=getScaledRes(stone_size,R.drawable.stone_black);
    	white_stone_bitmap_small=getScaledRes(stone_size*SMALL_STONE_SCALER,R.drawable.stone_white);
    	black_stone_bitmap_small=getScaledRes(stone_size*SMALL_STONE_SCALER,R.drawable.stone_black);
    
    	regenerate_stones_flag=false;
    	
    	whiteTextPaint.setTextSize(stone_size);
    	blackTextPaint.setTextSize(stone_size);
    	
    	
    }	
    
    
    public void setGridEmboss(boolean grid_embos) {
    	if (grid_embos)
    		gridPaint.setShadowLayer(1,1,1,0xFFFFFFFF );
    	else
    		gridPaint.setShadowLayer(1,1,1,0xFF000000 );
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        setSize(w,h);
    }
    
    private void setSize(int w,int h) {
    	stone_size_normal=zoom*(((w<h)?w:h)/(float)getGame().getVisualBoard().getSize());
        stone_size=stone_size_normal;
        regenerate_stones_flag=true;
    }
    
    public void boardSizeChanged() {
    	setSize(this.getWidth(),this.getHeight());
    }
    
    public void initializeStoneMove() {
    	
    	if (getGame().getGoMover().isPlayingInThisGame()) // dont allow with a mover
    		return;									 
    	
    	if (move_stone_mode)  // already in the mode
    		return;			  // -> do nothing
		
    	move_stone_mode=true;
    	
    	// TODO check if we only want this in certain modes
    	if (GoPrefs.isAnnounceMoveActive()) {
    			
			new AlertDialog.Builder(this.getContext()).setMessage(R.string.hint_stone_move)
			.setPositiveButton(R.string.ok, 
			
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					GoPrefs.setAnnounceMoveActive(false);
				}
				}).show();
    	}
    }
   
    public void setRegenerataStonesFlag(boolean new_flag) {
    	regenerate_stones_flag=new_flag;
    }
    
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	    if (enforce_square) {
		    int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		    int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
		    int size = Math.min(parentWidth, parentHeight);
		    this.setMeasuredDimension(size,size);
	    }
	}
	
	public void setZoomPOI(int zoom_poi) {
		this.zoom_poi=zoom_poi;
		// TODO check use-cases if we need to invalidate here
	}
	
	public int pixel2boardPos(float x,float y) {
		return (int)((x-getZoomTranslate().x)/(stone_size) //x
				+(int)(((y-getZoomTranslate().y))/(stone_size))*getGame().getSize());
	}
}