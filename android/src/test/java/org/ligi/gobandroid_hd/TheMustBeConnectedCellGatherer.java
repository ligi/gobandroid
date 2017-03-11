package org.ligi.gobandroid_hd;

import org.junit.Test;
import org.ligi.gobandroid_hd.logic.GoDefinitions;
import org.ligi.gobandroid_hd.logic.StatefulGoBoard;
import org.ligi.gobandroid_hd.logic.StatelessBoardCell;
import org.ligi.gobandroid_hd.logic.StatelessGoBoard;
import org.ligi.gobandroid_hd.logic.cell_gatherer.MustBeConnectedCellGatherer;
import static org.assertj.core.api.Assertions.assertThat;

public class TheMustBeConnectedCellGatherer {

    private StatefulGoBoard board = new StatefulGoBoard(new StatelessGoBoard(9));

    @Test
    public void testEmptyBoardIsOneGroup() {

        final StatelessBoardCell cell = board.getCell(0, 0);

        final MustBeConnectedCellGatherer boardCells = new MustBeConnectedCellGatherer(board, cell);

        assertThat(boardCells.getGatheredCells()).hasSize(board.getSize() * board.getSize());
    }

    @Test
    public void testSingleCellIsSingle() {

        final StatelessBoardCell cell = board.getCell(0, 0);
        board.setCell(cell, GoDefinitions.STONE_BLACK);

        final MustBeConnectedCellGatherer boardCells = new MustBeConnectedCellGatherer(board, cell);

        assertThat(boardCells.getGatheredCells()).contains(cell);
    }

    @Test
    public void testDiagonalDoesNotCount() {

        final StatelessBoardCell cell = board.getCell(0, 0);
        board.setCell(cell, GoDefinitions.STONE_BLACK);

        final StatelessBoardCell cell2 = board.getCell(1, 1);
        board.setCell(cell2, GoDefinitions.STONE_BLACK);

        final MustBeConnectedCellGatherer boardCells = new MustBeConnectedCellGatherer(board, cell);

        assertThat(boardCells.getGatheredCells()).contains(cell);
    }


    @Test
    public void testSmallGroup() {

        final StatelessBoardCell cell = board.getCell(0, 0);
        board.setCell(cell, GoDefinitions.STONE_BLACK);

        final StatelessBoardCell cell2 = board.getCell(0, 1);
        board.setCell(cell2, GoDefinitions.STONE_BLACK);

        final MustBeConnectedCellGatherer boardCells = new MustBeConnectedCellGatherer(board, cell);

        assertThat(boardCells.getGatheredCells()).contains(cell, cell2);
    }

}