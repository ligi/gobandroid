package org.ligi.gobandroid_hd.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.AssetManager
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.ui.application.GobandroidSettings
import java.io.File
import java.io.FileWriter

object UnzipSGFsDialog {

    fun show(activity: Activity, intent_after_finish: Intent, settings: GobandroidSettings) {

        val dialog = ProgressDialog.show(activity, activity.getString(R.string.copy_sgf_dialog_message), "", true)

        Thread({
            val callback: (String) -> Unit = { activity.runOnUiThread { dialog.setMessage(it) } }
            decompress(activity.assets, arrayOf("sgf_init"), settings.sgfBasePath, callback)

            activity.runOnUiThread {
                dialog.dismiss()
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
            if (list.size > 0) {
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
