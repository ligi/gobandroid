package org.ligi.gobandroid_hd.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.application.GobandroidSettings;
import org.ligi.tracedroid.logging.Log;

import javax.inject.Inject;

public class UnzipSGFsDialog {

    public static class Decompress {
        private InputStream _zipFile;
        private String _location;

        public Decompress(InputStream zipFile, String location) {
            _zipFile = zipFile;
            _location = location;

            _dirChecker("");
        }

        public void unzip() {
            try {
                InputStream fin = _zipFile;
                ZipInputStream zin = new ZipInputStream(fin);
                ZipEntry ze = null;
                byte[] readData = new byte[1024];
                while ((ze = zin.getNextEntry()) != null) {
                    Log.i("Decompress" + "unzip" + ze.getName());
                    if (ze.isDirectory()) {
                        _dirChecker(ze.getName());
                    } else {
                        FileOutputStream fout = new FileOutputStream(_location + ze.getName());

                        int i2 = zin.read(readData);

                        while (i2 != -1) {
                            fout.write(readData, 0, i2);
                            i2 = zin.read(readData);
                        }

                        zin.closeEntry();
                        fout.close();
                    }

                }
                zin.close();
            } catch (Exception e) {
                Log.e("Decompress", "unzip", e);
            }

        }

        private void _dirChecker(String dir) {
            File f = new File(_location + dir);

            if (!f.isDirectory()) {
                f.mkdirs();
            }
        }
    }

    public static void show(final Activity activity, final Intent intent_after_finish) {

        final ProgressDialog dialog = ProgressDialog.show(activity, "", activity.getString(R.string.copy_sgf_dialog_message), true);

        new Thread(new AlertDialogUpdater(activity, dialog, intent_after_finish)).start();

    }


    public static class AlertDialogUpdater implements Runnable {

        private ProgressDialog myProgress;
        private Activity activity;
        private Intent intent_after_finish;

        @Inject
        GobandroidSettings settings;

        public AlertDialogUpdater(Activity activity, ProgressDialog progress, Intent intent_after_finish) {
            App.component().inject(this);
            this.activity = activity;
            this.intent_after_finish = intent_after_finish;
            myProgress = progress;
        }

        public void run() {
            final Resources resources = activity.getResources();
            final InputStream is = resources.openRawResource(R.raw.sgf_pack);
            new Decompress(is, settings.getSGFBasePath()).unzip();

            myProgress.dismiss();
            activity.startActivity(intent_after_finish);
        }
    }
}
