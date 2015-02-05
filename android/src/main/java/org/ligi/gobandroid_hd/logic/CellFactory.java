package org.ligi.gobandroid_hd.logic;

import java.util.ArrayList;
import java.util.List;

public class CellFactory {

    public static List<Cell> getAllCellsForRect(final int sizeX, final int sizeY) {
        final List<Cell> created = new ArrayList<>();

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                created.add(new Cell(x, y));
            }
        }
        return created;
    }

}
