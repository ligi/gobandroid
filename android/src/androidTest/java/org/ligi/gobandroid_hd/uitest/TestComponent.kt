package org.ligi.gobandroid_hd.uitest

import dagger.Component
import org.ligi.gobandroid_hd.etc.AppComponent
import org.ligi.gobandroid_hd.etc.AppModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface TestComponent : AppComponent {

    fun inject(theGameInfoDialog: TheGameInfoDialog)

    fun inject(theTsumegoActivity: TheTsumegoActivity)

    fun inject(theGameScoringActivity: TheGameScoringActivity)

    fun inject(theReviewActivity: TheReviewActivity)

    fun inject(theGoGamePlayerActivity: TheGoGamePlayerActivity)

    fun inject(theEditGameActivity: TheEditGameActivity)

    fun inject(theUndoFunctionality: TheUndoFunctionality)
}
