package org.ligi.gobandroid_hd;

import org.junit.Test;
import org.ligi.gobandroid_hd.logic.StatelessBoardCell;
import org.ligi.gobandroid_hd.logic.StatelessGoBoard;
import static org.assertj.core.api.Assertions.assertThat;

public class TheBoardCell {

    private StatelessGoBoard board = new StatelessGoBoard(19);

    @Test
    public void testTopLeftCellHasCorrectNeighbours() {

        final StatelessBoardCell tested = board.getCell(0, 0);

        assertThat(tested.getNeighbors()).containsOnly(board.getCell(0, 1), board.getCell(1, 0));
    }

    @Test
    public void testCellx0y1HasCorrectNeighbours() {

        final StatelessBoardCell tested = board.getCell(0, 1);
        assertThat(tested.getNeighbors()).containsOnly(board.getCell(0, 0), board.getCell(0, 2), board.getCell(1, 1));
    }


}