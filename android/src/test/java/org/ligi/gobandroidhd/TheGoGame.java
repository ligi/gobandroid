package org.ligi.gobandroidhd;

import org.junit.Test;
import org.ligi.gobandroid_hd.logic.CellImpl;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoMove;
import static org.assertj.core.api.Assertions.assertThat;

public class TheGoGame extends AssetAwareTest {

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
    public void tesNextVarMoveWorks() {
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
}