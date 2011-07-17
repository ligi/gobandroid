package org.ligi.gobandroid.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class IgsConsoleActivity 
		extends Activity
{
	private TextView message_tv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		GoPrefs.init(this);		
		Socket igs;
		BufferedReader input;
		String st = "";
		
		try {
			igs = new Socket("igs.joyjoy.net", 6969);
			input = new BufferedReader(new InputStreamReader(igs.getInputStream()));
			Thread.currentThread().sleep(1000);
			st = input.readLine();
			igs.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		st += "\nOML\n";
		st += "\n awesome!!\n";
		
		message_tv = new TextView(this);
	    message_tv.setText(st);
	    setContentView(message_tv);
	}
}