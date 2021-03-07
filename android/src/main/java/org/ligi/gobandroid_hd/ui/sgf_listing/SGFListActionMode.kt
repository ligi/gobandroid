package org.ligi.gobandroid_hd.ui.sgf_listing


import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.ui.Refreshable
import org.ligi.gobandroid_hd.ui.share.ShareAsAttachmentDialog
import java.io.File

open class SGFListActionMode(internal val context: Context, internal val fileName: String, internal val refreshable: Refreshable, internal val menuResource: Int) : ActionMode.Callback {

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        val inflater = mode.menuInflater
        inflater.inflate(menuResource, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        mode.finish()
        when (item.itemId) {
            R.id.menu_share -> {
                ShareAsAttachmentDialog(context, File(fileName)).show()
                return true
            }

            R.id.menu_delete -> {
                AlertDialog.Builder(context).setMessage("Really delete " +fileName).setTitle("Delete?").setNegativeButton("NO", null).setPositiveButton("YES", fileOrDirRemovingOnClickListener).show()
                return true
            }

            else -> return false
        }
    }

    private val fileOrDirRemovingOnClickListener: DialogInterface.OnClickListener
        get() = DialogInterface.OnClickListener { _, which ->
            val file = File(fileName)
            if (file.isDirectory) {
                file.deleteRecursively()
            } else {
                file.delete()
            }
            refreshable.refresh()
        }

    override fun onDestroyActionMode(mode: ActionMode) {
    }

}
