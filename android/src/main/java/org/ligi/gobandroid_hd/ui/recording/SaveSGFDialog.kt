package org.ligi.gobandroid_hd.ui.recording

import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.dialog_save_sgf.*
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.logic.sgf.SGFWriter
import org.ligi.gobandroid_hd.ui.GobandroidDialog
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity
import org.ligi.kaxt.doAfterEdit
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Dialog to save a game to SGF file and ask the user about how in here
 *
 *
 * TODO check if file exists

 * @author ligi
 */
class SaveSGFDialog(private val context: GobandroidFragmentActivity) : GobandroidDialog(context) {

    init {

        setContentView(R.layout.dialog_save_sgf)

        setIconResource(R.drawable.ic_content_save)

        intro_txt.text = String.format(context.resources.getString(R.string.save_sgf_question), settings.SGFSavePath)

        setPositiveButton(android.R.string.ok, { dialog ->
            val file = completeFileName()
            val res = SGFWriter.saveSGF(gameProvider.get(), file!!)

            if (res)
                Toast.makeText(context, String.format(context.getString(R.string.file_saved), file.absolutePath), Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(context, String.format(context.getString(R.string.file_not_saved), file.absolutePath), Toast.LENGTH_SHORT).show()

            dialog.dismiss()
        })

        sgf_name_edittext.doAfterEdit {
            setPositiveButtonAndOverrideCheckboxEnabledByExistenceOfFile()
        }

        override_checkbox.setOnCheckedChangeListener { buttonView, isChecked -> setPositiveButtonAndOverrideCheckboxEnabledByExistenceOfFile() }

        // get the old filename from the metadata
        val oldFileName = gameProvider.get().metaData.fileName

        if (oldFileName != "") {
            var suggested_name = oldFileName.replace(".sgf", "")
            val absolutePath = settings.SGFSavePath.absolutePath
            if (suggested_name.startsWith(absolutePath)) {
                suggested_name = suggested_name.substring(absolutePath.length)
            }
            sgf_name_edittext.setText(suggested_name)
        }

        val (name, _, _, _, blackName, _, whiteName) = gameProvider.get().metaData

        /**
         * this is a OnClickListener to add Stuff to the FileName like
         * date/gamename/...
         */
        class FileNameAdder : View.OnClickListener {

            private fun getTextByButtonId(btn_resId: Int): String? {
                when (btn_resId) {
                    R.id.button_add_date -> {
                        val date_formatter = SimpleDateFormat("yyyy.MM.dd")
                        return date_formatter.format(Date())
                    }
                    R.id.button_add_time -> {
                        val time_formatter = SimpleDateFormat("H'h'm'm'")
                        return time_formatter.format(Date())
                    }
                    R.id.button_add_gamename -> return name
                    R.id.button_add_players -> return blackName + "_vs_" + whiteName

                    else -> return null
                }
            }

            override fun onClick(v: View) {
                val toAdd = getTextByButtonId(v.id)
                if (toAdd != null) {
                    val text = sgf_name_edittext.text.toString()
                    val cursorPos = sgf_name_edittext.selectionStart
                    val sb = StringBuilder()
                    sb.append(text.substring(0, cursorPos)).append(toAdd).append(text.substring(cursorPos, sgf_name_edittext.length()))
                    sgf_name_edittext.setText(sb.toString())
                    sgf_name_edittext.setSelection(cursorPos + toAdd.length)
                }
            }

        }

        val adder = FileNameAdder()

        button_add_date.setOnClickListener(adder)
        button_add_time.setOnClickListener(adder)

        if (name.isBlank())
            button_add_gamename.visibility = View.GONE
        else
            button_add_gamename.setOnClickListener(adder)

        if (blackName.isBlank() && whiteName.isBlank())
            button_add_players.visibility = View.GONE
        else
            button_add_players.setOnClickListener(adder)

        setTitle(R.string.save_sgf)

        setPositiveButtonAndOverrideCheckboxEnabledByExistenceOfFile()

    }

    private fun setPositiveButtonAndOverrideCheckboxEnabledByExistenceOfFile() {

        val wanted_file = completeFileName()

        if (wanted_file == null) { // we got no filename from user

            override_checkbox.visibility = View.GONE // no overwrite without
            // filename

            positive_btn.isEnabled = false // should not save without a
            // filename
            return
        }

        val target_file_exist = wanted_file.exists()
        override_checkbox.visibility = if (target_file_exist && !wanted_file.isDirectory) View.VISIBLE else View.GONE
        positive_btn.isEnabled = !target_file_exist || override_checkbox.isChecked
    }

    /**
     * @return the filename with path and file extension - returns null when
     * * there is no filename given
     */
    private // append filename extension
    fun completeFileName(): File? {
        var fname = sgf_name_edittext.text.toString()

        if (fname.isEmpty())
            return null

        fname += ".sgf"

        return if (fname.startsWith("/"))
            File(fname)
        else
            File(settings.SGFSavePath, fname)
    }

}
