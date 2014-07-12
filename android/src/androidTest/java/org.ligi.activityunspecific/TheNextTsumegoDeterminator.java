package org.ligi;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import org.ligi.axt.AXT;
import org.ligi.gobandroid_hd.ui.tsumego.NextTsumegoDeterminator;

import java.io.File;

import static org.fest.assertions.api.Assertions.assertThat;

public class TheNextTsumegoDeterminator extends AndroidTestCase {

    private File path;
    private final static String SINGLE_DIGIT_FILENAME1 = "foo1.sgf";
    private final static String SINGLE_DIGIT_FILENAME2 = "foo2.sgf";
    private final static String TWO_DIGIT_FILENAME1 = "foo01.sgf";
    private final static String TWO_DIGIT_FILENAME2 = "foo02.sgf";
    private final static String THREE_DIGIT_FILENAME1 = "foo201.sgf";
    private final static String THREE_DIGIT_FILENAME2 = "foo202.sgf";

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        path = getContext().getDir("testDir", Context.MODE_PRIVATE);
        path.mkdirs();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        AXT.at(path).deleteRecursive();
    }

    @SmallTest
    public void test_should_not_explode_if_no_number_is_in_filename() throws Exception {
        String result = NextTsumegoDeterminator.calcNextTsumego("FOO");

        assertThat(result).isEqualTo(null);
    }

    @SmallTest
    public void test_should_find_next_for_single_digit_when_there() throws Exception {
        new File(path, SINGLE_DIGIT_FILENAME1).createNewFile();
        new File(path, SINGLE_DIGIT_FILENAME2).createNewFile();

        String result = NextTsumegoDeterminator.calcNextTsumego(path + "/" + SINGLE_DIGIT_FILENAME1);

        assertThat(result).isEqualTo(path + "/" + SINGLE_DIGIT_FILENAME2);
    }


    @SmallTest
    public void test_should_not_find_next_for_single_digit_when_not_there() throws Exception {
        new File(path, SINGLE_DIGIT_FILENAME1).createNewFile();

        String result = NextTsumegoDeterminator.calcNextTsumego(path + "/" + SINGLE_DIGIT_FILENAME1);

        assertThat(result).isEqualTo(null);
    }

    @SmallTest
    public void test_should_find_next_for_two_digit_when_there() throws Exception {
        new File(path, TWO_DIGIT_FILENAME1).createNewFile();
        new File(path, TWO_DIGIT_FILENAME2).createNewFile();

        String result = NextTsumegoDeterminator.calcNextTsumego(path + "/" + TWO_DIGIT_FILENAME1);

        assertThat(result).isEqualTo(path + "/" + TWO_DIGIT_FILENAME2);
    }


    @SmallTest
    public void test_should_not_find_next_for_two_digit_when_not_there() throws Exception {
        new File(path, TWO_DIGIT_FILENAME1).createNewFile();

        String result = NextTsumegoDeterminator.calcNextTsumego(path + "/" + TWO_DIGIT_FILENAME1);

        assertThat(result).isEqualTo(null);
    }


    @SmallTest
    public void test_should_find_next_for_three_digit_when_there() throws Exception {
        new File(path, THREE_DIGIT_FILENAME1).createNewFile();
        new File(path, THREE_DIGIT_FILENAME2).createNewFile();

        String result = NextTsumegoDeterminator.calcNextTsumego(path + "/" + THREE_DIGIT_FILENAME1);

        assertThat(result).isEqualTo(path + "/" + THREE_DIGIT_FILENAME2);
    }


    @SmallTest
    public void test_should_not_find_next_for_three_digit_when_not_there() throws Exception {
        new File(path, THREE_DIGIT_FILENAME1).createNewFile();

        String result = NextTsumegoDeterminator.calcNextTsumego(path + "/" + THREE_DIGIT_FILENAME1);

        assertThat(result).isEqualTo(null);
    }


}