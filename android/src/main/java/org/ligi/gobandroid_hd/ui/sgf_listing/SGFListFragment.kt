package org.ligi.gobandroid_hd.ui.sgf_listing

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils.isEmpty
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.davekoelle.alphanum.AlphanumComparator
import org.ligi.gobandroid_hd.InteractionScope.Mode.TSUMEGO
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.helper.SGFFileNameFilter
import org.ligi.gobandroid_hd.logic.sgf.SGFReader
import org.ligi.gobandroid_hd.ui.GoLinkLoadActivity
import org.ligi.gobandroid_hd.ui.Refreshable
import org.ligi.gobandroid_hd.ui.SGFLoadActivity
import org.ligi.gobandroid_hd.ui.fragments.GobandroidFragment
import org.ligi.gobandroid_hd.ui.review.SGFMetaData
import org.ligi.gobandroid_hd.ui.sgf_listing.item_view_holder.PathViewHolder
import org.ligi.gobandroid_hd.ui.sgf_listing.item_view_holder.ReviewViewHolder
import org.ligi.gobandroid_hd.ui.sgf_listing.item_view_holder.TsumegoViewHolder
import org.ligi.gobandroid_hd.ui.sgf_listing.item_view_holder.ViewHolderInterface
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*

class SGFListFragment : GobandroidFragment(), Refreshable {

    private var menu_items: Array<String>? = null
    private var dir: String? = null

    private var actionMode: ActionMode? = null
    private var adapter: RecyclerView.Adapter<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getEnvFromSavedInstance()

