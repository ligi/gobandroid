package org.ligi.gobandroidhd.base;

import android.app.Activity;
import android.support.annotation.StringRes;
import android.test.ActivityInstrumentationTestCase2;
import android.view.WindowManager;
import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.sgf.SGFReader;
import reporting.SpooningFailureHandler;
import static android.support.test.espresso.Espresso.setFailureHandler;

public abstract class BaseIntegration<T extends Activity> extends ActivityInstrumentationTestCase2<T> {

    public BaseIntegration(Class<T> activityClass) {
        super(activityClass);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        setFailureHandler(new SpooningFailureHandler(this));
        App.isTesting = true;
    }

    @Override
    public T getActivity() {
        final T activity = super.getActivity();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            }
        });

        return activity;
    }

    protected String readAsset(final String fileName) {
        return GobandroidTestBaseUtil.readAsset(getInstrumentation().getContext(), fileName);
    }

    protected GoGame readGame(final String fileName) {
        return SGFReader.sgf2game(readAsset("sgf/" + fileName + ".sgf"), null);
    }

    protected String getString(@StringRes int resId) {
        return getInstrumentation().getTargetContext().getString(resId);
    }

}
