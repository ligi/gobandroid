package reporting;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.espresso.FailureHandler;
import android.support.test.espresso.base.DefaultFailureHandler;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.view.View;
import com.jraska.falcon.FalconSpoon;
import java.util.Collection;
import org.hamcrest.Matcher;

public class SpooningFailureHandler implements FailureHandler {

    private final FailureHandler delegate;
    private final Instrumentation instrumentation;

    public SpooningFailureHandler(Instrumentation instrumentation) {
        delegate = new DefaultFailureHandler(instrumentation.getTargetContext());
        this.instrumentation = instrumentation;
    }

    @Override
    public void handle(Throwable error, Matcher<View> viewMatcher) {
        try {
            FalconSpoon.screenshot(getCurrentActivity(), "error");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        delegate.handle(error, viewMatcher);

    }


    private Activity getCurrentActivity() throws Throwable {
        instrumentation.waitForIdleSync();
        final Activity[] activity = new Activity[1];
        instrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                final Collection<Activity> activites = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                activity[0] = activites.iterator().next();
            }
        });
        return activity[0];
    }

}
