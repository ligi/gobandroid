package org.ligi;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.ligi.androidhelper.AXT;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.sgf.SGFReader;
import org.ligi.gobandroid_hd.ui.tsumego.NextTsumegoDeterminator;
import org.robolectric.RobolectricTestRunner;

import java.io.File;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class TheNextTsumegoDeterminator {

    private final static String PATH = "test_sgf_path/";
    private final static String SINGLE_DIGIT_FILENAME1 = PATH + "foo1.sgf";
    private final static String SINGLE_DIGIT_FILENAME2 = PATH + "foo2.sgf";
    private final static String TWO_DIGIT_FILENAME1 = PATH + "foo01.sgf";
    private final static String TWO_DIGIT_FILENAME2 = PATH + "foo02.sgf";
    private final static String THREE_DIGIT_FILENAME1 = PATH + "foo201.sgf";
    private final static String THREE_DIGIT_FILENAME2 = PATH + "foo202.sgf";


    @Before
    public void setUp() {
        new File(PATH).mkdir();
    }

    @After
    public void tearDown() {
        AXT.at(new File(PATH)).deleteRecursive();
    }

    @org.junit.Test
    public void should_find_next_for_single_digit_when_there() throws Exception {
        new File(SINGLE_DIGIT_FILENAME1).createNewFile();
        new File(SINGLE_DIGIT_FILENAME2).createNewFile();

        String result = NextTsumegoDeterminator.calcNextTsumego(SINGLE_DIGIT_FILENAME1);

        assertThat(result).isEqualTo(SINGLE_DIGIT_FILENAME2);
    }


    @org.junit.Test
    public void should_not_find_next_for_single_digit_when_not_there() throws Exception {
        new File(SINGLE_DIGIT_FILENAME1).createNewFile();

        String result = NextTsumegoDeterminator.calcNextTsumego(SINGLE_DIGIT_FILENAME1);

        assertThat(result).isEqualTo(null);
    }

    @org.junit.Test
    public void should_find_next_for_two_digit_when_there() throws Exception {
        new File(TWO_DIGIT_FILENAME1).createNewFile();
        new File(TWO_DIGIT_FILENAME2).createNewFile();

        String result = NextTsumegoDeterminator.calcNextTsumego(TWO_DIGIT_FILENAME1);

        assertThat(result).isEqualTo(TWO_DIGIT_FILENAME2);
    }


    @org.junit.Test
    public void should_not_find_next_for_two_digit_when_not_there() throws Exception {
        new File(TWO_DIGIT_FILENAME1).createNewFile();

        String result = NextTsumegoDeterminator.calcNextTsumego(TWO_DIGIT_FILENAME1);

        assertThat(result).isEqualTo(null);
    }

    @org.junit.Test
    public void should_find_next_for_three_digit_when_there() throws Exception {
        new File(THREE_DIGIT_FILENAME1).createNewFile();
        new File(THREE_DIGIT_FILENAME2).createNewFile();

        String result = NextTsumegoDeterminator.calcNextTsumego(THREE_DIGIT_FILENAME1);

        assertThat(result).isEqualTo(THREE_DIGIT_FILENAME2);
    }


    @org.junit.Test
    public void should_not_find_next_for_three_digit_when_not_there() throws Exception {
        new File(THREE_DIGIT_FILENAME1).createNewFile();

        String result = NextTsumegoDeterminator.calcNextTsumego(THREE_DIGIT_FILENAME1);

        assertThat(result).isEqualTo(null);
    }

}