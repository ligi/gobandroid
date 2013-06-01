package org.ligi.gobandroid_hd.ui.online;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;

/**
 * User: ligi
 * Date: 1/31/13
 * Time: 5:46 PM
 */
public class OnlineCreateFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.online_create_game, container, false);
        Button b = (Button) v.findViewById(R.id.create_btn);

        final EditText game_name = (EditText) v.findViewById(R.id.game_name_et);


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GoGame p_game = new GoGame(((byte) ((GobandroidFragmentActivity) getActivity()).getApp().getGame().getSize()));
                p_game.getMetaData().setName(game_name.getText().toString());
                ((GobandroidFragmentActivity) getActivity()).getApp().getInteractionScope().setGame(p_game);
                new UploadGameToCloudEndpointsWithDialog((GobandroidFragmentActivity) getActivity(), "public_invite").execute();

            }
        });
        return v;
    }


}
