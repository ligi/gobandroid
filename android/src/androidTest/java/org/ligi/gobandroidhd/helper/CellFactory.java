package org.ligi.gobandroidhd.helper;

import java.util.ArrayList;
import java.util.List;
import org.ligi.gobandroid_hd.logic.Cell;
import org.ligi.gobandroid_hd.logic.CellImpl;

/**
 * The Petri dish
 */
public class CellFactory {

    public static List<Cell> getAllCellsForRect(final int sizeX, final int sizeY) {
        final List<Cell> created = new ArrayList<>();

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                created.add(new CellImpl(x, y));
            }
        }
        return created;
    }

}
