package org.ligi.gobandroid_hd.ui;

import org.ligi.gobandroid_hd.GobandroidApp;
import org.ligi.gobandroid_hd.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GobandroidDialog extends Dialog implements android.view.View.OnClickListener {

	private ContextThemeWrapper ctx;
	private LayoutInflater inflater;
	private OnClickListener myOKOnClickListener;
	
	public GobandroidDialog(Context context) {
		super(context);
		
		ctx=new ContextThemeWrapper(context,R.style.dialog_theme);
		inflater=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(inflater.inflate(R.layout.gobandroid_dialog,null));

		((Button)findViewById(R.id.dialog_ok_btn)).setOnClickListener(this);
		myOKOnClickListener=new DefaultOnClickListener();

		// this sounds misleading but behaves right - we just do not want to start with keyboard open 
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	}
	
	public GobandroidApp getApp() {
		return (GobandroidApp)ctx.getApplicationContext();
	}

	public void setIconResource(int ico_res) {
		((ImageView)findViewById(R.id.dialog_icon)).setImageResource(ico_res);
	}
	
	@Override
	public void setContentView(int content){
		LinearLayout container=(LinearLayout)this.findViewById(R.id.dialog_content);
		Log.i("","container" + container + " inflater" + inflater);
		container.addView(inflater.inflate(content, null));
		
	}
	
	public void setOnOKClick(OnClickListener listener) {
		myOKOnClickListener=listener;
	}
	
	class DefaultOnClickListener implements OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dismiss();	
		}
		
	}

	@Override
	public void setTitle(CharSequence title) {
		((TextView)this.findViewById(R.id.dialog_title)).setText(title);
	}
	
	@Override
	public void onClick(View v) {
		myOKOnClickListener.onClick(this,0);
	}
	
	public Context getThemedContext() {
		return ctx;
	}
}
