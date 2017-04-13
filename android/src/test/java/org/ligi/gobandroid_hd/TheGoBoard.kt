package org.ligi.gobandroid_hd

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.ligi.gobandroid_hd.logic.CellImpl
import org.ligi.gobandroid_hd.logic.StatelessGoBoard
import org.ligi.gobandroid_hd.logic.sgf.SGFReader

class TheGoBoard {

    @Test
    fun toStringWorksForMinimal2x2() {
        val board = SGFReader.sgf2game(readAsset("test_sgfs/minimal_2x2.sgf"), null)!!.calcBoard

        assertThat(board.toString()).isEqualTo("..\n..\n")
    }

    @Test
    fun toStringWorksFor2x2WithMoveTopLeft() {
        val board = SGFReader.sgf2game(readAsset("test_sgfs/2x2_move_topleft.sgf"), null)!!.calcBoard

        assertThat(board.toString()).isEqualTo("W.\n..\n")
    }


    @Test
    fun toThatIsCellOnBoardWorksForOutside() {
        val board = StatelessGoBoard(9)

        assertThat(board.isCellOnBoard(CellImpl(10, 1))).isFalse()
    }


    @Test
    fun toThatIsCellOnBoardWorksForInside() {
        val board = StatelessGoBoard(9)

        assertThat(board.isCellOnBoard(CellImpl(4, 4))).isTrue()
    }
}