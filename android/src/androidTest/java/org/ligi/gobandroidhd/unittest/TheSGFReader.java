package org.ligi.gobandroidhd.unittest;

import android.test.suitebuilder.annotation.SmallTest;

import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.markers.GoMarker;
import org.ligi.gobandroid_hd.logic.sgf.SGFReader;
import org.ligi.gobandroidhd.base.AssetAwareInstrumentationTestCase;

import static org.assertj.core.api.Assertions.assertThat;

public class TheSGFReader extends AssetAwareInstrumentationTestCase {

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
        assertThat(game.getMetaData().getName()).isEqualTo("SMALL19x19");
    }

    @SmallTest
    public void testReadSmall9x9SGF() throws Exception {
        GoGame game = SGFReader.sgf2game(readAsset("sgf/small_9x9.sgf"), null);

        assertThat(game.getSize()).isEqualTo(9);
        assertThat(game.getMetaData().getName()).isEqualTo("SMALL9x9");
    }


    @SmallTest
    // https://github.com/ligi/gobandroid/issues/106
    public void testReadSGFWithFirstMoveWhiteAndCapture() throws Exception {
        GoGame game = SGFReader.sgf2game(readAsset("sgf/first_move_capture_and_white.sgf"), null);

        assertThat(game.getLastMove().getMovePos()).isEqualTo(2);
    }

    @SmallTest
    public void testThatDefaultLabelWorks() throws Exception {
        GoGame game = SGFReader.sgf2game(readAsset("sgf/default_marker.sgf"), null);

        final GoMarker createdMarker = game.getLastMove().getMarkers().get(0);
        assertThat(createdMarker.getX()).isEqualTo((byte) 1);
        assertThat(createdMarker.getY()).isEqualTo((byte) 2);
        assertThat(createdMarker.getText()).isEqualTo("X");
    }

    @SmallTest
    public void testThatNamedLabelWorks() throws Exception {
        GoGame game = SGFReader.sgf2game(readAsset("sgf/named_marker.sgf"), null);

        final GoMarker createdMarker = game.getLastMove().getMarkers().get(0);
        assertThat(createdMarker.getX()).isEqualTo((byte) 1);
        assertThat(createdMarker.getY()).isEqualTo((byte) 2);
        assertThat(createdMarker.getText()).isEqualTo("L");
    }
}