package org.ligi;

import org.junit.runner.RunWith;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.sgf.SGFReader;
import org.ligi.util.SGFProvider;
import org.robolectric.RobolectricTestRunner;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class TheSGFReader {

    @org.junit.Test
	public void should_recognize19x19() throws Exception {
        GoGame game=SGFReader.sgf2game(SGFProvider.DEFAULT_SGF_19x19,null);

        assertThat(game.getSize()).isEqualTo(19);
    }

    @org.junit.Test
    public void should_recognize9x9() throws Exception {
        GoGame game=SGFReader.sgf2game(SGFProvider.DEFAULT_SGF_9x9,null);

        assertThat(game.getSize()).isEqualTo(9);
    }

}