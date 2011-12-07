package org.ligi.gobandroid_hd.ui;

import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;

public class AutoScreenShotDialog {

	public static void show(GobandroidFragmentActivity ctx) {
		new AutoScreenShotTask(ctx).execute("foo");
	}
}
