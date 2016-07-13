package org.ligi.gobandroid_hd;

import org.junit.Test;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.markers.GoMarker;
import org.ligi.gobandroid_hd.logic.markers.TextMarker;
import org.ligi.gobandroid_hd.logic.sgf.SGFReader;
import static org.assertj.core.api.Assertions.assertThat;

public class TheSGFReader extends AssetAwareTest {

    @Test
    public void testReadMinimal19x19SGF() throws Exception {
        GoGame game = SGFReader.sgf2game(readAsset("test_sgfs/minimal_19x19.sgf"), null);

        assertThat(game.getSize()).isEqualTo(19);
    }

    @Test
    public void testReadMinimal9x9SGF() throws Exception {
        GoGame game = SGFReader.sgf2game(readAsset("test_sgfs/minimal_9x9.sgf"), null);

        assertThat(game.getSize()).isEqualTo(9);
    }

    @Test
    public void testReadSmall19x19SGF() throws Exception {
        GoGame game = SGFReader.sgf2game(readAsset("test_sgfs/small_19x19.sgf"), null);

        assertThat(game.getSize()).isEqualTo(19);
        assertThat(game.getMetaData().getName()).isEqualTo("SMALL19x19");
    }

    @Test
    public void testReadSmall9x9SGF() throws Exception {
        GoGame game = SGFReader.sgf2game(readAsset("test_sgfs/small_9x9.sgf"), null);

        assertThat(game.getSize()).isEqualTo(9);
        assertThat(game.getMetaData().getName()).isEqualTo("SMALL9x9");
    }


    @Test
    // https://github.com/ligi/gobandroid/issues/106
    public void testReadSGFWithFirstMoveWhiteAndCapture() throws Exception {
        GoGame game = SGFReader.sgf2game(readAsset("test_sgfs/first_move_capture_and_white.sgf"), null);

        assertThat(game.findLastMove().getMovePos()).isEqualTo(2);
    }

    @Test
    public void testThatDefaultLabelWorks() throws Exception {
        GoGame game = SGFReader.sgf2game(readAsset("test_sgfs/default_marker.sgf"), null);

        final GoMarker createdMarker = game.findLastMove().getMarkers().get(0);
        assertThat(createdMarker.getX()).isEqualTo(1);
        assertThat(createdMarker.getY()).isEqualTo(2);
        assertThat(((TextMarker) createdMarker).getText()).isEqualTo("X");
    }

    @Test
    public void testThatNamedLabelWorks() throws Exception {
        GoGame game = SGFReader.sgf2game(readAsset("test_sgfs/named_marker.sgf"), null);

        final GoMarker createdMarker = game.findLastMove().getMarkers().get(0);
        assertThat(createdMarker.getX()).isEqualTo(1);
        assertThat(createdMarker.getY()).isEqualTo(2);
        assertThat(((TextMarker) createdMarker).getText()).isEqualTo("L");
    }

    @Test
    public void testThatKomiWorks() throws Exception {
        GoGame game = SGFReader.sgf2game(readAsset("test_sgfs/komi.sgf"), null);

        assertThat(game.getKomi()).isEqualTo(7f);
    }
}