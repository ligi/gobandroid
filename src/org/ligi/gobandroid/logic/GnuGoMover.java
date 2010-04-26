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

package org.ligi.gobandroid.logic;

import org.ligi.gobandroid.ai.gnugo.IGnuGoService;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class GnuGoMover implements Runnable{

	private IGnuGoService gnu_service ;
	private GoGame game;
	
	private boolean gnugo_size_set=false;
	
	public boolean playing_black=false;
	public boolean playing_white=false;
	
	public boolean paused=false;
	public boolean thinking=false;
	
	public final static String intent_action_name="org.ligi.gobandroid.ai.gnugo.GnuGoService";
	private byte level;
	private Application application;
	
	private ServiceConnection conn;
	private Thread mover_thread;
	
	public GnuGoMover(Activity activity,GoGame game,boolean playing_black,boolean playing_white,byte level) {
		this.application=activity.getApplication();
		this.level=level;
		this.playing_black=playing_black;
		this.playing_white=playing_white;
		
		this.game=game;
		
		if ( playing_black || playing_white ) 
			{
	        conn = new ServiceConnection() {
	
				@Override
				public void onServiceConnected(ComponentName name, IBinder service) {
					gnu_service = IGnuGoService.Stub.asInterface(service);
		
					try {
						Log.i("INFO", "Service bound "  + gnu_service.processGTP("test"));
					} catch (RemoteException e) {
					}
					
				}
	
				@Override
				public void onServiceDisconnected(ComponentName name) {
					Log.i("INFO", "Service unbound ");				
				}
	        	
	        	
	        };
	        
	        application.bindService(new Intent("org.ligi.gobandroid.ai.gnugo.GnuGoService"), conn, Context.BIND_AUTO_CREATE);
	        
	        mover_thread=new Thread(this);
	        mover_thread.start();
			}
	}
	
	public boolean isReady() {
		return (!isPlayingInThisGame()) // not need to init
				|| (gnu_service!=null); 	// or the service is there
	}
	
	
	
	public boolean isPlayingInThisGame() {
		return (playing_black || playing_white );
	}
	
	public String coordinates2gtpstr(byte x,byte y)  {
		if (x>8) x++; // I is missing
		y=(byte)(game.getBoardSize()-(y));
		return ""+(char)('A'+x) + ""+(y);
	}

	public void processWhiteMove(byte x,byte y)   {
		try {
			gnu_service.processGTP("white " + coordinates2gtpstr(x,y));
		} catch (Exception e) {		}
	}
	
	public void processBlackMove(byte x,byte y)   {
		try {
			gnu_service.processGTP("black " + coordinates2gtpstr(x,y));
		} catch (Exception e) {		}
	}
	
	public void stop() {
		try {
			if (!game.isFinished()) {
				game.pass();
				game.pass();	
			}
			Log.i("gobandroid", "gugoservice stopping service" + game.isFinished());
			application.unbindService(conn);
			application.stopService(new Intent("org.ligi.gobandroid.ai.gnugo.GnuGoService"));
		}
		catch (Exception e) {}
	} 
	
	@Override
	public void run() {
		while (!game.isFinished()) {
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if ((gnu_service==null)||(paused))
				continue;
						
			if (!gnugo_size_set)
				try {
					// set the size
					gnu_service.processGTP("boardsize " + game.getBoardSize());
									
					
					for (byte x=0;x<game.getBoardSize();x++)
						for (byte y=0;y<game.getBoardSize();y++)
							if (game.getHandicapBoard().isCellBlack(x, y))
								gnu_service.processGTP("black " + coordinates2gtpstr(x,y));
					Log.i("gobandroid" ,"setting level " + gnu_service.processGTP("level "+level));
					gnugo_size_set=true;
				} catch (RemoteException e) {}
			
				
			if (isMoversMove()) 
			{
			thinking =true;
			if (game.isBlackToMove()) {
				try {
				
					String answer= gnu_service.processGTP("genmove black");
					
					if (game.isFinished())
						break;
					
					GTPHelper.doMoveByGTPString(answer, game);
					Log.i("gobandroid", "gugoservice" + gnu_service.processGTP("showboard"));		
										
				} catch (RemoteException e) {
				}
			}
			else {
				
				try {
					String answer= gnu_service.processGTP("genmove white");
					
					if (game.isFinished())
						break;
					
					GTPHelper.doMoveByGTPString(answer, game);
					
					Log.i("gobandroid", "gugoservice" + gnu_service.processGTP("showboard"));
					
				} catch (RemoteException e) {
				}				
			}
			
			thinking=false;
			}

		}
		stop();
	}

	/**
	 * @return  if the engine is thinking
	 */
	public boolean isThinking() {
		return thinking;
	}
	
	/**
	 * process an undo in the engine
	 */
	public void undo() {
		try {
			gnu_service.processGTP("undo");
		} catch (RemoteException e) {
			Logger.i(""+e);
		}
	}
	
	/**
	 * @return if it is a move the mover has to process
	 */
	public boolean isMoversMove() {
		return (game.isBlackToMove()&&(playing_black))|| (!game.isBlackToMove()&&(playing_white)) ;
	}
}
