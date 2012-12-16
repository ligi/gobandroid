package org.ligi.gobandroid_hd.ui;

import org.ligi.gobandroid_beta.R;
import org.ligi.gobandroid_hd.GobandroidApp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * A styled Dialog fit in the gobandroid style
 * 
 * @author <a href="http://ligi.de">Marcus -LiGi- Bueschleb </a>
 * 
 *         This software is licenced with GPLv3
 */
public class GobandroidDialog extends Dialog {

	private ContextThemeWrapper ctx;
	private LayoutInflater inflater;

	private Button positive_btn;
	private Button negative_btn;

	private LinearLayout button_container;

	public GobandroidDialog(Context context) {
		super(context);

		ctx = new ContextThemeWrapper(context, R.style.dialog_theme);
		inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(inflater.inflate(R.layout.gobandroid_dialog, null));

		// this sounds misleading but behaves right - we just do not want to
		// start with keyboard open
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		button_container = (LinearLayout) findViewById(R.id.button_container);
	}

	// dirty hack - but works and finding another workaround costed me hours
	// without success in sight ..
	public void setIsSmallDialog() {
		findViewById(R.id.dialog_main_container).setBackgroundResource(R.drawable.shinkaya_half);
	}

	public GobandroidApp getApp() {
		return (GobandroidApp) ctx.getApplicationContext();
	}

	public void setIconResource(int ico_res) {
		((ImageView) findViewById(R.id.dialog_icon)).setImageResource(ico_res);
	}

	@Override
	public void setContentView(int content) {
		LinearLayout container = (LinearLayout) this.findViewById(R.id.dialog_content);

		Log.i("", "container" + container + " inflater" + inflater);
		container.addView(inflater.inflate(content, null));
	}

	public void setContentFill() {
		LinearLayout container = (LinearLayout) this.findViewById(R.id.dialog_content);
		container.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	}

	public void addItem(int image_resId, int str_resid, final OnClickListener listener) {
		LinearLayout container = (LinearLayout) this.findViewById(R.id.dialog_items);
		Log.i("", "container" + container + " inflater" + inflater);
		View v = inflater.inflate(R.layout.dialog_item, null);
		((TextView) v.findViewById(R.id.text)).setText(str_resid);
		((ImageView) v.findViewById(R.id.image)).setImageResource(image_resId);

		v.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					v.setBackgroundResource(R.drawable.holo_transparent_bg);
					return true;
				case MotionEvent.ACTION_UP:
					v.setBackgroundDrawable(null);
					listener.onClick(GobandroidDialog.this, 0);
					return true;
				}
				return false;
			}

		});

		container.addView(v);

	}

	class DefaultOnClickListener implements OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dismiss();
		}

	}

	@Override
	public void setTitle(CharSequence title) {
		((TextView) this.findViewById(R.id.dialog_title)).setText(title);
	}

	public Context getThemedContext() {
		return ctx;
	}

	class DialogOnClickWrapper implements View.OnClickListener {

		private DialogInterface.OnClickListener listener;

		public DialogOnClickWrapper(DialogInterface.OnClickListener listener) {
			this.listener = listener;
		}

		@Override
		public void onClick(View v) {
			listener.onClick(GobandroidDialog.this, 0);
		}

	}

	public void setPositiveButton(int text_stringres) {
		getPositiveButton().setText(text_stringres);
		getPositiveButton().setOnClickListener(new DialogOnClickWrapper(new DefaultOnClickListener()));
	}

	public void setPositiveButton(int text_stringres, DialogInterface.OnClickListener listener) {
		getPositiveButton().setText(text_stringres);
		getPositiveButton().setOnClickListener(new DialogOnClickWrapper(listener));
	}

	public Button getPositiveButton() {
		if (positive_btn == null) {
			positive_btn = new Button(getContext());
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
			positive_btn.setLayoutParams(lp);
			button_container.addView(positive_btn);
		}
		return positive_btn;
	}

	public void setNegativeButton(int text_stringres) {
		getNegativeButton().setText(text_stringres);
		getNegativeButton().setOnClickListener(new DialogOnClickWrapper(new DefaultOnClickListener()));
	}

	public void setNegativeButton(int text_stringres, DialogInterface.OnClickListener listener) {
		getNegativeButton().setText(text_stringres);
		getNegativeButton().setOnClickListener(new DialogOnClickWrapper(listener));
	}

	private Button getNegativeButton() {
		if (negative_btn == null) {
			negative_btn = new Button(getContext());
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
			negative_btn.setLayoutParams(lp);
			button_container.addView(negative_btn);
		}
		return negative_btn;
	}
}
