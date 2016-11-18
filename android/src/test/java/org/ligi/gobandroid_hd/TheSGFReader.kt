package org.ligi.gobandroid_hd

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.ligi.gobandroid_hd.logic.markers.TextMarker
import org.ligi.gobandroid_hd.logic.sgf.SGFReader

class TheSGFReader {

    @Test
    fun testReadMinimal19x19SGF() {
        val game = SGFReader.sgf2game(readAsset("test_sgfs/minimal_19x19.sgf"), null)

        assertThat(game.size).isEqualTo(19)
    }

    @Test
    fun testProblem() {
        val game = SGFReader.sgf2game(readAsset("test_sgfs/bad_komi.sgf"), null)

        assertThat(game.size).isEqualTo(9)
    }

    @Test
    fun testReadMinimal9x9SGF() {
        val game = SGFReader.sgf2game(readAsset("test_sgfs/minimal_9x9.sgf"), null)

        assertThat(game.size).isEqualTo(9)
    }

    @Test
    fun testReadSmall19x19SGF() {
        val game = SGFReader.sgf2game(readAsset("test_sgfs/small_19x19.sgf"), null)

        assertThat(game.size).isEqualTo(19)
        assertThat(game.metaData.name).isEqualTo("SMALL19x19")
    }

    @Test
    fun testEmptySZWorks() {
        val game = SGFReader.sgf2game(readAsset("test_sgfs/empty_SZ.sgf"), null)

        assertThat(game.size).isEqualTo(19)
    }

    @Test
    fun testReadSmall9x9SGF() {
        val game = SGFReader.sgf2game(readAsset("test_sgfs/small_9x9.sgf"), null)

        assertThat(game.size).isEqualTo(9)
        assertThat(game.metaData.name).isEqualTo("SMALL9x9")
    }


    @Test
            // https://github.com/ligi/gobandroid/issues/106
    fun testReadSGFWithFirstMoveWhiteAndCapture() {
        val game = SGFReader.sgf2game(readAsset("test_sgfs/first_move_capture_and_white.sgf"), null)

        assertThat(game.findLastMove().movePos).isEqualTo(2)
    }

    @Test
    fun testThatDefaultLabelWorks() {
        val game = SGFReader.sgf2game(readAsset("test_sgfs/default_marker.sgf"), null)

        val createdMarker = game.findLastMove().markers[0]
        assertThat(createdMarker.x).isEqualTo(1)
        assertThat(createdMarker.y).isEqualTo(2)
        assertThat((createdMarker as TextMarker).text).isEqualTo("X")
    }

    @Test
    fun testThatNamedLabelWorks() {
        val game = SGFReader.sgf2game(readAsset("test_sgfs/named_marker.sgf"), null)

        val createdMarker = game.findLastMove().markers[0]
        assertThat(createdMarker.x).isEqualTo(1)
        assertThat(createdMarker.y).isEqualTo(2)
        assertThat((createdMarker as TextMarker).text).isEqualTo("L")
    }

    @Test
    fun testThatKomiWorks() {
        val game = SGFReader.sgf2game(readAsset("test_sgfs/komi.sgf"), null)

        assertThat(game.komi).isEqualTo(7f)
    }
}