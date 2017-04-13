package org.ligi.gobandroid_hd.ui.tsumego.fetch

import android.app.Activity
import android.view.View
import org.ligi.gobandroid_hd.App
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.ui.Refreshable
import org.ligi.gobandroid_hd.ui.alerts.ProgressDialog

class DownloadProblemsDialog(context: Activity, refreshable: Refreshable?) : ProgressDialog(context) {

    init {
        App.tracker.trackEvent("ui_action", "tsumego", "refresh", null);

        setIconResource(R.drawable.ic_navigation_refresh)
        setTitle(R.string.please_stay_patient)
        progress.isIndeterminate = true
        message.setText(R.string.downloading_tsumegos_please_wait)

        Thread(Runnable {
            val initList = TsumegoDownloadHelper.getDefaultList(App.env)
            val result = TsumegoDownloadHelper.doDownload(context, initList, {
                context.runOnUiThread {
                    message.text = it
                }
            })

            context.runOnUiThread {

                var msg = context.getString(R.string.no_new_tsumegos_found)

                if (result > 0) {
                    msg = context.getString(R.string.downloaded_n_tsumego, result)
                    refreshable?.refresh()
                }

                setPositiveButton(android.R.string.ok)
                message.text = msg
                progress.visibility = View.GONE
            }
        }).start()
    }

}
