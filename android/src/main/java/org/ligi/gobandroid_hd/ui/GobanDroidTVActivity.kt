package org.ligi.gobandroid_hd.ui

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import org.ligi.gobandroid_hd.App
import org.ligi.gobandroid_hd.InteractionScope
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import java.io.File

/**
 * Activity to replay GO Games in TV / Lean back style
 */
@RuntimePermissions
open class GobanDroidTVActivity : GobandroidFragmentActivity() {

    private val path_to_play_from: File by lazy { File(env.reviewPath, "commented") }

    open val intent2start: Intent
        get() = Intent(this, GobanDroidTVActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        interactionScope.mode = InteractionScope.Mode.TELEVIZE

        supportActionBar!!.setLogo(R.drawable.gobandroid_tv)

        App.tracker.init(this)

        checkStorageWithPermissionCheck()
    }

    @NeedsPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun checkStorage(){
        if (path_to_play_from.listFiles() == null) {
            setContentView(R.layout.empty)
            App.tracker.trackEvent("intern", "unzip", "gtv", null)
            UnzipSGFsDialog(this, intent2start, env).show()
        } else {
            startTV()
        }
    }
    @OnPermissionDenied(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun onStorageDenied() {
        finish()
    }

    private fun startTV() {

        val start_review_intent = Intent(this, SGFLoadActivity::class.java)

        val avail_file_list = path_to_play_from.walk().filter { it.name.endsWith(".sgf") }.toList()

        if (avail_file_list.isEmpty()) {
            setContentView(R.layout.empty)
            AlertDialog.Builder(this)
                    .setMessage(getString(R.string.there_are_no_files_in) + " " + path_to_play_from)
                    .setTitle(R.string.problem)
                    .setPositiveButton(R.string.ok, DialogInterface.OnClickListener { _, _ ->
                        this@GobanDroidTVActivity.finish()
                    }).show()
        } else {

            val chosen = avail_file_list[(Math.random() * avail_file_list.size).toInt()]

            App.tracker.trackEvent("gtv", "start_play_file", chosen.absolutePath, null)

            start_review_intent.data = Uri.parse("file://" + chosen)

            startActivity(start_review_intent)

            finish()
        }
    }

    override fun onNewIntent(intent: Intent) {
        startTV()
        super.onNewIntent(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated function
        onRequestPermissionsResult(requestCode, grantResults)
    }
}
