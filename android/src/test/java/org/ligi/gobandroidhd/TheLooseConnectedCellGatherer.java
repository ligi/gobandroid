package org.ligi.gobandroidhd;

import org.junit.Test;
import org.ligi.gobandroid_hd.logic.BoardCell;
import org.ligi.gobandroid_hd.logic.GoBoard;
import org.ligi.gobandroid_hd.logic.cell_gatherer.LooseConnectedCellGatherer;
import static org.assertj.core.api.Assertions.assertThat;

public class TheLooseConnectedCellGatherer extends MarkerTestBase {

    private GoBoard board = new GoBoard(9);

    @Test
    public void testEmptyBoardIsOneGroup() {

        final BoardCell cell = board.getCell(0, 0);

        final LooseConnectedCellGatherer boardCells = new LooseConnectedCellGatherer(cell);

        assertThat(boardCells).hasSize(board.getSize() * board.getSize());
    }

}