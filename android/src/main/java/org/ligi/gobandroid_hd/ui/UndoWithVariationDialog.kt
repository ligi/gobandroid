package org.ligi.gobandroid_hd.ui

import android.app.Activity
import android.support.design.widget.Snackbar

import org.ligi.gobandroid_hd.InteractionScope
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.logic.GoGame

class UndoWithVariationDialog private constructor(activity: Activity) : GobandroidDialog(activity) {

    companion object {

        fun userInvokedUndo(activity: Activity, interactionScope: InteractionScope, game: GoGame) {
            if (interactionScope.ask_variant_session) {
                UndoWithVariationDialog(activity).show()
            } else {
                game.undo(GoPrefs.isKeepVariantWanted)

                val snackbar = Snackbar.make(activity.window.decorView.findViewById(R.id.content_frame), R.string.snackbar_keep_variant_message_enable, Snackbar.LENGTH_LONG)
                snackbar.setAction(R.string.snackbar_keep_variant_action_enable) { interactionScope.ask_variant_session = true }
                snackbar.show()

            }
        }
    }

    init {
        setTitle(R.string.keep_variant)
        setIconResource(R.drawable.ic_action_help_outline)
        setContentView(R.layout.dialog_keep_variant)

        setPositiveButton(R.string.yes, { dialog ->
            gameProvider.get().undo(true)
            dialog.dismiss()
        })

        setNegativeButton(R.string.no, { dialog ->
            gameProvider.get().undo(false)
            dialog.dismiss()

            val snackbar = Snackbar.make(activity.window.decorView.findViewById(R.id.content_frame), R.string.snackbar_keep_variant_message_disable, Snackbar.LENGTH_LONG)
            snackbar.setAction(R.string.snackbar_keep_variant_action_disable) { interactionScope.ask_variant_session = false }
            snackbar.show()
        })
    }


}
