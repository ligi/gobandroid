package org.ligi.gobandroid_hd.ui

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import org.ligi.gobandroid_hd.App
import org.ligi.gobandroid_hd.InteractionScope
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.model.GameProvider
import org.ligi.gobandroid_hd.ui.application.GoAndroidEnvironment

/**
 * A styled Dialog fit in the gobandroid style
 */
open class GobandroidDialog(context: Context) : Dialog(context, R.style.dialog_theme) {

    private val inflater: LayoutInflater
    private val button_container by lazy { findViewById(R.id.button_container) as LinearLayout }
    val container by lazy { findViewById(R.id.dialog_content) as LinearLayout }

    val positive_btn: Button by lazy {
        createButton().apply {
            button_container.addView(this)
        }
    }


    val negative_btn: Button by lazy {
        createButton().apply {
            button_container.addView(this)
        }
    }

    val settings: GoAndroidEnvironment by App.kodein.lazy.instance()
    val gameProvider: GameProvider by App.kodein.lazy.instance()
    val interactionScope: InteractionScope by App.kodein.lazy.instance()

    init {
        inflater = LayoutInflater.from(context)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.setContentView(R.layout.dialog_gobandroid)

        // this sounds misleading but behaves right - we just do not want to  start with keyboard open
        window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

    }

    fun setIconResource(@DrawableRes icon: Int) {
        (findViewById(R.id.dialog_title) as TextView).setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
    }

    override fun setContentView(@LayoutRes content: Int) {
        container.addView(inflater.inflate(content, container, false))
    }


    override fun setTitle(title: CharSequence?) {
        (this.findViewById(R.id.dialog_title) as TextView).text = title
    }

    fun setPositiveButton(@StringRes text: Int, listener: (dialog: Dialog) -> Unit = { dismiss() }) {
        positive_btn.setText(text)
        positive_btn.setOnClickListener{ listener(this) }
    }


    fun setNegativeButton(@StringRes text: Int, listener: (dialog: Dialog) -> Unit = { dismiss() }) {
        negative_btn.setText(text)
        negative_btn.setOnClickListener{ listener(this) }
    }


    private fun createButton(): Button {
        val res = Button(context)
        val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f)
        res.layoutParams = lp
        return res
    }

    private inner class DialogDiscardingOnClickListener : DialogInterface.OnClickListener {
        override fun onClick(dialogInterface: DialogInterface, i: Int) {
            dialogInterface.dismiss()
        }
    }
}
