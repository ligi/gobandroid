package org.ligi.gobandroid_hd.test_helper_functions

import androidx.test.InstrumentationRegistry.getInstrumentation
import org.ligi.gobandroid_hd.logic.sgf.SGFReader

fun readAsset(fileName: String) = readAsset(getInstrumentation().context, fileName)

fun readGame(fileName: String) = SGFReader.sgf2game(readAsset("sgf/$fileName.sgf"), null)
