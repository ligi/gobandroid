package org.ligi.gobandroid_hd.ui.share;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import org.ligi.android.common.dialogs.DialogDiscarder;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.SGFHelper;
import org.ligi.gobandroid_hd.ui.GobandroidDialog;

/**
 * Dialog with the intention to share the current Game
 *
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         <p/>
 *         License: This software is licensed with GPLv3
 */
public class ShareSGFDialog extends GobandroidDialog {

    /**
     * when no fname used in constructor -> use the current game
     * @param context
     */
    public ShareSGFDialog(Context context) {
        super(context);
        setContentView(R.layout.share_options);
        setTitle(R.string.share);
        setIconResource(android.R.drawable.ic_menu_share);
        setPositiveButton(R.string.ok,new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                dialog.dismiss();
            }
        });

        setNegativeButton(R.string.cancel,new DialogDiscarder());


        Spinner type_spinner=(Spinner)findViewById(R.id.type_spinner);
        type_spinner.setAdapter(new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,new String[] {"Recorded Game","Commented Game","Tsumego","Situation","Other"}));

        setIsSmallDialog();
    }


    public ShareSGFDialog(Context context,String fname) {
        this(context);
    }

}
