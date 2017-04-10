package org.ligi.gobandroid_hd;

import org.junit.Test;
import org.ligi.gobandroid_hd.logic.CellImpl;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoMove;

import static org.assertj.core.api.Assertions.assertThat;

public class TheGoGame {

    @Test
    public void testReplayingMoves() {
        final GoGame goGame = new GoGame(9);
        goGame.do_move(new CellImpl(0, 0));
        goGame.do_move(new CellImpl(0, 1));
        goGame.do_move(new CellImpl(0, 2));
        goGame.do_move(new CellImpl(0, 3));

        goGame.undo();
        goGame.undo();
        goGame.undo();

        assertThat(goGame.do_move(new CellImpl(0, 1))).isEqualTo(GoGame.MoveStatus.VALID);
        assertThat(goGame.do_move(new CellImpl(0, 2))).isEqualTo(GoGame.MoveStatus.VALID);
        assertThat(goGame.do_move(new CellImpl(0, 3))).isEqualTo(GoGame.MoveStatus.VALID);
    }

    @Test
    public void testOffBoardRejection() {
        final GoGame goGame = new GoGame(9);
        assertThat(goGame.do_move(new CellImpl(9, 9))).isEqualTo(GoGame.MoveStatus.INVALID_NOT_ON_BOARD);
    }

    @Test
    public void testCapture() {
        final GoGame goGame = new GoGame(9);
        goGame.do_move(new CellImpl(0, 1));
        goGame.do_move(new CellImpl(0, 0));
        goGame.do_move(new CellImpl(1, 0));
        assertThat(goGame.getCapturesBlack()).isEqualTo(1);
    }

    @Test
    public void testPreviousVarMoveWorks() {
        final GoGame goGame = new GoGame(9);
        goGame.do_move(new CellImpl(0, 1));
        goGame.undo();

        final GoMove previousVarMove = goGame.actMove.getNextMoveOnCell(new CellImpl(0, 1));
        goGame.do_move(new CellImpl(0, 0));
        goGame.undo();
        goGame.do_move(new CellImpl(1, 0));
        goGame.undo();
        goGame.do_move(new CellImpl(0, 0));
        assertThat(goGame.nextVariationWithOffset(-1)).isEqualTo(previousVarMove);
    }

    @Test
    public void testNextVarMoveWorks() {
        final GoGame goGame = new GoGame(9);
        goGame.do_move(new CellImpl(0, 1));
        goGame.undo();

        goGame.do_move(new CellImpl(0, 0));
        goGame.undo();
        goGame.do_move(new CellImpl(1, 0));
        goGame.undo();
        final GoMove nextVarMove = goGame.actMove.getNextMoveOnCell(new CellImpl(1, 0));
        goGame.do_move(new CellImpl(0, 0));
        assertThat(goGame.nextVariationWithOffset(1)).isEqualTo(nextVarMove);
    }

    @Test
    public void testInvalidCellNotFree() {
        final GoGame goGame = new GoGame(9);
        goGame.do_move(new CellImpl(1, 1));
        assertThat(goGame.do_move(new CellImpl(1, 1))).isEqualTo(GoGame.MoveStatus.INVALID_CELL_NOT_FREE);
    }

    @Test
    public void testInvalidKo() {
        final GoGame goGame = new GoGame(9);
        goGame.do_move(new CellImpl(1, 1));
        goGame.do_move(new CellImpl(0, 1));
        goGame.do_move(new CellImpl(2, 0));
        goGame.do_move(new CellImpl(1, 0));
        goGame.do_move(new CellImpl(0, 0));
        assertThat(goGame.do_move(new CellImpl(1, 0))).isEqualTo(GoGame.MoveStatus.INVALID_IS_KO);
    }

    @Test
    public void testInvalidNoLiberties() {
        final GoGame goGame = new GoGame(9);
        goGame.do_move(new CellImpl(0, 1));
        goGame.do_move(new CellImpl(1, 1));
        goGame.do_move(new CellImpl(1, 0));
        assertThat(goGame.do_move(new CellImpl(0, 0))).isEqualTo(GoGame.MoveStatus.INVALID_CELL_NO_LIBERTIES);
    }

    @Test
    public void testReposition() {
        final GoGame goGame = new GoGame(9);
        goGame.do_move(new CellImpl(0, 1));
        assertThat(goGame.actMove.getCell()).isEqualTo(new CellImpl(0, 1));
        assertThat(goGame.repositionActMove(new CellImpl(1, 1))).isEqualTo(GoGame.MoveStatus.VALID);
        assertThat(goGame.actMove.getCell()).isEqualTo(new CellImpl(1, 1));

        assertThat(goGame.repositionActMove(new CellImpl(9, 9))).isEqualTo(GoGame.MoveStatus.INVALID_NOT_ON_BOARD);

        goGame.do_move(new CellImpl(0, 1));
        assertThat(goGame.repositionActMove(new CellImpl(1, 1))).isEqualTo(GoGame.MoveStatus.INVALID_CELL_NOT_FREE);

        goGame.do_move(new CellImpl(2, 0));
        goGame.do_move(new CellImpl(1, 0));
        goGame.do_move(new CellImpl(0, 0));
        goGame.do_move(new CellImpl(5, 5));
        assertThat(goGame.repositionActMove(new CellImpl(1, 0))).isEqualTo(GoGame.MoveStatus.INVALID_IS_KO);

        goGame.do_move(new CellImpl(0, 7));
        goGame.do_move(new CellImpl(0, 8));
        goGame.do_move(new CellImpl(1, 8));
        goGame.do_move(new CellImpl(2, 8));
        assertThat(goGame.repositionActMove(new CellImpl(0, 8))).isEqualTo(GoGame.MoveStatus.INVALID_CELL_NO_LIBERTIES);
    }

    @Test
    public void testRepositionCapture() {
        final GoGame goGame = new GoGame(9);
        goGame.do_move(new CellImpl(0, 1));
        goGame.do_move(new CellImpl(0, 0));
        goGame.do_move(new CellImpl(2, 2));
        goGame.repositionActMove(new CellImpl(1, 0));
        assertThat(goGame.getCapturesBlack()).isEqualTo(1);
        goGame.repositionActMove(new CellImpl(2, 2));
        assertThat(goGame.getCapturesBlack()).isEqualTo(0);
    }
}