package org.ligi.gobandroid_hd.ui.sgf_listing

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import org.ligi.gobandroid_hd.helper.SGFFileNameFilter
import org.ligi.gobandroid_hd.logic.sgf.SGFReader
import org.ligi.tracedroid.logging.Log
import java.io.File

class GoProblemsRenaming(private val context: Context, private val dir: File) : AsyncTask<Void, Int, Void>() {
    private var list: Array<String>? = null

    private var progressDialog: ProgressDialog? = null

    override fun onPreExecute() {
        super.onPreExecute()

        list = dir.list(SGFFileNameFilter())

        progressDialog = ProgressDialog(context)

        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog!!.isIndeterminate = false
        progressDialog!!.max = list!!.size
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    override fun doInBackground(vararg params: Void): Void? {

        var i = 0

        for (filename in list!!) {

            val gameFile = File(dir, filename)
            try {
                val game = SGFReader.sgf2game(gameFile.bufferedReader().readText(), null, SGFReader.BREAKON_FIRSTMOVE)

                if (game == null) {
                    // some files inside goproblems are broken move them in a special directory
                    val brokenPath = File(dir, "broken")
                    brokenPath.mkdirs()
                    gameFile.renameTo(File(brokenPath, gameFile.name))
                } else {
                    val levelPath = File(dir, game.metaData.difficulty)
                    levelPath.mkdirs()
                    gameFile.renameTo(File(levelPath, gameFile.name))
                }
            } catch (e: Exception) {
                Log.w("problem in the process of GoProblemsRenaming", e)
            }

            publishProgress(i++)
        }
        return null
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
        progressDialog!!.progress = values[0]!!
    }

    override fun onPostExecute(aVoid: Void) {
        progressDialog!!.dismiss()
        val intent = Intent(context, SGFFileSystemListActivity::class.java)
        intent.data = Uri.parse("file://" + dir.toString())
        context.startActivity(intent)
        super.onPostExecute(aVoid)
    }
}
