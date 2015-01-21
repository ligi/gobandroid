package org.ligi.gobandroid_hd.ui.editing;

import org.ligi.gobandroid_hd.R;

import java.util.ArrayList;
import java.util.List;

public class StatefulEditModeItems {
    private final List<EditModeItem> list;
    private int act_activated = 0;

    public StatefulEditModeItems() {
        list = new ArrayList<>();
        list.add(new EditModeItem(R.drawable.stone_black, EditGameMode.BLACK, R.string.black));
        list.add(new EditModeItem(R.drawable.stone_white, EditGameMode.WHITE,  R.string.white));
        list.add(new EditModeItem(R.drawable.stone_circle, EditGameMode.CIRCLE, R.string.circle));
        list.add(new EditModeItem(R.drawable.stone_square, EditGameMode.SQUARE, R.string.square ));
        list.add(new EditModeItem(R.drawable.stone_triangle, EditGameMode.TRIANGLE, R.string.triangle));
        list.add(new EditModeItem(R.drawable.stone_number, EditGameMode.NUMBER, R.string.number));
        list.add(new EditModeItem(R.drawable.stone_letter, EditGameMode.LETTER, R.string.letter));
    }

    public List<EditModeItem> getList() {
        return list;
    }

    public void setActivatedItem(int id) {
        act_activated = id;
    }

    public int getActivatedItem() {
        return act_activated;
    }

    public EditGameMode getActMode() {
        return list.get(act_activated).mode;
    }
}
