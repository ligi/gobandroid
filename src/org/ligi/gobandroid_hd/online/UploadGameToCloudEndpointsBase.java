package org.ligi.gobandroid_hd.online;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import com.google.api.services.cloudgoban.Cloudgoban;
import com.google.api.services.cloudgoban.model.Game;
import com.google.api.services.cloudgoban.model.Text;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.backend.CloudGobanHelper;
import org.ligi.gobandroid_hd.logic.SGFHelper;
import org.ligi.gobandroid_hd.ui.GoActivity;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.tracedroid.logging.Log;

import java.io.IOException;

/**
* Created with IntelliJ IDEA.
* User: ligi
* Date: 1/31/13
* Time: 3:04 PM
* To change this template use File | Settings | File Templates.
*/
public class UploadGameToCloudEndpointsBase extends AsyncTask<Void, Void, String> {

    private ProgressDialog pd;
    private String type;
    private GobandroidFragmentActivity goActivity;
    public UploadGameToCloudEndpointsBase(GobandroidFragmentActivity goActivity, String type) {
        this.goActivity = goActivity;
        this.type=type;
    }

    @Override
    protected void onPreExecute() {
        pd = new ProgressDialog(goActivity);
        pd.setMessage(goActivity.getString(R.string.uploading_game));
        pd.show();
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

                    if (type!=null)
                        game.setType(type);

                    if (goActivity.getGame().isBlackToMove()) {
                        // if black is to move here -> we are white
                        goActivity.getGame().getMetaData().setWhiteName(goActivity.getApp().getSettings().getUsername());
                        goActivity.getGame().getMetaData().setWhiteRank(goActivity.getApp().getSettings().getRank());
                    } else {
                        // if white is to move here -> we are black
                        goActivity.getGame().getMetaData().setBlackName(goActivity.getApp().getSettings().getUsername());
                        goActivity.getGame().getMetaData().setBlackRank(goActivity.getApp().getSettings().getRank());
                    }


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
                        CloudGobanHelper.registerGame(goActivity, game_key, "" + (goActivity.getGame().isBlackToMove() ? 'b' : 'w'), false, null);
                    }
                    goActivity.getGame().notifyGameChange();
                    return game_key;
                }

                try { // exponential back off
                    Thread.sleep(attempts * attempts * 1000);
                } catch (InterruptedException e) {
                }
            } catch (IOException e) {
                Log.i("CloudGoban err " + e);
            }

        }

        return null;

    }

    @Override
    protected void onPostExecute(String result) {
        pd.dismiss();
    }

}
