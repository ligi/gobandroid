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
    
    float stone_size;
    public GoBoardView( Context context,GoGame game ) {
        super( context );
        this.game=game;
      //  this.board=game.getVisualBoard();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint mPaint=new Paint();
       
        mPaint.setColor(0xCCCCCCCC);
       
        Paint blackPaint=new Paint();
        
        blackPaint.setColor(0xCC000000);
        blackPaint.setTextAlign(Paint.Align.CENTER  );
        Paint boardPaint=new Paint();
        
        boardPaint.setColor(0xFFc6b460);

        
        Paint gridPaint=new Paint();
        
        gridPaint.setColor(0xFFFFFFFF);
        gridPaint.setShadowLayer(1,1,1,0xFF000000 );
      
        canvas.drawRect(new RectF(0,0,this.getWidth(),this.getHeight()),boardPaint );
        
        for(int x=0;x<game.getVisualBoard().getSize();x++)
        {
            canvas.drawLine(stone_size/2.0f , stone_size/2.0f + x*stone_size , stone_size*(float)(game.getVisualBoard().getSize()-1)+stone_size/2.0f ,stone_size/2.0f+ x*stone_size,gridPaint);
            canvas.drawLine(stone_size/2.0f   + x*stone_size , stone_size/2.0f, stone_size/2.0f+ x*stone_size,stone_size*(float)(game.getVisualBoard().getSize()-1) +stone_size/2.0f,gridPaint);
        }
        
        for(byte x=0;x<game.getVisualBoard().getSize();x++)
            for(byte y=0;y<game.getVisualBoard().getSize();y++)
            {
                if (game.getVisualBoard().isCellWhite(x,y))
                    {
                    canvas.drawCircle( x*stone_size + stone_size/2.0f ,y*stone_size+stone_size/2.0f,stone_size/2,mPaint );
                    canvas.drawText( "" + game.getGroup(x,y) +"-" + (game.group_has_liberty(game.getGroup( x,y))?"x":"-"), x*stone_size + stone_size/2.0f ,y*stone_size+stone_size/2.0f ,blackPaint );
                    }
                if (game.getVisualBoard().isCellBlack(x,y))
                    {
                    canvas.drawCircle( x*stone_size + stone_size/2.0f ,y*stone_size+stone_size/2.0f,stone_size/2,blackPaint );
                    canvas.drawText( "" + game.getGroup(x,y), x*stone_size + stone_size/2.0f ,y*stone_size+stone_size/2.0f ,mPaint );
                    }
                
        
            }
        
        
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w<=h)
            stone_size=w/(float)game.getVisualBoard().getSize();
        else
            stone_size=h/(float)game.getVisualBoard().getSize();
        invalidate(); // needed here?
    }

    
    public boolean onTouch( View v, MotionEvent event ) {

        
        if ((event.getY()<stone_size*game.getVisualBoard().getSize())&&(event.getX()<stone_size*game.getVisualBoard().getSize())) // if user put his finger on the board
        {
            if (!game.do_move((byte)(event.getX()/stone_size),(byte)(event.getY()/stone_size)))
                ; // vibrate
        }
        else
        {
            if (event.getAction()==MotionEvent.ACTION_UP) game.undo();
        }
        
        invalidate();  // the board looks diffrent after a move
        return true;
    }
    
    // TODO undo to context menue / pass to context
    
}