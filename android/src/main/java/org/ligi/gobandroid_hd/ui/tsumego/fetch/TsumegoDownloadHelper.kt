package org.ligi.gobandroid_hd.ui.tsumego.fetch

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Okio
import org.ligi.gobandroid_hd.App
import org.ligi.gobandroid_hd.backend.GobandroidBackend
import org.ligi.gobandroid_hd.ui.application.GoAndroidEnvironment
import java.io.File

object TsumegoDownloadHelper {
    private val BASE_URL = "https://raw.githubusercontent.com/gogameguru/go-problems/master/weekly-go-problems/"

    class TsumegoSource(var local_path: String, var remote_path: String, var fname: String) {

        fun getFnameByPos(pos: Int): String {
            return String.format(fname, pos)
        }
    }

    fun getDefaultList(settings: GoAndroidEnvironment): Array<TsumegoSource> {
        return arrayOf(
                TsumegoSource("${settings.tsumegoPath}/1.easy/", BASE_URL + "/easy/", "/ggg-easy-%02d.sgf"),
                TsumegoSource("${settings.tsumegoPath}/2.intermediate/", BASE_URL + "/intermediate/", "/ggg-intermediate-%02d.sgf"),
                TsumegoSource("${settings.tsumegoPath}/3.hard/", BASE_URL + "/hard/", "/ggg-hard-%02d.sgf")
        )
    }

    fun doDownloadDefault(app: App): Int {
        return doDownload(app, getDefaultList(App.component().settings()), {})
    }

    fun doDownload(ctx: Context, params: Array<TsumegoSource>, callback: (current: String) -> Unit): Int {
        var download_count = 0

        val limit = GobandroidBackend.getMaxTsumegos(ctx)

        if (limit != -1)
            for (src in params) {

                var pos = 10

                while (File(src.local_path + src.getFnameByPos(pos)).exists()) {
                    pos++
                }

                val client = OkHttpClient()

                while (pos < limit) {

                    try {
                        val fnameByPos = src.getFnameByPos(pos)

                        val build = Request.Builder().url(src.remote_path + fnameByPos).build()

                        val response = client.newCall(build).execute()
                        val downloadedFile = File(src.local_path, fnameByPos)

                        val sink = Okio.buffer(Okio.sink(downloadedFile))
                        sink.writeAll(response.body().source())
                        sink.close()

                        response.body().close()

                        download_count++
                        callback(fnameByPos)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        return download_count
                    }

                }
            }
        return download_count
    }
}
