package org.ligi;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.sgf.SGFReader;
import org.ligi.gobandroid_hd.logic.sgf.SGFWriter;
import org.ligi.util.SGFProvider;

public class TheSGFWriter extends AndroidTestCase {

    @SmallTest
    public void test_all_sgfs_should_survive_roundtrip() throws Exception {
        for (String sgf : SGFProvider.ALL_SGFS) {
            final GoGame game = SGFReader.sgf2game(sgf, null);
            final String newSGF = SGFWriter.game2sgf(game);
            final GoGame newGame = SGFReader.sgf2game(newSGF, null);
            if (!game.isContentEqualTo(newGame)) {
                fail("game did not survive the read->write->read RoundTrip " + game.getMetaData().getName());
            }
        }
    }

}