package org.ligi.gobandroid_hd.online;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;

/**
 * Created with IntelliJ IDEA.
 * User: ligi
 * Date: 1/31/13
 * Time: 5:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class OnlineCreateFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.online_create_game,container,false);
        Button b=(Button)v.findViewById(R.id.create_btn);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UploadGameToCloudEndpointsWithSend((GobandroidFragmentActivity)getActivity(), "public_invite").execute();
            }
        });
        //
        return v;
    }


}
