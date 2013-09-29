package org.ligi.gobandroid_hd.ui.tsumego;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NextTsumegoDeterminator {


    /**
     * try to find next tsumego based on filename
     * searching the last number and incrementing it
     *
     * @param FileName
     * @return the filename found
     */
    public static String calcNextTsumego(String FileName) {
        String old_index = getLastNumberInStringOrNull(FileName);

        if (old_index == null) {
            return null;
        }

        int index = Integer.parseInt(old_index);

        String new_index = "";
        // add the leading zeroes
        for (int i = 0; i < old_index.length() - ((index + 1) / 10 + 1); i++) {
            new_index += "0";
        }

        new_index += "" + (index + 1);

        String guessed_fname_str = replaceLast(FileName, old_index, new_index);

        // check if it exists
        if (!new File(guessed_fname_str).exists()) {
            return null;
        }

        return guessed_fname_str;

    }

    private static String getLastNumberInStringOrNull(String fname) {
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(fname);

        String old_index = "";
        while (m.find()) {
            old_index = m.group();
        }

        // found no index -> exit
        if (old_index.equals("")) {
            return null;
        }

        return old_index;
    }

    private  static String replaceLast(String string, String from, String to) {
        int lastIndex = string.lastIndexOf(from);
        if (lastIndex < 0) {
            return string;
        }
        String tail = string.substring(lastIndex).replaceFirst(from, to);
        return string.substring(0, lastIndex) + tail;
    }

}
