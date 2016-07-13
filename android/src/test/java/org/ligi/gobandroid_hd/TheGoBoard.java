package org.ligi.gobandroid_hd;

import org.junit.Test;
import org.ligi.gobandroid_hd.logic.CellImpl;
import org.ligi.gobandroid_hd.logic.StatefulGoBoard;
import org.ligi.gobandroid_hd.logic.StatelessGoBoard;
import org.ligi.gobandroid_hd.logic.sgf.SGFReader;

import static org.assertj.core.api.Assertions.assertThat;

public class TheGoBoard extends AssetAwareTest {

    @Test
    public void toStringWorksForMinimal2x2() throws Exception {
        final StatefulGoBoard board = SGFReader.sgf2game(readAsset("test_sgfs/minimal_2x2.sgf"), null).getCalcBoard();

        assertThat(board.toString()).isEqualTo("..\n..\n");
    }

    @Test
    public void toStringWorksFor2x2WithMoveTopLeft() throws Exception {
        final StatefulGoBoard board = SGFReader.sgf2game(readAsset("test_sgfs/2x2_move_topleft.sgf"), null).getCalcBoard();

        assertThat(board.toString()).isEqualTo("W.\n..\n");
    }


    @Test
    public void toThatIsCellOnBoardWorksForOutside() throws Exception {
        final StatelessGoBoard board = new StatelessGoBoard(9);

        assertThat(board.isCellOnBoard(new CellImpl(10, 1))).isFalse();
    }


    @Test
    public void toThatIsCellOnBoardWorksForInside() throws Exception {
        final StatelessGoBoard board = new StatelessGoBoard(9);

        assertThat(board.isCellOnBoard(new CellImpl(4, 4))).isTrue();
    }
}