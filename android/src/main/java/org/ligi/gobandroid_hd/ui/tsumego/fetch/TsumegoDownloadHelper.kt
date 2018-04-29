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

        fun getFileNameByPos(pos: Int): String {
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
        return doDownload(app, getDefaultList(App.env), {})
    }

    fun doDownload(ctx: Context, params: Array<TsumegoSource>, callback: (current: String) -> Unit): Int {

        val limit = GobandroidBackend.getMaxTsumegos(ctx)

        if (limit == -1) {
            return 0
        }

        var download_count = 0

        for (src in params) {

            var pos = 10

            while (File(src.local_path, src.getFileNameByPos(pos)).exists()) {
                pos++
            }

            val client = OkHttpClient()

            while (pos < limit) {

                try {
                    val fileNameByPos = src.getFileNameByPos(pos)

                    val build = Request.Builder().url(src.remote_path + fileNameByPos).build()

                    val responseBody = client.newCall(build).execute().body()
                    val downloadedFile = File(src.local_path, fileNameByPos)

                    responseBody?.use { body ->
                        Okio.buffer(Okio.sink(downloadedFile))?.use { sink ->
                            sink.writeAll(body.source())
                        }
                    }

                    pos++
                    download_count++
                    callback(fileNameByPos)

                } catch (e: Exception) {
                    e.printStackTrace()
                    return download_count
                }

            }
        }
        return download_count
    }
}
