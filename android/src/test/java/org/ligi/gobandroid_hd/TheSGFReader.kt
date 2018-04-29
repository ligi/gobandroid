package org.ligi.gobandroid_hd

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.ligi.gobandroid_hd.logic.CellImpl
import org.ligi.gobandroid_hd.logic.GoDefinitions
import org.ligi.gobandroid_hd.logic.GoMove
import org.ligi.gobandroid_hd.logic.markers.TextMarker
import org.ligi.gobandroid_hd.logic.sgf.SGFReader

class TheSGFReader {

    @Test
    fun testReadMinimal19x19SGF() {
        val game = SGFReader.sgf2game(readAsset("test_sgfs/minimal_19x19.sgf"), null)!!

        assertThat(game.size).isEqualTo(19)
    }

    @Test
    fun testProblem() {
        val game = SGFReader.sgf2game(readAsset("test_sgfs/bad_komi.sgf"), null)!!

        assertThat(game.size).isEqualTo(9)
    }

    @Test
    fun testReadMinimal9x9SGF() {
        val game = SGFReader.sgf2game(readAsset("test_sgfs/minimal_9x9.sgf"), null)!!

        assertThat(game.size).isEqualTo(9)
    }

    @Test
    fun testReadSmall19x19SGF() {
        val game = SGFReader.sgf2game(readAsset("test_sgfs/small_19x19.sgf"), null)!!

        assertThat(game.size).isEqualTo(19)
        assertThat(game.metaData.name).isEqualTo("SMALL19x19")
    }

    @Test
    fun testEmptySZWorks() {
        val game = SGFReader.sgf2game(readAsset("test_sgfs/empty_SZ.sgf"), null)!!

        assertThat(game.size).isEqualTo(19)
    }

    @Test
    fun testReadSmall9x9SGF() {
        val game = SGFReader.sgf2game(readAsset("test_sgfs/small_9x9.sgf"), null)!!

        assertThat(game.size).isEqualTo(9)
        assertThat(game.metaData.name).isEqualTo("SMALL9x9")
    }


    @Test
            // https://github.com/ligi/gobandroid/issues/106
    fun testReadSGFWithFirstMoveWhiteAndCapture() {
        val game = SGFReader.sgf2game(readAsset("test_sgfs/first_move_capture_and_white.sgf"), null)!!
        assertThat(game.findLastMove().movePos).isEqualTo(2)
        assertThat(game.findLastMove().player).isEqualTo(GoDefinitions.PLAYER_BLACK)

        val moveZero = game.actMove
        assertThat(moveZero.hasNextMove()).isTrue()
        val nextMoveCell = moveZero.getnextMove(0)!!.cell!!
        assertThat(nextMoveCell).isNotNull()
        val cellToCapture = CellImpl(nextMoveCell.x -1, nextMoveCell.y)
        assertThat(game.calcBoard.getCellKind(cellToCapture)).isEqualTo(GoDefinitions.STONE_BLACK)
        game.redo(0)
        assertThat(game.calcBoard.getCellKind(cellToCapture)).isEqualTo(GoDefinitions.STONE_NONE)
        val redoMove = game.actMove
        assertThat(redoMove.captures.size).isEqualTo(1)
    }

    @Test
    fun testThatDefaultLabelWorks() {
        val game = SGFReader.sgf2game(readAsset("test_sgfs/default_marker.sgf"), null)!!

        val createdMarker = game.findLastMove().markers[0]
        assertThat(createdMarker.x).isEqualTo(1)
        assertThat(createdMarker.y).isEqualTo(2)
        assertThat((createdMarker as TextMarker).text).isEqualTo("X")
    }

    @Test
    fun testThatNamedLabelWorks() {
        val game = SGFReader.sgf2game(readAsset("test_sgfs/named_marker.sgf"), null)!!

        val createdMarker = game.findLastMove().markers[0]
        assertThat(createdMarker.x).isEqualTo(1)
        assertThat(createdMarker.y).isEqualTo(2)
        assertThat((createdMarker as TextMarker).text).isEqualTo("L")
    }

    @Test
    fun testThatKomiWorks() {
        val game = SGFReader.sgf2game(readAsset("test_sgfs/komi.sgf"), null)!!

        assertThat(game.komi).isEqualTo(7f)
    }

    @Test
    fun testCorrectMove() {
        val game = SGFReader.sgf2game(readAsset("test_sgfs/ggg-easy-07.sgf"), null)
        var move: GoMove? = game?.actMove
        assertThat(move!!.nextMoveVariationCount).isEqualTo(4)
        move = move.getNextMoveOnCell(CellImpl(16,16))
        move = move!!.getNextMoveOnCell(CellImpl(18,16))
        move = move!!.getNextMoveOnCell(CellImpl(18,17))
        move = move!!.getNextMoveOnCell(CellImpl(17,18))

        //the next move is missing from the variation list
        move = move!!.getNextMoveOnCell(CellImpl(18,17))
        move = move!!.getNextMoveOnCell(CellImpl(18,18))
        move = move!!.getNextMoveOnCell(CellImpl(18,17))
        assertThat(move!!.comment).isEqualToIgnoringCase("CORRECT")
    }
}