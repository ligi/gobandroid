package org.ligi;

import org.junit.runner.RunWith;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.sgf.SGFReader;
import org.ligi.gobandroid_hd.logic.sgf.SGFWriter;
import org.ligi.util.SGFProvider;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.fail;

@RunWith(RobolectricTestRunner.class)
public class TheSGFWriter {

    @org.junit.Test
    public void all_sgfs_should_survive_roundtrip() throws Exception {
        for (String sgf : SGFProvider.ALL_SGFS) {
            GoGame game = SGFReader.sgf2game(sgf, null);
            String newSGF = SGFWriter.game2sgf(game);
            GoGame newGame = SGFReader.sgf2game(newSGF, null);
            if (!game.isContentEqualTo(newGame)) {
                fail("game did not survive the read->write->read RoundTrip " + game.getMetaData().getName());
            }
        }
    }

}