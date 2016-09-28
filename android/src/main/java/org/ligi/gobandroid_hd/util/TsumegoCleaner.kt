package org.ligi.gobandroid_hd.util

import org.ligi.gobandroid_hd.ui.GoPrefs
import org.ligi.gobandroid_hd.ui.application.GoAndroidEnvironment
import java.io.InputStream


class TsumegoCleaner(val env: GoAndroidEnvironment) {

    fun clean() {
        if (GoPrefs.isTsumegoCleanDone) {
            return // only once
        }
        GoPrefs.isTsumegoCleanDone = true

        Thread({
            env.tsumegoPath.walk().filter { !it.isDirectory }.forEach {
                if (is404File(it.inputStream())) {
                    it.delete()
                }
            }
        }).start()
    }

    private fun is404File(ins: InputStream): Boolean {
        "404: Not Found".forEach {
            if (ins.read().toChar() != it) {
                return false
            }
        }
        return true
    }
}
