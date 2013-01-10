package org.ligi.gobandroid_hd.ui.editing;

import java.util.ArrayList;
import java.util.List;

import org.ligi.gobandroid_beta.R;

public class EditModeItemPool {
	private List<EditModeItem> list;
	private int act_activated = 0;

	public EditModeItemPool() {
		list = new ArrayList<EditModeItem>();

		list.add(new EditModeItem(R.drawable.stone_black, EditGameMode.BLACK));
		list.add(new EditModeItem(R.drawable.stone_white, EditGameMode.WHITE));
		list.add(new EditModeItem(R.drawable.stone_circle, EditGameMode.CIRCLE));
		list.add(new EditModeItem(R.drawable.stone_square, EditGameMode.SQUARE));
		list.add(new EditModeItem(R.drawable.stone_triangle, EditGameMode.TRIANGLE));
		list.add(new EditModeItem(R.drawable.stone_number, EditGameMode.NUMBER));
		list.add(new EditModeItem(R.drawable.stone_letter, EditGameMode.LETTER));
	}

	public List<EditModeItem> getList() {
		return list;
	}

	public void setActivateItem(int id) {
		act_activated = id;
	}

	public int getActivatedItem() {
		return act_activated;
	}

	public EditGameMode getActMode() {
		return list.get(act_activated).mode;
	}
}
