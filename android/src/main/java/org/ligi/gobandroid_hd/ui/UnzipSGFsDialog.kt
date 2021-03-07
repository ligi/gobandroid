package org.ligi.gobandroid_hd.ui

import android.app.Activity
import android.content.Intent
import android.content.res.AssetManager
import org.ligi.gobandroid_hd.ui.alerts.ProgressDialog
import org.ligi.gobandroid_hd.ui.application.GoAndroidEnvironment
import java.io.File
import java.io.FileWriter

class UnzipSGFsDialog(val activity: Activity, val intent_after_finish: Intent, settings: GoAndroidEnvironment) : ProgressDialog(activity) {


    init {
        setTitle("Copying")
        progress.isIndeterminate = true

        Thread({
            val callback: (String) -> Unit = { activity.runOnUiThread { message.text = it } }
            decompress(activity.assets, arrayOf("sgf_init"), settings.SGFBasePath, callback)

            activity.runOnUiThread {
                dismiss()
                activity.startActivity(intent_after_finish)
            }
        }).start()

    }

    private fun decompress(assets: AssetManager, fromAssetDir: Array<String>, toDirConst: File, callback: (current: String) -> Unit) {
        fromAssetDir.forEach { current ->
            val cleanCurrent = current.substringAfter("/")
            callback(cleanCurrent.substringAfterLast("/"))
            val list = assets.list(current)
            val toDir = File(toDirConst, cleanCurrent)
            if (list!!.size > 0) {
                toDir.mkdirs()
                val relativePathList = list.map { child -> "$current/$child" }.toTypedArray()
                decompress(assets, relativePathList, toDirConst, callback)
            } else {
                val asset = assets.open(current)
                val reader = asset.reader()
                val out = FileWriter(toDir)
                reader.copyTo(out)
                reader.close()
                asset.close()
                out.close()
            }
        }
    }


}
