package org.ligi.gobandroid;

import android.content.Context;
import android.graphics.Canvas;
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

    //GoBoard board;
    GoGame game;

    Paint whitePaint;
    Paint blackPaint;
    Paint boardPaint;
    Paint gridPaint;
    Paint textPaint;
    
    float stone_size;
    
    public GoBoardView( Context context,GoGame game ) {
        super( context );
        this.game=game;
      //  this.board=game.getVisualBoard();
        
        whitePaint=new Paint();
        whitePaint.setColor(0xCCCCCCCC);
        whitePaint.setAntiAlias(true);
        
        blackPaint=new Paint();
        blackPaint.setColor(0xCC000000);
        blackPaint.setTextAlign(Paint.Align.CENTER  );
        blackPaint.setAntiAlias(true);
        
        boardPaint=new Paint();
        boardPaint.setColor(0xFFc6b460);

        gridPaint=new Paint();
        
        //gridPaint.setColor(0xFFFFFFFF);
        //gridPaint.setShadowLayer(1,1,1,0xFF000000 );
        
        gridPaint.setColor(0xFF000000);
        gridPaint.setShadowLayer(1,1,1,0xFFFFFFFF );
        gridPaint.setTextAlign(Paint.Align.CENTER );
        gridPaint.setTextSize(12.0f );

    
        textPaint=new Paint();
        textPaint.setColor(0xFF000000);
        textPaint.setAntiAlias(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
                canvas.drawRect(new RectF(0,0,this.getWidth(),this.getHeight()),boardPaint );
        
        for(int x=0;x<game.getVisualBoard().getSize();x++)
        	canvas.drawLine(stone_size/2.0f   + x*stone_size , stone_size/2.0f, stone_size/2.0f+ x*stone_size,stone_size*(float)(game.getVisualBoard().getSize()-1) +stone_size/2.0f,gridPaint);
                
        for(int x=0;x<game.getVisualBoard().getSize();x++)
        {
            canvas.drawLine(stone_size/2.0f , stone_size/2.0f + x*stone_size , stone_size*(float)(game.getVisualBoard().getSize()-1)+stone_size/2.0f ,stone_size/2.0f+ x*stone_size,gridPaint);
            canvas.drawText("" + (1+x) , 6+ stone_size*(float)(game.getVisualBoard().getSize()-1)+stone_size/2.0f ,stone_size/2.0f+ x*stone_size+gridPaint.getTextSize()/3,gridPaint);
            canvas.drawText("" + (char)('A'+x) , stone_size/2.0f+ x*stone_size,stone_size*(float)(game.getVisualBoard().getSize()-1) +stone_size/2.0f + 1 + gridPaint.getTextSize() ,gridPaint);
        }
        
        
        for(byte x=0;x<game.getVisualBoard().getSize();x++)
            for(byte y=0;y<game.getVisualBoard().getSize();y++)
            {
                if (game.getVisualBoard().isCellWhite(x,y))
                    {
                    canvas.drawCircle( x*stone_size + stone_size/2.0f ,y*stone_size+stone_size/2.0f,stone_size/2,whitePaint );
                    canvas.drawText( "" + game.getGroup(x,y) +"-" + (game.group_has_liberty(game.getGroup( x,y))?"x":"-"), x*stone_size + stone_size/2.0f ,y*stone_size+stone_size/2.0f ,blackPaint );
                    }
                if (game.getVisualBoard().isCellBlack(x,y))
                    {
                    canvas.drawCircle( x*stone_size + stone_size/2.0f ,y*stone_size+stone_size/2.0f,stone_size/2,blackPaint );
                    canvas.drawText( "" + game.getGroup(x,y), x*stone_size + stone_size/2.0f ,y*stone_size+stone_size/2.0f ,whitePaint );
                    }
                
        
            }
    
        
        int txt_anchor_x=0;
        int txt_anchor_y=0;
        
        if (width_is_max)
        	{
        	txt_anchor_x=10;
        	txt_anchor_y=this.getWidth()+10;
        	}
        else        	
        {
        	txt_anchor_x=this.getHeight()+10;
        	txt_anchor_y=20;
        	}
        float spacer=textPaint.getTextSize()*1.5f;
        if (game.isFinished())
        	canvas.drawText("Game is finished",txt_anchor_x,txt_anchor_y + 0*spacer,textPaint);
        else
        {
        	if (game.isLastActionPass())
        		canvas.drawText((game.isBlackToMove()?"black":"white")+" to move (" +(!game.isBlackToMove()?"black":"white") + " passed)",txt_anchor_x,txt_anchor_y + 0*spacer,textPaint);
        	else
        		canvas.drawText((game.isBlackToMove()?"black":"white")+" to move",txt_anchor_x,txt_anchor_y + 0*spacer,textPaint);
        }
        
        
        canvas.drawText("Captures black: " + game.getCapturesBlack(),txt_anchor_x,txt_anchor_y + 1*spacer,textPaint);
        canvas.drawText("Captures white: " + game.getCapturesWhite(),txt_anchor_x,txt_anchor_y + 2*spacer,textPaint);
        
        if (touch_x!=-1)
        canvas.drawText("Touch: " + (char)('A'+touch_x) + (touch_y+1),txt_anchor_x,txt_anchor_y + 3*spacer,textPaint);
    }
    
    boolean width_is_max;
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	width_is_max=(w<=h);
    	
        if (w<=h)
            stone_size=w/(float)game.getVisualBoard().getSize();
        else
            stone_size=h/(float)game.getVisualBoard().getSize();
        invalidate(); // needed here?
    }

    byte touch_x=-1;
    byte touch_y=0;
    
    public boolean onTouch( View v, MotionEvent event ) {

        
    	
    	
        if ((event.getY()<stone_size*game.getVisualBoard().getSize())&&(event.getX()<stone_size*game.getVisualBoard().getSize())) // if user put his finger on the board
        {
        	
        	touch_x=(byte)(event.getX()/stone_size);
    		touch_y=(byte)(event.getY()/stone_size);
    		
        	if (event.getAction()==MotionEvent.ACTION_UP)
        		{
        		if (!game.do_move(touch_x,touch_y))	;
        		touch_x=-1;
        		}
        	
        	
        	
        }
        invalidate();  // the board looks diffrent after a move
        return true;
    }
    
}