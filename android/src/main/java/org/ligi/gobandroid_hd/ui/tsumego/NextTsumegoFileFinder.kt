package org.ligi.gobandroid_hd.ui.tsumego

import org.ligi.gobandroid_hd.App
import org.ligi.gobandroid_hd.helper.SGFFileNameFilter
import java.io.File
import java.util.*
import java.util.Collections.sort

object NextTsumegoFileFinder {

    /**
     * try to find next tsumego based on filename
     * searching the last number and incrementing it

     * @param fileName
     * *
     * @return the filename found
     */
    fun calcNextTsumego(fileName: String): String? {

        val file = File(fileName)

        if (!file.exists()) {
            App.tracker.trackException("file given to calcNextTsumego is null", false)
            return null
        }

        val dir = file.parentFile

        if (dir == null || !dir.isDirectory) {
            App.tracker.trackException("file given to calcNextTsumego has no valid parent", false)
            return null
        }

        val fileNames = dir.list(SGFFileNameFilter())

        if (fileNames == null || fileNames.size == 0) {
            App.tracker.trackException("file given to calcNextTsumego has empty parent", false)
            return null
        }

        val fileList = Arrays.asList(*fileNames)

        sort(fileList)

        val inputFilePos = fileList.lastIndexOf(file.name)
        if (inputFilePos + 1 < fileList.size) {
            return dir.toString() + "/" + fileList[inputFilePos + 1]
        }
        return null
    }

}
