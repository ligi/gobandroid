/**
 * gobandroid
 * by Marcus -Ligi- Bueschleb 
 * http://ligi.de
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as 
 * published by the Free Software Foundation; 
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. 
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 **/

package org.ligi.gobandroid_hd.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.widget.LinearLayout;

import org.ligi.axt.AXT;
import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.FileEncodeDetector;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.sgf.SGFReader;
import org.ligi.gobandroid_hd.ui.alerts.GameLoadingDialog;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.ingame_common.SwitchModeHelper;
import org.ligi.gobandroid_hd.ui.tsumego.TsumegoHelper;
import org.ligi.tracedroid.logging.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Activity to load a SGF with a ProgressDialog showing the Progress
 *
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         <p/>
 *         License: This software is licensed with GPLv3
 */

public class SGFLoadActivity extends GobandroidFragmentActivity implements
        Runnable, SGFReader.ISGFLoadProgressCallback {

    public static final String INTENT_EXTRA_MOVE_NUM = "move_num";
    private Uri intent_uri;
    private String sgf;
    private int act_progress;
    private int max_progress;
    private String act_message;
    private GameLoadingDialog dlg;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(new LinearLayout(this));

        GoPrefs.init(this);

        dlg = new GameLoadingDialog(this);
        dlg.show();

        App.getTracker().trackEvent("ui_action", "load_gf", getIntent().getData().toString(), null);
        new Thread(this).start();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * get the content-string from a uri
     *
     * @param intent_uri
     * @return
     * @throws FileNotFoundException
     * @throws MalformedURLException
     * @throws IOException
     */
    public String uri2string(Uri intent_uri) throws IOException {

        if (intent_uri.toString().startsWith("/")) {
            return AXT.at(new File(intent_uri.toString())).readToString(FileEncodeDetector.detect(intent_uri.toString()));
        }

        InputStream in;
        String uri_str = intent_uri.toString();
        if (uri_str.startsWith("content://")) {
            in = getContentResolver().openInputStream(intent_uri);
        } /* CloudGobanRemove else if (uri_str.startsWith(GobandroidConfiguration.CLOUD_GOBAN_URL_BASE)) {
            new GobandroidNotifications(this).cancelCloudMoveNotification();
            cloudgoban_parent_key = uri_str.replace(GobandroidConfiguration.CLOUD_GOBAN_URL_BASE, "").split("#")[0];
            Cloudgoban cg = getApp().getCloudgoban();
            //cg.games().get(cloudgoban_parent_key).execute().
            Log.i("downloading CloudGoban game");
            return cg.games().get(cloudgoban_parent_key).execute().getSgf().getValue();
        } */ else {
            in = new BufferedInputStream(new URL("" + intent_uri).openStream(), 4096);
        }

        FileOutputStream file_writer = null;

        // if it comes from network
        if (intent_uri.toString().startsWith("http")) { // https catched also
            new File(GoPrefs.getSGFPath() + "/downloads").mkdirs();
            final File f = new File(GoPrefs.getSGFPath() + "/downloads/" + intent_uri.getLastPathSegment());
            f.createNewFile();
            file_writer = new FileOutputStream(f);
        }

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) > -1) {
            buf.write(buffer, 0, len);
        }
        buf.flush();

        final InputStream stream_det = new ByteArrayInputStream(buf.toByteArray());
        final Charset chrset = FileEncodeDetector.detect(stream_det);

        final InputStream stream_pro = new ByteArrayInputStream(buf.toByteArray());
        final StringBuilder out = new StringBuilder();
        final byte[] b = new byte[4096];
        for (int n; (n = stream_pro.read(b)) != -1; ) {
            out.append(new String(b, 0, n, chrset));
            if (file_writer != null) {
                file_writer.write(b, 0, n);
            }
        }
        if (file_writer != null) {
            file_writer.close();
        }

        Log.i("SGF return" + out.toString());
        return out.toString();
    }

    @Override
    public void run() {
        Looper.prepare();

        intent_uri = getIntent().getData(); // extract the uri from
        // the intent

        if (intent_uri == null) {
            Log.e("SGFLoadActivity with intent_uri==null");
            finish();
            return;
        }

        if (intent_uri.toString().endsWith(".golink")) {
            AXT.at(this).startCommonIntent().activityFromClass(GoLinkLoadActivity.class);
            finish();
            return;
        }

        if (intent_uri.toString().startsWith("tsumego")) {
            intent_uri = Uri.parse(intent_uri.toString().replaceFirst("tsumego", "file"));
            App.getInteractionScope().setMode(InteractionScope.MODE_TSUMEGO);
        }

        GoGame game = null;
        try {
            sgf = uri2string(intent_uri);

            game = SGFReader.sgf2game(sgf, this);

            // if it is a tsumego and we need a transformation to right corner
            // -> do so
            if (App.getInteractionScope().getMode() == InteractionScope.MODE_TSUMEGO) {
                final int transform = TsumegoHelper.calcTransform(game);

                if (transform != SGFReader.DEFAULT_SGF_TRANSFORM) {
                    game = SGFReader.sgf2game(sgf, null, SGFReader.BREAKON_NOTHING, transform);
                }
            }


        } catch (Exception e) {
            Log.w("exception in load", e);
            game = null;
        }

        if (game == null) {
            runOnUiThread(new Runnable() {

                @Override
                /** if the sgf loading fails - give the user the option to send this SGF to me - to perhaps fix the
                 * parser to load more SGF's - TODO remove this block if all SGF's load fine ;-) */
                public void run() {
                    dlg.hide();
                    new AlertDialog.Builder(SGFLoadActivity.this)
                            .setTitle(R.string.results)
                            .setMessage(
                                    R.string.problem_loading_sgf_would_you_like_to_send_ligi_this_sgf_to_fix_the_problem)
                            .setPositiveButton(R.string.yes,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int whichButton) {
                                            final Intent emailIntent = new Intent(
                                                    android.content.Intent.ACTION_SEND);
                                            emailIntent.setType("plain/text");
                                            emailIntent
                                                    .putExtra(
                                                            android.content.Intent.EXTRA_EMAIL,
                                                            new String[]{"ligi@ligi.de"});
                                            emailIntent
                                                    .putExtra(
                                                            android.content.Intent.EXTRA_SUBJECT,
                                                            "SGF Problem"
                                                                    + App.getVersionCode()
                                                    );
                                            emailIntent
                                                    .putExtra(
                                                            android.content.Intent.EXTRA_TEXT,
                                                            "uri: "
                                                                    + intent_uri
                                                                    + " sgf:\n"
                                                                    + sgf
                                                                    + "err:"
                                                                    + Log.getCachedLog()
                                                    );
                                            SGFLoadActivity.this.startActivity(Intent
                                                    .createChooser(emailIntent,
                                                            "Send mail..."));
                                            finish();
                                        }
                                    }
                            )
                            .setNegativeButton(R.string.no,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int whichButton) {
                                            finish();
                                        }
                                    }
                            ).show();
                }
            });

            return;
        }

        final int move_num = getIntent().getIntExtra(INTENT_EXTRA_MOVE_NUM, -1);

        if (move_num != -1) {
            for (int i = 0; i < move_num; i++) {
                game.jump(game.getActMove().getnextMove(0));
            }
        }
        App.setGame(game);

        game.getMetaData().setFileName(Uri.decode(intent_uri.toString()));

        App.getGame().notifyGameChange();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dlg.hide();
                finish();
            }
        });
        SwitchModeHelper.startGameWithCorrectMode(this);

    }

    @Override
    public void progress(int act, int max, int progress_val) {
        act_progress = act;
        max_progress = max;
        act_message = getResources().getString(R.string.move) + " " + progress_val;

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                dlg.progress.setProgress(act_progress);
                dlg.progress.setMax(max_progress);
                dlg.message.setText(act_message);
            }
        });
    }
}