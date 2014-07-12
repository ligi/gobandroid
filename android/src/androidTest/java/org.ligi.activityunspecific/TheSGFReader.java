package org.ligi;

import android.test.suitebuilder.annotation.SmallTest;

import org.ligi.base.AssetAwareInstrumentationTestCase;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.sgf.SGFReader;

import static org.fest.assertions.api.Assertions.assertThat;

public class TheSGFReader extends AssetAwareInstrumentationTestCase {

    @SmallTest
    public void testReadSGFWithFirstMoveWhiteAndCapture() throws Exception {
        GoGame game = SGFReader.sgf2game(readAsset("sgf/first_move_capture_and_white.sgf"), null);

        assertThat(game.getLastMove().getMovePos()).isEqualTo(2);
    }

    @SmallTest
    public void testReadMinimal19x19SGF() throws Exception {
        GoGame game = SGFReader.sgf2game(readAsset("sgf/minimal_19x19.sgf"), null);

        assertThat(game.getSize()).isEqualTo(19);
    }

    @SmallTest
    public void testReadMinimal9x9SGF() throws Exception {
        GoGame game = SGFReader.sgf2game(readAsset("sgf/minimal_9x9.sgf"), null);

        assertThat(game.getSize()).isEqualTo(9);
    }

    @SmallTest
    public void testReadSmall19x19SGF() throws Exception {
        GoGame game = SGFReader.sgf2game(readAsset("sgf/small_19x19.sgf"), null);

        assertThat(game.getSize()).isEqualTo(19);
    }

    @SmallTest
    public void testReadSmall9x9SGF() throws Exception {
        GoGame game = SGFReader.sgf2game(readAsset("sgf/small_9x9.sgf"), null);

        assertThat(game.getSize()).isEqualTo(9);
    }

}