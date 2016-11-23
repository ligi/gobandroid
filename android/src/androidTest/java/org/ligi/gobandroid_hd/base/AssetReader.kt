package org.ligi.gobandroid_hd.base

import android.support.test.InstrumentationRegistry.getInstrumentation
import org.ligi.gobandroid_hd.logic.sgf.SGFReader

object AssetReader {

    fun readAsset(fileName: String) = GobandroidTestBaseUtil.readAsset(getInstrumentation().context, fileName)

    fun readGame(fileName: String) = SGFReader.sgf2game(readAsset("sgf/$fileName.sgf"), null)

}
