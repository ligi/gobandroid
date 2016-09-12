package org.ligi.gobandroid_hd.uitest;

import dagger.Component;
import javax.inject.Singleton;
import org.ligi.gobandroid_hd.etc.AppComponent;
import org.ligi.gobandroid_hd.etc.AppModule;

@Singleton
@Component(modules = {AppModule.class})
public interface TestComponent extends AppComponent {

    void inject(TheGameInfoDialog theGameInfoDialog);

    void inject(TheTsumegoActivity theTsumegoActivity);

    void inject(TheGameScoringActivity theGameScoringActivity);

    void inject(TheReviewActivity theReviewActivity);


    void inject(TheGoGamePlayerActivity theGoGamePlayerActivity);

    void inject(TheEditGameActivity theEditGameActivity);

    void inject(TheUndoFunctionality theUndoFunctionality);
}
