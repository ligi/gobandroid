package org.ligi.gobandroid_hd.ui;

import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;

public class AutoScreenShotDialog {

	public static void show4tsumego(GobandroidFragmentActivity ctx) {
		new AutoScreenShotTask(ctx).execute(ctx.getSettings().getTsumegoPath());
	}
	
	public static void show4review(GobandroidFragmentActivity ctx) {
		new AutoScreenShotTask(ctx).execute(ctx.getSettings().getReviewPath());
	}
}
