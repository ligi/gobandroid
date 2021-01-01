package org.ligi.gobandroid_hd.ui.application

import android.content.Context
import android.os.Environment
import java.io.File

class GoAndroidEnvironment(private val ctx: Context) {

    companion object {
        val settingsXMLName = "env"
    }

    // workaround for Samsung tablet with internal and external SD-card
    val SGFBasePath: File
        get() {
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                return File(ctx.getExternalFilesDir(null), "/gobandroid/sgf/")
            }
            val probe = File("/sdcard/Android")
            if (probe.exists() && probe.isDirectory) {
                return File("/sdcard/gobandroid/sgf/")
            }
            return File(ctx.filesDir, "/sgf/")
        }

    val tsumegoPath: File by lazy { File(SGFBasePath, "tsumego") }
    val bookmarkPath: File by lazy { File(reviewPath, "bookmarks") }
    val reviewPath: File by lazy { File(SGFBasePath, "review") }
    val SGFSavePath: File by lazy { File(SGFBasePath, "review/saved") }
}