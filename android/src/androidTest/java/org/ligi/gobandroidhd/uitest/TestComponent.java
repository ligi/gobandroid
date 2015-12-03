package org.ligi.gobandroidhd.uitest;

import org.ligi.gobandroid_hd.etc.AppComponent;
import org.ligi.gobandroid_hd.etc.AppModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface TestComponent extends AppComponent {

    void inject(TheGameInfoDialog theGameInfoDialog);

    void inject(TheTsumegoActivity theTsumegoActivity);

    void inject(TheGameScoringActivity theGameScoringActivity);

    void inject(TheReviewActivity theReviewActivity);


    void inject(TheGoGamePlayerActivity theGoGamePlayerActivity);

    void inject(TheEditGameActivity theEditGameActivity);
}
