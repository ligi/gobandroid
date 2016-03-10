package org.ligi.gobandroid_hd.etc;

import org.ligi.gobandroid_hd.ui.CustomActionBar;
import org.ligi.gobandroid_hd.ui.GoBoardViewHD;
import org.ligi.gobandroid_hd.ui.GobandroidDialog;
import org.ligi.gobandroid_hd.ui.GobandroidNotifications;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.application.GobandroidSettings;
import org.ligi.gobandroid_hd.ui.application.navigation.NavigationDrawerHandler;
import org.ligi.gobandroid_hd.ui.fragments.GobandroidFragment;
import org.ligi.gobandroid_hd.ui.fragments.GobandroidGameAwareFragment;
import org.ligi.gobandroid_hd.ui.gnugo.GnuGoGame;
import org.ligi.gobandroid_hd.ui.tsumego.TsumegoGameExtrasFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    GobandroidSettings settings();

    void inject(GobandroidDialog gobandroidDialog);

    void inject(GobandroidFragmentActivity gobandroidFragmentActivity);

    void inject(GnuGoGame gnuGoGame);

    void inject(NavigationDrawerHandler navigationDrawer);

    void inject(TsumegoGameExtrasFragment tsumegoGameExtrasFragment);

    void inject(CustomActionBar customActionBar);

    void inject(GobandroidGameAwareFragment gobandroidGameAwareFragment);

    void inject(GoBoardViewHD goBoardViewHD);

    void inject(GobandroidFragment gobandroidFragment);

    void inject(GobandroidNotifications gobandroidNotifications);
}
