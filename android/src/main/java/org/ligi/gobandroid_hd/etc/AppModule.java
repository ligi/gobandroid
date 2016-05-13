package org.ligi.gobandroid_hd.etc;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.model.GameProvider;
import org.ligi.gobandroid_hd.ui.application.GoAndroidEnvironment;

@Module
public class AppModule {

    private final App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public GoAndroidEnvironment provideSettings() {
        return new GoAndroidEnvironment(app);
    }

    @Provides
    @Singleton
    public InteractionScope provideInteractionScope() {
        return new InteractionScope();
    }

    @Provides
    @Singleton
    public GameProvider provideGameProvider(InteractionScope interactionScope) {
        return new GameProvider(interactionScope);
    }

    @Provides
    @Singleton
    public App provideApp() {
        return app;
    }


}
