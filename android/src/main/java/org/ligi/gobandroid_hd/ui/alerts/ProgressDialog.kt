/**

 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation;

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see //www.gnu.org/licenses/>.

 */

package org.ligi.gobandroid_hd.ui.alerts

import android.content.Context
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.Bind
import butterknife.ButterKnife
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.ui.GobandroidDialog

open class ProgressDialog(context: Context) : GobandroidDialog(context) {

    @Bind(R.id.message)
    lateinit var message: TextView

    @Bind(R.id.progressBar)
    lateinit var progress: ProgressBar

    init {
        setContentView(R.layout.dialog_game_load)
        ButterKnife.bind(this)
        setCancelable(false)
    }

}