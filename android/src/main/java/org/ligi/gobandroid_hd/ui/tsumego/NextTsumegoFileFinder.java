package org.ligi.gobandroid_hd.ui.tsumego;

import com.google.common.base.Optional;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.helper.SGFFileNameFilter;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.sort;

public class NextTsumegoFileFinder {

    /**
     * try to find next tsumego based on filename
     * searching the last number and incrementing it
     *
     * @param fileName
     * @return the filename found
     */
    public static Optional<String> calcNextTsumego(String fileName) {

        final File file = new File(fileName);

        if (!file.exists()) {
            App.getTracker().trackException("file given to calcNextTsumego is null", false);
            return Optional.absent();
        }

        final File dir = file.getParentFile();

        if (dir == null || !dir.isDirectory()) {
            App.getTracker().trackException("file given to calcNextTsumego has no valid parent", false);
            return Optional.absent();
        }

        final String[] fileNames = dir.list(new SGFFileNameFilter());

        if (fileNames == null || fileNames.length == 0) {
            App.getTracker().trackException("file given to calcNextTsumego has empty parent", false);
            return Optional.absent();
        }

        final List<String> fileList = Arrays.asList(fileNames);
        ;

        sort(fileList);

        final int inputFilePos = fileList.lastIndexOf(file.getName());
        if (inputFilePos + 1 < fileList.size()) {
            return Optional.of(dir.toString() + "/" + fileList.get(inputFilePos + 1));
        }
        return Optional.absent();
    }

}
