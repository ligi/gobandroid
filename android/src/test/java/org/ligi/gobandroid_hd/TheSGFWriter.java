package org.ligi.gobandroid_hd;

import org.junit.Test;
import org.ligi.axt.AXT;
import org.ligi.gobandroid_hd.helper.SGFFileNameFilter;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.sgf.SGFReader;
import org.ligi.gobandroid_hd.logic.sgf.SGFWriter;

import java.io.File;

import static junit.framework.Assert.fail;

public class TheSGFWriter {

    @Test
    public void testAllSGFsShouldSurviveRoundtrip() throws Exception {

        final File test_sgf_dir = new File(getClass().getClassLoader().getResource("test_sgfs").toURI());

        for (String sgf : test_sgf_dir.list(new SGFFileNameFilter())) {
            final GoGame game = SGFReader.sgf2game(AXT.at(new File(test_sgf_dir, sgf)).readToString(), null);
            final String newSGF = SGFWriter.INSTANCE.game2sgf(game);
            final GoGame newGame = SGFReader.sgf2game(newSGF, null);
            if (!game.isContentEqualTo(newGame)) {
                fail("game did not survive the read->write->read RoundTrip " + sgf + " " + game.getMetaData().getName());
            }
        }
    }

}