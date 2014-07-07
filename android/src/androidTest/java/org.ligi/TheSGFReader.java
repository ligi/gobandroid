package org.ligi;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.sgf.SGFReader;
import org.ligi.util.SGFProvider;

import static org.fest.assertions.api.Assertions.assertThat;

public class TheSGFReader extends AndroidTestCase {

    @SmallTest
    public void test_should_recognize19x19() throws Exception {
        GoGame game=SGFReader.sgf2game(SGFProvider.DEFAULT_SGF_19x19,null);

        assertThat(game.getSize()).isEqualTo(19);
    }

    @SmallTest
    public void test_should_recognize9x9() throws Exception {
        GoGame game=SGFReader.sgf2game(SGFProvider.DEFAULT_SGF_9x9,null);

        assertThat(game.getSize()).isEqualTo(9);
    }

}