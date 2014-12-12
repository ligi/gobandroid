package org.ligi.gobandroid_hd;

import android.util.Log;

import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;
import org.mozilla.intl.chardet.nsPSMDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by icehong on 2014/11/9.
 */
public class FileEncodeDetector {

    public static Charset detect(String fileName) {
        return detect(new File(fileName));
    }

    public static Charset detect(File file) {
        try {
            return detect(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            return Charset.forName("UTF-8");
        }
    }

    public static Charset detect(InputStream imp) {
        class CodeNmeHolder {
            public String code_name="UTF-8";
        }

        final CodeNmeHolder holder = new CodeNmeHolder();
        nsDetector det = new nsDetector(nsPSMDetector.ALL);

        det.Init(new nsICharsetDetectionObserver() {
                     public void Notify(String charset) {
                         holder.code_name = charset;
                     }
                 }
        );
        try {
            byte[] buf = new byte[1024];
            int len;
            boolean done = false;
            boolean isAscii = true;
            while (!done && (len = imp.read(buf, 0, buf.length)) != -1) {
                if (isAscii) {
                    isAscii = det.isAscii(buf, len);
                }
                if (!isAscii) {
                    done = det.DoIt(buf, len, false);
                }
            }
        } catch (Exception e) {
            Log.w("exception in detect encoding.", e);
        }
        det.Done();
        return Charset.forName(holder.code_name);
    }
}
