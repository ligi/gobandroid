package org.ligi.gobandroid_hd.ui.online;

import android.os.AsyncTask;
import com.google.api.services.cloudgoban.Cloudgoban;
import com.google.api.services.cloudgoban.model.Game;
import com.google.api.services.cloudgoban.model.Text;
import org.ligi.gobandroid_hd.backend.CloudGobanHelper;
import org.ligi.gobandroid_hd.logic.SGFHelper;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.tracedroid.logging.Log;

import java.io.IOException;

public class UploadGameToCloudEndpointsBase extends AsyncTask<Void, Void, String> {

    protected String type;
    protected GobandroidFragmentActivity goActivity;

    public UploadGameToCloudEndpointsBase(GobandroidFragmentActivity goActivity, String type) {
        this.goActivity = goActivity;
        this.type = type;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected boolean doRegister() {
        return false;
    }

    @Override
    protected String doInBackground(Void... params) {

        String game_key = null;

        int attempts = 0;
        boolean has_cloud_history = (goActivity.getGame().getCloudKey() != null);
        while (attempts++ < 6) {
            try {
                Cloudgoban gc = goActivity.getApp().getCloudgoban();

                Log.i("CloudGoban upload " + game_key + " " + has_cloud_history);
                if (game_key == null) {
                    Game game = new Game();

                    if (type != null) {
                        game.setType(type);
                    }

                    UserHandler.setGameUsername(goActivity);


                    game.setSgf(new Text().setValue(SGFHelper.game2sgf(goActivity.getGame())));

                    if (has_cloud_history) {
                        game.setEncodedKey(goActivity.getGame().getCloudKey());
                        Game res_game = gc.games().update(UserHandler.getUserKey(goActivity.getApp()), game).execute();
                        game_key = res_game.getEncodedKey();
                    } else { // create a new Game
                        game_key = gc.games().insert(game).execute().getEncodedKey();
                    }

                }

                if (game_key != null) { // success
                    if (doRegister()) {
                        boolean setCloudKey = game_key.endsWith("invite"); // TODO remove this hack
                        CloudGobanHelper.registerGame(goActivity, game_key, "" + (goActivity.getGame().isBlackToMove() ? 'b' : 'w'), false, null, setCloudKey);
                    }
                    goActivity.getGame().notifyGameChange();
                    return game_key;
                }

                try { // exponential back off
                    Thread.sleep(attempts * attempts * 1000);
                }
                catch (InterruptedException e) {
                }
            }
            catch (IOException e) {
                Log.i("CloudGoban err " + e);
            }

        }

        return null;

    }

}
