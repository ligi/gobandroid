package org.ligi.gobandroid_hd.ui.share

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.android.synthetic.main.share_options.view.*
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.ui.GoBoardView
import org.ligi.gobandroid_hd.ui.GobandroidDialog
import java.io.File
/**
 * Dialog with the intention to share the current Game

 * @author [Marcus -Ligi- Bueschleb](http://ligi.de)
 * *
 *
 *
 * *         License: This software is licensed with GPLv3
 */
class ShareSGFDialog(context: Context) : GobandroidDialog(context) {

    init {

        setContentView(R.layout.share_options)

        setTitle(R.string.share)
        setIconResource(R.drawable.ic_social_share)

        setNegativeButton(R.string.cancel)
        setPositiveButton(R.string.ok,  { _ ->
            when (container.shareTypeRadioGroup.checkedRadioButtonId) {
                R.id.radioButtonAsUnicode -> {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.putExtra(Intent.EXTRA_TEXT, gameProvider.get().visualBoard.toString(true))
                    intent.type = "text/plain"
                    getContext().startActivity(Intent.createChooser(intent, getContext().getString(R.string.choose_how_to_send_sgf)))
                }

                R.id.radioButtonAsAttachment -> ShareAsAttachmentDialog(getContext())

                R.id.radioButtonAsImage -> {
                    val file = File(settings.SGFBasePath, "game_to_share_via_action.png")

                    val goBoardView = GoBoardView(getContext())
                    val mutableImage = BitmapFactory.decodeResource(getContext().resources, R.drawable.shinkaya)
                            .copy(Bitmap.Config.ARGB_8888, true)

                    goBoardView.layout(0, 0, mutableImage.width, mutableImage.height)
                    goBoardView.screenshot(file, mutableImage)

                    val it = Intent(Intent.ACTION_SEND)
                    it.putExtra(Intent.EXTRA_SUBJECT, "Image created with gobandroid")
                    it.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file))
                    it.type = "image/*"
                    getContext().startActivity(Intent.createChooser(it, getContext().getString(R.string.choose_how_to_send_sgf)))
                }
            }

            dismiss()
        })

    }
}
