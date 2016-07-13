package org.ligi.gobandroid_hd;

import android.app.Application;
import android.content.Context;
import android.support.test.runner.AndroidJUnitRunner;
import org.ligi.gobandroid_hd.etc.AppComponent;
import org.ligi.gobandroid_hd.etc.AppModule;
import org.ligi.gobandroid_hd.uitest.DaggerTestComponent;
import org.ligi.gobandroid_hd.uitest.TestComponent;

public class TestApp extends App {

    @Override
    public TestComponent createComponent() {
        return DaggerTestComponent.builder().appModule(new AppModule(this)).build();
    }

    public static TestComponent component() {
        return (TestComponent)App.component();
    }
}
