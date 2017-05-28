package org.ligi.gobandroid_hd.ui.recording

import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.dialog_save_sgf.*
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.logic.sgf.SGFWriter
import org.ligi.gobandroid_hd.ui.GobandroidDialog
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity
import org.ligi.kaxt.doAfterEdit
import org.ligi.kaxt.setVisibility
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

        setPositiveButton(android.R.string.ok, { _ ->
            val file = completeFileName()
            val res = SGFWriter.saveSGF(gameProvider.get(), file!!)

            val toastText = if (res) R.string.file_saved else R.string.file_not_saved
            Toast.makeText(context, String.format(context.getString(toastText), file.absolutePath), Toast.LENGTH_SHORT).show()
        })

        sgf_name_edittext.doAfterEdit {
            setPositiveButtonAndOverrideCheckboxEnabledByExistenceOfFile()
        }

        override_checkbox.setOnCheckedChangeListener { _, _ -> setPositiveButtonAndOverrideCheckboxEnabledByExistenceOfFile() }

        // get the old filename from the metadata
        val oldFileName = gameProvider.get().metaData.fileName

        if (oldFileName.isNotBlank()) {
            var suggested_name = oldFileName.replace(".sgf", "")
            val absolutePath = settings.SGFSavePath.absolutePath
            if (suggested_name.startsWith(absolutePath)) {
                suggested_name = suggested_name.substring(absolutePath.length + 1)
            }
            sgf_name_edittext.setText(suggested_name)
        }

        val (name, _, _, _, blackName, _, whiteName) = gameProvider.get().metaData

        /**
         * this is a OnClickListener to add Stuff to the FileName like
         * date/gamename/...
         */
        class FileNameAdder : View.OnClickListener {

            private fun getTextByButtonId(btn_resId: Int) = when (btn_resId) {
                R.id.button_add_date -> {
                    val date_formatter = SimpleDateFormat("yyyy.MM.dd")
                    date_formatter.format(Date())
                }
                R.id.button_add_time -> {
                    val time_formatter = SimpleDateFormat("H'h'm'm'")
                    time_formatter.format(Date())
                }
                R.id.button_add_gamename -> name
                R.id.button_add_players -> blackName + "_vs_" + whiteName

                else -> null
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

        button_add_gamename.prepareButton(name.isBlank(), adder)
        button_add_players.prepareButton(blackName.isBlank() && whiteName.isBlank(), adder)

        setTitle(R.string.save_sgf)

        setPositiveButtonAndOverrideCheckboxEnabledByExistenceOfFile()
    }

    fun Button.prepareButton(condition: Boolean, adder: View.OnClickListener) = this.apply {
        setVisibility(condition)
        setOnClickListener(adder)
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
     * @return the filename with path and file extension
     * or null when there is no filename given
     */
    private fun completeFileName(): File? {
        var fileName = sgf_name_edittext.text.toString()

        if (fileName.isEmpty())
            return null

        fileName += ".sgf"

        return if (fileName.startsWith("/"))
            File(fileName)
        else
            File(settings.SGFSavePath, fileName)
    }

}
