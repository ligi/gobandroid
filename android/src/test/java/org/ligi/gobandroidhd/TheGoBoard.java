package org.ligi.gobandroidhd;

import org.junit.Test;
import org.ligi.gobandroid_hd.logic.Cell;
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


    @Test
    public void toThatIsCellOnBoardWorksForOutside() throws Exception {
        final GoBoard board = new GoBoard(9);

        assertThat(board.isCellOnBoard(new Cell(10, 1))).isFalse();
    }


    @Test
    public void toThatIsCellOnBoardWorksForInside() throws Exception {
        final GoBoard board = new GoBoard(9);

        assertThat(board.isCellOnBoard(new Cell(4, 4))).isTrue();
    }
}