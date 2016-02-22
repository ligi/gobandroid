package org.ligi.gobandroidhd;

import org.junit.Test;
import org.ligi.gobandroid_hd.logic.CellImpl;
import org.ligi.gobandroid_hd.logic.GoGame;

import static org.assertj.core.api.Assertions.assertThat;

public class TheGoGame extends AssetAwareTest {

    @Test
    public void testReplayingMoves() throws Exception {
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
    public void testOffBoardRejection() throws Exception {
        final GoGame goGame = new GoGame(9);

        assertThat(goGame.do_move(new CellImpl(9, 9))).isEqualTo(GoGame.MoveStatus.INVALID_NOT_ON_BOARD);
    }

}