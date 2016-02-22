package org.ligi.gobandroidhd;

import org.junit.Test;
import org.ligi.gobandroid_hd.logic.StatefulGoBoard;
import org.ligi.gobandroid_hd.logic.StatelessBoardCell;
import org.ligi.gobandroid_hd.logic.StatelessGoBoard;
import org.ligi.gobandroid_hd.logic.cell_gatherer.LooseConnectedCellGatherer;

import static org.assertj.core.api.Assertions.assertThat;

public class TheLooseConnectedCellGatherer extends MarkerTestBase {

    private StatefulGoBoard board = new StatefulGoBoard(new StatelessGoBoard(9));

    @Test
    public void testEmptyBoardIsOneGroup() {

        final StatelessBoardCell cell = board.getStatelessGoBoard().getCell(0, 0);

        final LooseConnectedCellGatherer boardCells = new LooseConnectedCellGatherer(board, cell);

        assertThat(boardCells.getGatheredCells()).hasSize(board.getSize() * board.getSize());
    }

}