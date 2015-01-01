/**
 * gobandroid 
 * by Marcus -Ligi- Bueschleb 
 * http://ligi.de
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as 
 * published by the Free Software Foundation; 
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. 
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 **/

package org.ligi.gobandroid_hd.ui;

import android.content.Intent;
import android.os.Bundle;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;

import static org.ligi.gobandroid_hd.InteractionScope.MODE_REVIEW;

/**
 * Activity to load a SGF with a ProgressDialog showing the Progress
 *
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         <p/>
 *         License: This software is licensed with GPLv3
 */

public class SGFLoadFromExternalActivity extends GobandroidFragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // that's the main reason why we build this fwd activity
        App.getInteractionScope().setMode(MODE_REVIEW);

        // take the original intent and change the class - the rest stays
        final Intent fwd_intent = getIntent();
        fwd_intent.setClass(this, SGFLoadActivity.class);
        startActivity(fwd_intent);

        finish(); // we are done
    }

}