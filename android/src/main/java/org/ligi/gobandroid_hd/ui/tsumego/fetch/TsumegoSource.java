package org.ligi.gobandroid_hd.ui.tsumego.fetch;

public class TsumegoSource {
    public String remote_path, local_path, fname;

    public TsumegoSource(String local_path, String remote_path, String fname) {
        this.remote_path = remote_path;
        this.local_path = local_path;
        this.fname = fname;
    }

    public String getFnameByPos(int pos) {
        return String.format(fname, pos);
    }
}