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
	
	public final static String intent_action_name="org.ligi.gobandroid.ai.gnugo.GnuGoService";
	private byte level;
	
	public GnuGoMover(Activity activity,GoGame game,boolean playing_black,boolean playing_white,byte level) {
		this.level=level;
		this.playing_black=playing_black;
		this.playing_white=playing_white;
		
		this.game=game;
		
        ServiceConnection conn = new ServiceConnection() {

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
        
        activity.bindService(new Intent("org.ligi.gobandroid.ai.gnugo.GnuGoService"), conn, Context.BIND_AUTO_CREATE);
	
        new Thread(this).start();
	}
	
	public String coordinates2gtpstr(byte x,byte y)  {
	if (x>8) x++; // I is missing
	y=(byte)(game.getBoardSize()-(y));
	return ""+(char)('A'+x) + ""+(y);
	}

	public void processWhiteMove(byte x,byte y)   {
		try {
			gnu_service.processGTP("white " + coordinates2gtpstr(x,y));
		} catch (RemoteException e) {		}
	}
	
	public void processBlackMove(byte x,byte y)   {
		try {
			gnu_service.processGTP("black " + coordinates2gtpstr(x,y));
		} catch (RemoteException e) {		}
	}
	
	@Override
	public void run() {
		while ( true) {
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			if (gnu_service==null)
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
			
				if (game.isBlackToMove()&&(playing_black)) {
				try {
				
					String answer= gnu_service.processGTP("genmove black");
					GTPHelper.doMoveByGTPString(answer, game);
					Log.i("gobandroid", "gugoservice" + gnu_service.processGTP("showboard"));		
										
				} catch (RemoteException e) {
				}
			}
			if (!game.isBlackToMove()&&(playing_white)) {
				
				try {
					String answer= gnu_service.processGTP("genmove white");
					GTPHelper.doMoveByGTPString(answer, game);
					
					Log.i("gobandroid", "gugoservice" + gnu_service.processGTP("showboard"));
					
				} catch (RemoteException e) {
				}				
			}

		}
	}

	
}
