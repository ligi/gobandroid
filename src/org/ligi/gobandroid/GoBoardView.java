package org.ligi.gobandroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.KeyEvent;
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

    //GoBoard board;
    GoGame game;

    Paint whitePaint;
    Paint blackPaint;
    Paint boardPaint;
    Paint gridPaint;
    Paint gridPaint_h; // highlighted for cursor
    
    Paint textPaint;
    
    float stone_size;
    float stone_size_zoomed;
    float stone_size_normal;
    
    float offset_x=0.0f;
    float offset_y=0.0f;
    
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

    public void prepare_keyinput() {
    	if (touch_x==-1) touch_x=0;
    	if (touch_y==-1) touch_y=0;
    }
    @Override 
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	switch (keyCode) {
    	case KeyEvent.KEYCODE_DPAD_UP:
    		prepare_keyinput();
    		if (touch_y>0) touch_y--;
    		break;
    	
    	case KeyEvent.KEYCODE_DPAD_LEFT:
    		prepare_keyinput();
    		if (touch_x>0) touch_x--;
    		break;
    	
    	case KeyEvent.KEYCODE_DPAD_DOWN:
    		prepare_keyinput();
    		if (touch_y<game.getVisualBoard().getSize()) touch_y++;
    		break;
    	
    	case KeyEvent.KEYCODE_DPAD_RIGHT:
    		prepare_keyinput();
    		if (touch_x<game.getVisualBoard().getSize()) touch_x++;
    		break;
    		
    	case KeyEvent.KEYCODE_DPAD_CENTER:
    		if (!game.do_move(touch_x,touch_y))	;
    		
    		offset_x=0;
    		offset_y=0;
        
    		break;
    	}
    	invalidate();
    	return true;	
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
                canvas.drawRect(new RectF(0,0,this.getWidth(),this.getHeight()),boardPaint );
        int txt_anchor_x=0;
        int txt_anchor_y=0;
        
        if (width_is_max)
        	{
        	txt_anchor_x=10;
        	txt_anchor_y=this.getWidth()+(int)textPaint.getTextSize()*2;;
        	}
        else        	
        {
        	txt_anchor_x=this.getHeight()+(int)textPaint.getTextSize()*2;
        	txt_anchor_y=20;
        	}
        float spacer=textPaint.getTextSize()*1.5f;
        if (game.isFinished())
        	canvas.drawText("Game is finished - Mark Dead Stones",txt_anchor_x,txt_anchor_y + 0*spacer,textPaint);
        else
        {
        	if (game.isLastActionPass())
        		canvas.drawText((game.isBlackToMove()?"black":"white")+" to move (" +(!game.isBlackToMove()?"black":"white") + " passed)",txt_anchor_x,txt_anchor_y + 0*spacer,textPaint);
        	else
        		canvas.drawText((game.isBlackToMove()?"black":"white")+" to move",txt_anchor_x,txt_anchor_y + 0*spacer,textPaint);
        }
        
        
        canvas.drawText("Move: " + (game.moves.size()+1),txt_anchor_x,txt_anchor_y + 1*spacer,textPaint);
        canvas.drawText("Captures black: " + game.getCapturesBlack(),txt_anchor_x,txt_anchor_y + 2*spacer,textPaint);
        canvas.drawText("Captures white: " + game.getCapturesWhite(),txt_anchor_x,txt_anchor_y + 3*spacer,textPaint);
                
        if (touch_x!=-1)
        canvas.drawText("Touch: " + (char)('A'+touch_x) + (touch_y+1),txt_anchor_x,txt_anchor_y + 4*spacer,textPaint);
        
        
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
            			canvas.drawCircle( stone_size/2.0f+ x*stone_size +1.0f ,stone_size/2.0f+y*stone_size+1.0f,stone_size/10,blackPaint );
            	
            	if (game.isStoneDead(x, y))
            	{
            		blackPaint.setColor(0xBB000000);
            		whitePaint.setColor(0xBBCCCCCC);
            	}
            	else
            	{
            		blackPaint.setColor(0xFF000000);
            		whitePaint.setColor(0xFFCCCCCC);
            	}
            	if (game.getVisualBoard().isCellWhite(x,y))
                    {
            		canvas.drawCircle( x*stone_size + stone_size/2.0f ,y*stone_size+stone_size/2.0f,stone_size/2,whitePaint );
                    //canvas.drawText( "" + game.getGroup(x,y) +"-" + (game.group_has_liberty(game.getGroup( x,y))?"x":"-"), x*stone_size + stone_size/2.0f ,y*stone_size+stone_size/2.0f ,blackPaint );
                    }
                if (game.getVisualBoard().isCellBlack(x,y))
                    {
                    canvas.drawCircle( x*stone_size + stone_size/2.0f ,y*stone_size+stone_size/2.0f,stone_size/2,blackPaint );
                    //canvas.drawText( "" + game.getGroup(x,y), x*stone_size + stone_size/2.0f ,y*stone_size+stone_size/2.0f ,whitePaint );
                    }
            }
    
    canvas.restore();
    }
    
    boolean width_is_max;
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	width_is_max=(w<=h);
    	
        if (w<=h)
            stone_size_normal=w/(float)game.getVisualBoard().getSize();
        else
            stone_size_normal=h/(float)game.getVisualBoard().getSize();
       
        stone_size=stone_size_normal;
        stone_size_zoomed=stone_size_normal*3;
        invalidate(); // needed here?
    }

    byte touch_x=-1;
    byte touch_y=-1;
    
    
    
    
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
    		if (stone_size==stone_size_normal)
    		{
    			stone_size=stone_size_zoomed;
    			
    			
    			if (touch_x>= (2*game.getVisualBoard().getSize())/3)
    				offset_x=-stone_size*game.getVisualBoard().getSize()+this.getWidth();
    			else if (touch_x> (game.getVisualBoard().getSize()/3))
    				offset_x=(-stone_size*game.getVisualBoard().getSize()+this.getWidth())/2;	
    			
    			if (touch_y>= (2*game.getVisualBoard().getSize())/3)
    				offset_y=-stone_size*game.getVisualBoard().getSize()+this.getWidth();
    			else if (touch_y> (game.getVisualBoard().getSize()/3))
    				offset_y=(-stone_size*game.getVisualBoard().getSize()+this.getWidth())/2;	
    			
    			
    			Log.i("gobandroid","offset_x"+offset_x);
    		}
    		else
        		{
        		if (!game.do_move(touch_x,touch_y))	;
        		touch_x=-1;
        		touch_y=-1;
        		
        		stone_size=stone_size_normal;
        		offset_x=0;
        		offset_y=0;
            	
        		}
        	
    		}
        	
        }
        invalidate();  // the board looks diffrent after a move
        return true;
    }
    
}