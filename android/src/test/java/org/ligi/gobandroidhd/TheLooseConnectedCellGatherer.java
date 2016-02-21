package org.ligi.gobandroidhd;

import org.junit.Test;
import org.ligi.gobandroid_hd.logic.BoardCell;
import org.ligi.gobandroid_hd.logic.StatefulGoBoard;
import org.ligi.gobandroid_hd.logic.StatelessGoBoard;
import org.ligi.gobandroid_hd.logic.cell_gatherer.LooseConnectedCellGatherer;

import static org.assertj.core.api.Assertions.assertThat;

public class TheLooseConnectedCellGatherer extends MarkerTestBase {

    private StatefulGoBoard board = new StatefulGoBoard(new StatelessGoBoard(9));

    @Test
    public void testEmptyBoardIsOneGroup() {

        final BoardCell cell = board.getCell(0, 0);

        final LooseConnectedCellGatherer boardCells = new LooseConnectedCellGatherer(cell, board);

        assertThat(boardCells).hasSize(board.getSize() * board.getSize());
    }

}