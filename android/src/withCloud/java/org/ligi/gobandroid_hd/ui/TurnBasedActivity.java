package org.ligi.gobandroid_hd.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import java.util.ArrayList;
import org.ligi.gobandroid_hd.CloudHooks;

public class TurnBasedActivity extends AppCompatActivity {

    private boolean pick = true;

    public class MatchInitiatedCallback implements ResultCallback<TurnBasedMultiplayer.InitiateMatchResult> {

        @Override
        public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
            // Check if the status code is not success.
            Status status = result.getStatus();
            if (!status.isSuccess()) {
//            showError(status.getStatusCode());
                new AlertDialog.Builder(TurnBasedActivity.this).setMessage("" + status.getStatusMessage()).show();
                return;
            }

            TurnBasedMatch match = result.getMatch();

            // If this player is not the first player in this match, continue.
            if (match.getData() != null) {
                //          showTurnUI(match);
                return;
            }

            // Otherwise, this is the first player. Initialize the game state.
            //    initGame(match);

            // Let the player take the first turn
            //    showTurnUI(match);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (pick) {
            CloudHooks.INSTANCE.getGoogleApiClient().registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(@Nullable final Bundle bundle) {
                    final Intent intent = Games.TurnBasedMultiplayer.getSelectOpponentsIntent(CloudHooks.INSTANCE.getGoogleApiClient(), 1, 1, true);
                    startActivityForResult(intent, 101);
                    pick = false;
                    CloudHooks.INSTANCE.getGoogleApiClient().unregisterConnectionCallbacks(this);
                }

                @Override
                public void onConnectionSuspended(final int i) {

                }
            });

        }
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            // user canceled
            return;
        }

        CloudHooks.INSTANCE.getGoogleApiClient().registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable final Bundle bundle) {

                // Get the invitee list.
                final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

                // Get auto-match criteria.
                Bundle autoMatchCriteria = null;
                int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
                int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
                if (minAutoMatchPlayers > 0) {
                    autoMatchCriteria = RoomConfig.createAutoMatchCriteria(minAutoMatchPlayers, maxAutoMatchPlayers, 0);
                } else {
                    autoMatchCriteria = null;
                }

                TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder().addInvitedPlayers(invitees).setAutoMatchCriteria(autoMatchCriteria).build();

                // Create and start the match.
                Games.TurnBasedMultiplayer.createMatch(CloudHooks.INSTANCE.getGoogleApiClient(), tbmc).setResultCallback(new MatchInitiatedCallback());

                CloudHooks.INSTANCE.getGoogleApiClient().unregisterConnectionCallbacks(this);
            }

            @Override
            public void onConnectionSuspended(final int i) {

            }
        });
    }
}
