package org.ligi.gobandroidhd;

import org.junit.Test;
import org.ligi.gobandroid_hd.logic.BoardCell;
import org.ligi.gobandroid_hd.logic.StatefulGoBoard;
import org.ligi.gobandroid_hd.logic.GoDefinitions;
import org.ligi.gobandroid_hd.logic.StatelessGoBoard;
import org.ligi.gobandroid_hd.logic.cell_gatherer.MustBeConnectedCellGatherer;
import static org.assertj.core.api.Assertions.assertThat;

public class TheMustBeConnectedCellGatherer extends MarkerTestBase {

    private StatefulGoBoard board = new StatefulGoBoard(new StatelessGoBoard(9));

    @Test
    public void testEmptyBoardIsOneGroup() {

        final BoardCell cell = board.getCell(0, 0);

        final MustBeConnectedCellGatherer boardCells = new MustBeConnectedCellGatherer(cell);

        assertThat(boardCells).hasSize(board.getSize() * board.getSize());
    }

    @Test
    public void testSingleCellIsSingle() {

        final BoardCell cell = board.getCell(0, 0);
        board.setCell(cell, GoDefinitions.STONE_BLACK);

        final MustBeConnectedCellGatherer boardCells = new MustBeConnectedCellGatherer(cell);

        assertThat(boardCells).contains(cell);
    }

    @Test
    public void testDiagonalDoesNotCount() {

        final BoardCell cell = board.getCell(0, 0);
        board.setCell(cell, GoDefinitions.STONE_BLACK);

        final BoardCell cell2 = board.getCell(1, 1);
        board.setCell(cell2, GoDefinitions.STONE_BLACK);

        final MustBeConnectedCellGatherer boardCells = new MustBeConnectedCellGatherer(cell);

        assertThat(boardCells).contains(cell);
    }


    @Test
    public void testSmallGroup() {

        final BoardCell cell = board.getCell(0, 0);
        board.setCell(cell, GoDefinitions.STONE_BLACK);

        final BoardCell cell2 = board.getCell(0, 1);
        board.setCell(cell2, GoDefinitions.STONE_BLACK);

        final MustBeConnectedCellGatherer boardCells = new MustBeConnectedCellGatherer(cell);

        assertThat(boardCells).contains(cell, cell2);
    }

}