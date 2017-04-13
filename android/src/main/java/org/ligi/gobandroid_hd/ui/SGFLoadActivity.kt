/**
 * gobandroid
 * by Marcus -Ligi- Bueschleb
 * http://ligi.de
 *
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation;
 *
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http:></http:>//www.gnu.org/licenses/>.
 */

package org.ligi.gobandroid_hd.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.support.v7.app.AlertDialog
import android.widget.LinearLayout
import org.greenrobot.eventbus.EventBus
import org.ligi.gobandroid_hd.App
import org.ligi.gobandroid_hd.BuildConfig
import org.ligi.gobandroid_hd.FileEncodeDetector
import org.ligi.gobandroid_hd.InteractionScope.Mode.TSUMEGO
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.events.GameChangedEvent
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.logic.sgf.SGFReader
import org.ligi.gobandroid_hd.ui.alerts.GameLoadingDialog
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity
import org.ligi.gobandroid_hd.ui.ingame_common.SwitchModeHelper
import org.ligi.gobandroid_hd.ui.tsumego.TsumegoHelper
import org.ligi.kaxt.startActivityFromClass
import org.ligi.tracedroid.logging.Log
import java.io.*
import java.net.MalformedURLException
import java.net.URL

/**
 * Activity to load a SGF with a ProgressDialog showing the Progress

 * @author [Marcus -Ligi- Bueschleb](http://ligi.de)
 * *
 *
 *
 * *         License: This software is licensed with GPLv3
 */

class SGFLoadActivity : GobandroidFragmentActivity(), Runnable, SGFReader.ISGFLoadProgressCallback {

    private var act_progress: Int = 0
    private var max_progress: Int = 0
    private var act_message: String? = null

    private val dlg by lazy { GameLoadingDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(LinearLayout(this))

        dlg.show()

        App.tracker.trackEvent("ui_action", "load_gf", intent.data.toString(), null)
        Thread(this).start()
    }


    /**
     * get the content-string from a uri

     * @param intent_uri
     * *
     * @return
     * *
     * @throws FileNotFoundException
     * *
     * @throws MalformedURLException
     * *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun uri2string(intent_uri: Uri): String {

        if (intent_uri.toString().startsWith("/")) {
            return File(intent_uri.toString()).bufferedReader(FileEncodeDetector.detect(intent_uri.toString())).readText()
        }

        val uri_str = intent_uri.toString()

        val inputStream = if (uri_str.startsWith("content://")) {
            contentResolver.openInputStream(intent_uri)
        } else {
            BufferedInputStream(URL(intent_uri.toString()).openStream(), 4096)
        }

        val buf = ByteArrayOutputStream()

        inputStream.buffered().copyTo(buf)

        val stream_det = ByteArrayInputStream(buf.toByteArray())
        val charset = FileEncodeDetector.detect(stream_det)

        // if it comes from network
        if (intent_uri.toString().startsWith("http")) { // https is included
            File(env.SGFBasePath, "downloads").mkdirs()
            val f = File(env.SGFBasePath, "downloads/" + intent_uri.lastPathSegment)
            f.createNewFile()
            val file_writer = FileOutputStream(f)
            stream_det.reset()
            stream_det.buffered().copyTo(file_writer)
        }

        stream_det.reset()

        return stream_det.bufferedReader(charset).readText()
    }

    override fun run() {
        Looper.prepare()

        var intent_uri = intent.data // extract the uri from
        // the intent

        if (intent_uri == null) {
            Log.e("SGFLoadActivity with intent_uri==null")
            finish()
            return
        }

        if (intent_uri.toString().endsWith(".golink")) {
            startActivityFromClass(GoLinkLoadActivity::class.java)
            finish()
            return
        }

        if (intent_uri.toString().startsWith("tsumego")) {
            intent_uri = Uri.parse(intent_uri.toString().replaceFirst("tsumego".toRegex(), "file"))
            interactionScope.mode = TSUMEGO
        }

        var game: GoGame? = null
        var sgf: String? = null

        try {
            sgf = uri2string(intent_uri)

            game = SGFReader.sgf2game(sgf, this)

            // if it is a tsumego and we need a transformation to right corner
            // -> do so
            if (interactionScope.mode === TSUMEGO) {
                val transform = TsumegoHelper.calcTransform(game!!)

                if (transform != SGFReader.DEFAULT_SGF_TRANSFORM) {
                    game = SGFReader.sgf2game(sgf, null, SGFReader.BREAKON_NOTHING, transform)
                }
            }


        } catch (e: Exception) {
            Log.w("exception in load", e)
        }

        if (game == null) {
            runOnUiThread {
                /** if the sgf loading fails - give the user the option to send this SGF to me - to perhaps fix the
                 * parser to load more SGF's - TODO remove this block if all SGF's load fine ;-)  */

                dlg.hide()
                AlertDialog.Builder(this@SGFLoadActivity)
                        .setTitle(R.string.results)
                        .setMessage(
                                R.string.problem_loading_sgf_would_you_like_to_send_ligi_this_sgf_to_fix_the_problem)
                        .setPositiveButton(R.string.yes
                        ) { dialog, whichButton ->
                            val emailIntent = Intent(
                                    android.content.Intent.ACTION_SEND)
                            emailIntent.type = "plain/text"
                            emailIntent
                                    .putExtra(
                                            android.content.Intent.EXTRA_EMAIL,
                                            arrayOf("ligi@ligi.de"))
                            emailIntent
                                    .putExtra(
                                            android.content.Intent.EXTRA_SUBJECT,
                                            "SGF Problem " + BuildConfig.VERSION_NAME
                                    )
                            emailIntent
                                    .putExtra(
                                            android.content.Intent.EXTRA_TEXT,
                                            "uri: "
                                                    + intent_uri
                                                    + " sgf:\n"
                                                    + sgf
                                                    + "err:"
                                                    + Log.getCachedLog()
                                    )
                            this@SGFLoadActivity.startActivity(Intent
                                    .createChooser(emailIntent,
                                            "Send mail..."))
                            finish()
                        }
                        .setNegativeButton(R.string.no
                        ) { dialog, whichButton -> finish() }.show()
            }

            return
        }

        val move_num = intent.getIntExtra(INTENT_EXTRA_MOVE_NUM, -1)

        if (move_num != -1) {
            for (i in 0..move_num - 1) {
                game.jump(game.actMove.getnextMove(0))
            }
        }
        gameProvider.set(game)

        game.metaData.fileName = Uri.decode(intent_uri!!.toString())

        EventBus.getDefault().post(GameChangedEvent)

        runOnUiThread {
            dlg.hide()
            finish()
            SwitchModeHelper.startGameWithCorrectMode(this)
        }
    }

    override fun progress(act: Int, max: Int, progress_val: Int) {
        act_progress = act
        max_progress = max
        act_message = resources.getString(R.string.move) + " " + progress_val

        runOnUiThread {
            dlg.progress.progress = act_progress
            dlg.progress.max = max_progress
            dlg.message.text = act_message
        }
    }

    companion object {
        val INTENT_EXTRA_MOVE_NUM = "move_num"
    }
}