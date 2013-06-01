package org.ligi.gobandroid_hd.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.ligi.gobandroid_hd.ui.GoBoardViewHD;

public class ZoomGameExtrasFragment extends Fragment {

    private GoBoardViewHD board;
    private boolean show_shadow_stone = false;

    public ZoomGameExtrasFragment() {

    }

    public ZoomGameExtrasFragment(boolean _show_shadow_stone) {
        show_shadow_stone = _show_shadow_stone;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        board = new GoBoardViewHD(this.getActivity(), false, 3.0f);
        board.do_actpos_highlight = show_shadow_stone;
        board.do_actpos_highlight_ony_if_active = false;
        return board;
    }

    public GoBoardViewHD getBoard() {
        return board;
    }

}
