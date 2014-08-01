package org.ligi.gobandroid_hd.helper;

import java.io.File;
import java.io.FilenameFilter;

public class SGFFileNameFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String filename) {
        return filename.toUpperCase().endsWith(".SGF");
    }
}
