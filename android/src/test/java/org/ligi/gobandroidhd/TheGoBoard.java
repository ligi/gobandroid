package org.ligi.gobandroidhd;

import org.junit.Test;
import org.ligi.gobandroid_hd.logic.GoBoard;
import org.ligi.gobandroid_hd.logic.sgf.SGFReader;
import static org.assertj.core.api.Assertions.assertThat;

public class TheGoBoard extends AssetAwareTest {

    @Test
    public void toStringWorksForMinimal2x2() throws Exception {
        final GoBoard board = SGFReader.sgf2game(readAsset("test_sgfs/minimal_2x2.sgf"), null).getCalcBoard();

        assertThat(board.toString()).isEqualTo("..\n..\n");
    }

    @Test
    public void toStringWorksFor2x2WithMoveTopLeft() throws Exception {
        final GoBoard board = SGFReader.sgf2game(readAsset("test_sgfs/2x2_move_topleft.sgf"), null).getCalcBoard();

        assertThat(board.toString()).isEqualTo("W.\n..\n");
    }

}