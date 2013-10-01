package org.ligi.util;

import java.util.List;

import static java.util.Arrays.asList;

public class SGFProvider {
    public static final String DEFAULT_SGF_19x19 = "(;GM[1]FF[4]CA[UTF-8]AP[CGoban:3]ST[2]RU[Japanese]SZ[19]KM[0.00]AW[aa](B[ab]))";
    public static final String DEFAULT_SGF_9x9 = "(;GM[1]FF[4]CA[UTF-8]AP[CGoban:3]ST[2]RU[Japanese]SZ[9]KM[0.00]AW[aa](B[ab]))";
    public static final List<String> ALL_SGFS = asList(DEFAULT_SGF_19x19,DEFAULT_SGF_9x9);
}
