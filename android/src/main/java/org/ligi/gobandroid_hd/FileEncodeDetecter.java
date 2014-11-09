package org.ligi.gobandroid_hd;

import android.util.Log;

import org.mozilla.intl.chardet.*;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by icehong on 2014/11/9.
 */
public class FileEncodeDetecter {
    private static String code_name = "UTF-8";
    public static Charset detect(String file) {

        nsDetector det = new nsDetector(nsPSMDetector.ALL);

        det.Init(new nsICharsetDetectionObserver() {
                     public void Notify(String charset) {
                         code_name = charset;
                     }
                 }
        );
        try {
            BufferedInputStream imp = new BufferedInputStream(new FileInputStream(file));
            byte[] buf = new byte[1024];
            int len;
            boolean done = false;
            boolean isAscii = true;
            while (!done && (len = imp.read(buf, 0, buf.length)) != -1) {
                if (isAscii)
                    isAscii = det.isAscii(buf, len);
                if (!isAscii)
                    done = det.DoIt(buf, len, false);
            }
        } catch (Exception e) {
            Log.w("exception in detect encoding.", e);
        }
        det.Done();
        return Charset.forName(code_name);
    }

    public static Charset detect(InputStream imp ) {

        nsDetector det = new nsDetector(nsPSMDetector.ALL);

        det.Init(new nsICharsetDetectionObserver() {
                     public void Notify(String charset) {
                         code_name = charset;
                     }
                 }
        );
        try {
            byte[] buf = new byte[1024];
            int len;
            boolean done = false;
            boolean isAscii = true;
            while (!done && (len = imp.read(buf, 0, buf.length)) != -1) {
                if (isAscii)
                    isAscii = det.isAscii(buf, len);
                if (!isAscii)
                    done = det.DoIt(buf, len, false);
            }
        } catch (Exception e) {
            Log.w("exception in detect encoding.", e);
        }
        det.Done();
        return Charset.forName(code_name);
    }
}
