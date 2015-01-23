package org.ligi.gobandroid_hd.logic;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

public class CellFactory {
    private static SparseArray<List<Cell>> cache = new SparseArray<>();

    public static List<Cell> getAllCellsForSquareCached(final int size) {
        final List<Cell> cached = cache.get(size);

        if (cached != null) {
            return cached;
        }

        final List<Cell> created = getAllCellsForRect(size, size);
        cache.append(size, created);
        return created;
    }

    public static List<BoardCell> getAllCellsForSquare(final int size, GoBoard board) {
        final List<BoardCell> result = new ArrayList<>();

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                result.add(new BoardCell(x, y, board));
            }
        }

        return result;
    }

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
