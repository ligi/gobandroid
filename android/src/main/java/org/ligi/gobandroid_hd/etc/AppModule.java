package org.ligi.gobandroid_hd.etc;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.model.GameProvider;
import org.ligi.gobandroid_hd.ui.application.GobandroidSettings;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public GobandroidSettings provideSettings() {
        return new GobandroidSettings(app);
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