        if (menu_items == null) { // we got nothing from savedInstance
            refresh()
        }

    }


    private fun getEnvFromSavedInstance() {
        if (menu_items == null) {
            menu_items = arguments?.getStringArray(EXTRA_MENU_ITEMS)
        }

        if (dir == null) {
            dir = arguments?.getString(EXTRA_DIR)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflate = View.inflate(context, R.layout.recycler_view, null)
        val recyclerView = inflate.findViewById(R.id.content_recycler) as RecyclerView

        val rows = resources.getInteger(R.integer.sgf_list_rows)

        recyclerView.layoutManager = StaggeredGridLayoutManager(rows, OrientationHelper.VERTICAL)
        adapter = SGFListAdapter()
        recyclerView.adapter = adapter
        return inflate
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArray(EXTRA_MENU_ITEMS, menu_items)
        outState.putString(EXTRA_DIR, dir)
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    override fun refresh() {
        val alert = AlertDialog.Builder(requireActivity()).setTitle(R.string.problem_listing_sgf)

        alert.setPositiveButton(R.string.ok, { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.dismiss()
            requireActivity().finish()
        })
        alert.setOnCancelListener({
            requireActivity().finish()
        })

        if (dir == null) {
            alert.setMessage(resources.getString(R.string.sgf_path_invalid) + " " + dir).show()
            return
        }

        val dir_file = File(dir!!)
        val files = File(dir!!).listFiles()

        if (files == null) {
            alert.setMessage(resources.getString(R.string.there_are_no_files_in) + " " + dir_file.absolutePath).show()
            return
        }

        val fileNames = ArrayList<String>()
        val directoryNames = ArrayList<String>()

        for (file in files) {
            if (file.isDirectory) {
                directoryNames.add(file.name)
            } else if (file.name.endsWith(".sgf") || file.name.endsWith(".golink")) {
                fileNames.add(file.name)
            }
        }

        if (fileNames.size + directoryNames.size == 0) {
            alert.setMessage(resources.getString(R.string.there_are_no_files_in) + " " + dir_file.absolutePath).show()
            return
        }

        val new_menu_items : Array<String> =
        if (interactionScope.mode === TSUMEGO) {

            if (fileNames.size > 1000) {
                try {
                    val list = dir_file.list(SGFFileNameFilter())
                    val game1 = SGFReader.sgf2game(File(dir_file, list[10]).bufferedReader().readText(), null, SGFReader.BREAKON_FIRSTMOVE)
                    val game2 = SGFReader.sgf2game(File(dir_file, list[12]).bufferedReader().readText(), null, SGFReader.BREAKON_FIRSTMOVE)
                    if (game1!=null && game2!=null && !isEmpty(game1.metaData.difficulty) && !isEmpty(game2.metaData.difficulty)) {
                        AlertDialog.Builder(requireActivity()).setMessage("This looks like the gogameguru offline selection - sort by difficulty")
                                .setPositiveButton(R.string.ok) { dialog, _ ->
                                    GoProblemsRenaming(requireActivity(), dir_file).execute()
                                    dialog.dismiss()
                                }
                                .setNegativeButton(R.string.cancel, null)

                                .show()
                    }
                } catch (e: IOException) {
                    Timber.w(e, "problem in gogameguru rename offer")
                }

                return
            }

            val done = ArrayList<String>()
            val undone = ArrayList<String>()
            for (fileName in fileNames)
                if (SGFMetaData(dir_file.absolutePath + "/" + fileName).isSolved) {
                    done.add(fileName)
                } else {
                    undone.add(fileName)
                }

            val undone_arr = undone.toTypedArray()
            val done_arr = done.toTypedArray()
            sortListing(undone_arr)
            sortListing(done_arr)
            undone_arr.plus(done_arr)
        } else {
            fileNames.toTypedArray().apply {
                sortListing(this)
            }
        }
        val dirs = directoryNames.toTypedArray()
        sortListing(dirs)

        menu_items = dirs.plus(new_menu_items)
        if (adapter != null) {
            adapter!!.notifyDataSetChanged()
        }
    }

    private fun sortListing(done_arr: Array<String>) {
        Arrays.sort(done_arr, AlphanumComparator())
    }


    fun delete_sgfmeta() {
        Timber.i("delete sgfmeta files")
        val alertBuilder = AlertDialog.Builder(requireActivity()).setTitle(R.string.del_sgfmeta)
        alertBuilder.setMessage(R.string.del_sgfmeta_prompt)

        if (dir == null) {
            alertBuilder.setMessage(resources.getString(R.string.sgf_path_invalid) + " " + dir).show()
            return
        }

        val dir_file = File(dir!!)
        val filesToDelete = File(dir!!).listFiles()

        if (filesToDelete == null) {
            alertBuilder.setMessage(resources.getString(R.string.there_are_no_files_in) + " " + dir_file.absolutePath).show()
            return
        }

        alertBuilder.setPositiveButton(R.string.ok) { dialog, _ ->
            // User clicked OK button
            dialog.dismiss()
            for (file in filesToDelete) {
                if (file.name.endsWith(SGFMetaData.FNAME_ENDING)) {
                    file.delete()
                }
            }
            refresh()
        }

        alertBuilder.setNegativeButton(R.string.cancel, null)

        alertBuilder.create().show()
    }

    private inner class SGFListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private val TYPE_PATH = 0
        private val TYPE_TSUMEGO = 1
        private val TYPE_GOLINK = 2
        private val TYPE_REVIEW = 3

        override fun getItemViewType(position: Int): Int {

            if (getFile(position).isDirectory) {
                return TYPE_PATH
            }

            if (GoLink.isGoLink(getFile(position))) {
                return TYPE_GOLINK
            }

            if (interactionScope.mode === TSUMEGO) {
                return TYPE_TSUMEGO
            }

            return TYPE_REVIEW
        }

        private fun getFile(position: Int): File {
            val fileName = dir + "/" + menu_items!![position]
            return File(fileName)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflator = LayoutInflater.from(parent.context)

            when (viewType) {
                TYPE_PATH -> return PathViewHolder(inflator.inflate(R.layout.sgf_dir_list_item, parent, false))
                TYPE_TSUMEGO -> return TsumegoViewHolder(inflator.inflate(R.layout.sgf_tsumego_list_item, parent, false))

                TYPE_GOLINK, TYPE_REVIEW -> return ReviewViewHolder(inflator.inflate(R.layout.sgf_review_game_details_list_item, parent, false))

                else -> throw IllegalStateException("unknown view-type " + viewType)
            }
        }


        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as ViewHolderInterface).apply(getFile(position))


            val cardView = holder.itemView as CardView

            cardView.setOnTouchListener(View.OnTouchListener { v, event ->
                if (v.getTag(R.id.tag_actionmode) != null) {
                    return@OnTouchListener false
                }
                when (event.action) {
                    MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> cardView.cardElevation = resources.getDimension(R.dimen.cardview_default_elevation)

                    MotionEvent.ACTION_DOWN -> cardView.cardElevation = resources.getDimension(R.dimen.cardview_unelevated_elevation)
                }
                false
            })


            cardView.setOnClickListener {
                val intent2start = Intent(activity, SGFLoadActivity::class.java)
                val fileName = dir + "/" + menu_items!![holder.adapterPosition]

                // check if it is directory behind golink or general
                if (GoLink.isGoLink(fileName)) {
                    intent2start.setClass(requireContext(), GoLinkLoadActivity::class.java)
                } else if (!fileName.endsWith(".sgf")) {
                    intent2start.setClass(requireContext(), SGFFileSystemListActivity::class.java)
                }

                intent2start.data = Uri.parse(fileName)

                if (actionMode != null) {
                    actionMode!!.finish()
                }

                startActivity(intent2start)
            }

            cardView.setOnLongClickListener(object : View.OnLongClickListener {
                override fun onLongClick(v: View): Boolean {

                    if (activity !is AppCompatActivity) {
                        Timber.w("Activity not instanceof AppCompatActivity - this is not really expected")
                        return false
                    }

                    v.setTag(R.id.tag_actionmode, java.lang.Boolean.TRUE)

                    val activity = activity as AppCompatActivity
                    actionMode = activity.startSupportActionMode(getActionMode(holder.adapterPosition))

                    cardView.cardElevation = resources.getDimension(R.dimen.cardview_elevated_elevation)

                    return true
                }

                private fun getActionMode(position: Int): SGFListActionMode {
                    val fileName = dir + "/" + menu_items!![position]
                    val file = File(fileName)
                    var menuResource = R.menu.list_file_sgf_action_mode

                    if (file.isDirectory) {
                        menuResource = R.menu.list_dir_sgf_action_mode
                    }

                    return object : SGFListActionMode(this@SGFListFragment.requireActivity(), fileName, this@SGFListFragment, menuResource) {
                        override fun onDestroyActionMode(mode: ActionMode) {
                            actionMode = null
                            cardView.cardElevation = resources.getDimension(R.dimen.cardview_default_elevation)
                            cardView.setTag(R.id.tag_actionmode, null)
                            super.onDestroyActionMode(mode)
                        }
                    }
                }
            })
        }

        override fun getItemCount(): Int {
            if (menu_items == null) {
                return 0
            }
            return menu_items!!.size
        }


    }

    companion object {

        val EXTRA_DIR = "dir"
        val EXTRA_MENU_ITEMS = "menu_items"

        fun newInstance(dir: File): SGFListFragment {
            val f = SGFListFragment()

            val args = Bundle()
            args.putString(EXTRA_DIR, dir.absolutePath)
            f.arguments = args

            return f
        }
    }
}