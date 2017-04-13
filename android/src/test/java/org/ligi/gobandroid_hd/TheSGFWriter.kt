package org.ligi.gobandroid_hd

import junit.framework.Assert.fail
import org.junit.Test
import org.ligi.gobandroid_hd.helper.SGFFileNameFilter
import org.ligi.gobandroid_hd.logic.sgf.SGFReader
import org.ligi.gobandroid_hd.logic.sgf.SGFWriter
import java.io.File

class TheSGFWriter {

    @Test
    fun testAllSGFsShouldSurviveRoundtrip() {

        val test_sgf_dir = File(javaClass.classLoader.getResource("test_sgfs").toURI())

        for (sgf in test_sgf_dir.list(SGFFileNameFilter())) {
            val game = SGFReader.sgf2game(File(test_sgf_dir, sgf).readText(), null)!!
            val newSGF = SGFWriter.game2sgf(game)
            val newGame = SGFReader.sgf2game(newSGF, null)!!
            if (!game.isContentEqualTo(newGame)) {
                fail("game did not survive the read->write->read RoundTrip " + sgf + " " + game.metaData.name)
            }
        }
    }

}