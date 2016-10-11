package org.ligi.gobandroid_hd.print

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.pdf.PrintedPdfDocument
import org.ligi.gobandroid_hd.ui.GoBoardView
import java.io.FileOutputStream
import java.io.IOException

@TargetApi(Build.VERSION_CODES.KITKAT)
class GoGamePrintDocumentAdapter(private val context: Context, private val jobName: String) : PrintDocumentAdapter() {

    private var mPdfDocument: PrintedPdfDocument? = null

    override fun onLayout(oldAttributes: PrintAttributes,
                          newAttributes: PrintAttributes,
                          cancellationSignal: CancellationSignal,
                          layoutResultCallback: LayoutResultCallback,
                          bundle: Bundle) {

        if (cancellationSignal.isCanceled) {
            layoutResultCallback.onLayoutCancelled()
            return
        }

        mPdfDocument = PrintedPdfDocument(context, newAttributes)

        val info = PrintDocumentInfo.Builder(jobName).setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).setPageCount(1).build()
        layoutResultCallback.onLayoutFinished(info, true)

    }

    override fun onWrite(pageRanges: Array<PageRange>,
                         destination: ParcelFileDescriptor,
                         cancellationSignal: CancellationSignal,
                         callback: WriteResultCallback) {

        val page = mPdfDocument!!.startPage(0)
        val canvas = page.canvas

        val goBoardViewHD = GoBoardView(context)
        goBoardViewHD.setSize(canvas.width, canvas.height)
        goBoardViewHD.draw(canvas)


        mPdfDocument!!.finishPage(page)

        try {
            mPdfDocument!!.writeTo(FileOutputStream(destination.fileDescriptor))
        } catch (e: IOException) {
            callback.onWriteFailed(e.toString())
            return
        } finally {
            mPdfDocument!!.close()
            mPdfDocument = null
        }

        callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
    }

}
