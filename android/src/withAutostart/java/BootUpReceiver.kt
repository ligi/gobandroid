package org.ligi.gobandroid_hd

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.ligi.gobandroid_hd.ui.GobanDroidTVActivity

class BootUpReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val i = Intent(context, GobanDroidTVActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(i)
    }
}