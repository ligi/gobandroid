package org.ligi.gobandroid_hd.ui;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import java.util.List;
import org.ligi.axt.AXT;
import org.ligi.gobandroid_hd.CloudPrefs;
import org.ligi.gobandroid_hd.R;

public class ProfileActivityLogic implements GoogleApiClient.OnConnectionFailedListener {

    BaseProfileActivity base;
    GoogleApiClient mGoogleApiClient;

    @BindView(R.id.sign_in_button)
    SignInButton signInButton;

    @BindViews({R.id.sign_out_button, R.id.achievements})
    List<Button> buttonsToShowWhenSignedIn;

    @OnClick(R.id.sign_out_button)
    void singOut() {
        CloudPrefs.INSTANCE.setUserWantsPlayConnection(false);
        mGoogleApiClient.disconnect();
        refresh();
    }


    @OnClick(R.id.achievements)
    void onAchievementsClick() {
        final Intent achievementsIntent = Games.Achievements.getAchievementsIntent(mGoogleApiClient);
        base.startActivityForResult(achievementsIntent, 816);
    }

    @OnClick(R.id.turnbased)
    void bar() {
        AXT.at(base).startCommonIntent().activityFromClass(TurnBasedActivity.class);
    }

    @OnClick(R.id.sign_in_button)
    void onSignInClick() {
        mGoogleApiClient.registerConnectionFailedListener(this);
        CloudPrefs.INSTANCE.setUserWantsPlayConnection(true);
        mGoogleApiClient.connect();
    }

    public void onResume(BaseProfileActivity base, GoogleApiClient mGoogleApiClient) {
        this.base = base;
        this.mGoogleApiClient = mGoogleApiClient;

        ButterKnife.bind(this, base);

        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable final Bundle bundle) {
                refresh();
            }

            @Override
            public void onConnectionSuspended(final int i) {
                refresh();
            }
        });

        refresh();
    }

    public void refresh() {
        signInButton.setVisibility(mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting() ? View.GONE : View.VISIBLE);
        for (final Button button : buttonsToShowWhenSignedIn) {
            button.setVisibility(mGoogleApiClient.isConnected() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        try {
            connectionResult.startResolutionForResult(base, 1001);

            if (connectionResult.hasResolution()) {
                connectionResult.startResolutionForResult(base, 1001);
            } else {
                GooglePlayServicesUtil.showErrorDialogFragment(connectionResult.getErrorCode(), base, null, 100, null);
            }

        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

}
