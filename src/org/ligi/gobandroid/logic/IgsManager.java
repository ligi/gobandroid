package org.ligi.gobandroid.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import org.ligi.gobandroid.ui.GoPrefs;
import org.ligi.tracedroid.logging.Log;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

public class IgsManager extends Thread {

	private static Socket igs = null;
	private static BufferedReader input = null;
	private static String console_output = "";
	private static boolean is_connected = false;
	
	public static void connect() {
		if (is_connected)
			return;
		
		try {
			igs = new Socket("igs.joyjoy.net", 6969);
			input = new BufferedReader(new InputStreamReader(igs.getInputStream()));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		

		is_connected = true;
	}
		
	public static void disconnect() {
		if (!is_connected)
			return;
		
		try {
			igs.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		input = null;
		is_connected = false;		
	}

	
	public void run() {				

		while (true) {
			if (is_connected) {
				try {
					int ret_char = input.read();
					if (ret_char != -1) {
						console_output += (char)ret_char;
					}
					else Thread.currentThread().sleep(1000);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
				try {
					Thread.currentThread().sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	public static String getConsoleOutput() {
		return console_output;
	}
		
}