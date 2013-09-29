package org.ligi;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.sgf.SGFReader;
import org.robolectric.RobolectricTestRunner;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class TheSGFReader {


    public static final String DEFAULT_SGF_19x19 = "(;GM[1]FF[4]CA[UTF-8]AP[CGoban:3]ST[2]RU[Japanese]SZ[19]KM[0.00]AW[aa](B[ab]))";
    public static final String DEFAULT_SGF_9x9 = "(;GM[1]FF[4]CA[UTF-8]AP[CGoban:3]ST[2]RU[Japanese]SZ[9]KM[0.00]AW[aa](B[ab]))";

    @Before
    public void setUp() {
    }

    @org.junit.Test
	public void should_recognize19x19() throws Exception {
        GoGame game=SGFReader.sgf2game(DEFAULT_SGF_19x19,null);

        assertThat(game.getSize()).isEqualTo(19);
    }

    @org.junit.Test
    public void should_recognize9x9() throws Exception {
        GoGame game=SGFReader.sgf2game(DEFAULT_SGF_9x9,null);

        assertThat(game.getSize()).isEqualTo(9);
    }

}