package org.ligi.gobandroid_hd;

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


    @Override
    public boolean isTesting() {
        return false;
    }
}
