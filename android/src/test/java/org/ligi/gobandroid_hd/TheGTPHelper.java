package org.ligi.gobandroid_hd;

import org.junit.Test;
import org.ligi.gobandroid_hd.logic.CellImpl;
import org.ligi.gobandroid_hd.logic.GTPHelper;

import static org.assertj.core.api.Assertions.assertThat;

public class TheGTPHelper {
    /*
    the coordinate system in gtp

        A B C D E F G H J
      9 . . . . . . . . . 9
      8 . . . . . . . . . 8
      7 . . + . . . + . . 7
      6 . . . . . . . . . 6
      5 . . . . + . . . . 5
      4 . . . . . . . . . 4
      3 . . + . . . + . . 3
      2 . . . . . . . . . 2
      1 . . . . . . . . . 1
        A B C D E F G H J
    */

    @Test
    public void testThatTopLeftGTPCoordinateIsValid() throws Exception {

        final String tested = GTPHelper.coordinates2gtpstr(new CellImpl(0, 0), 19);

        assertThat(tested).isEqualTo("A19");
    }


    @Test
    public void testThatTopLeftGTPCoordinateIsValidInAnotherBoardSize() throws Exception {

        final String tested = GTPHelper.coordinates2gtpstr(new CellImpl(0, 0), 9);

        assertThat(tested).isEqualTo("A9");
    }

    @Test
    public void testThatBottomRightGTPCoordinateIsValidInAnotherBoardSize() throws Exception {

        final String tested = GTPHelper.coordinates2gtpstr(new CellImpl(8, 8), 9);

        assertThat(tested).isEqualTo("J1");
    }

}