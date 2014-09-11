package org.ligi.gobandroidhd.unittest;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.common.base.Optional;

import org.ligi.axt.AXT;
import org.ligi.gobandroid_hd.ui.tsumego.NextTsumegoFileFinder;

import java.io.File;

import static org.fest.assertions.api.Assertions.assertThat;

public class TheNextTsumegoFileFinder extends AndroidTestCase {

    private File path;
    private final static String SINGLE_DIGIT_FILENAME1 = "foo1.sgf";
    private final static String SINGLE_DIGIT_FILENAME2 = "foo2.sgf";
    private final static String TWO_DIGIT_FILENAME1 = "foo01.sgf";
    private final static String TWO_DIGIT_FILENAME2 = "foo02.sgf";
    private final static String THREE_DIGIT_FILENAME1 = "foo201.sgf";
    private final static String THREE_DIGIT_FILENAME2 = "foo202.sgf";
    private final static String THREE_DIGIT_FILENAME5 = "foo205.sgf";

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
    public void testShouldNotExplodeIfNoNumberIsInFilename() throws Exception {
        Optional<String> result = NextTsumegoFileFinder.calcNextTsumego("FOO");

        assertThat(result.isPresent()).isEqualTo(false);
    }

    @SmallTest
    public void testShouldFindNextForSingleDigitWhenThere() throws Exception {
        new File(path, SINGLE_DIGIT_FILENAME1).createNewFile();
        new File(path, SINGLE_DIGIT_FILENAME2).createNewFile();

        Optional<String> result = NextTsumegoFileFinder.calcNextTsumego(path + "/" + SINGLE_DIGIT_FILENAME1);

        assertThat(result.get()).isEqualTo(path + "/" + SINGLE_DIGIT_FILENAME2);
    }


    @SmallTest
    public void testShouldNotFindNextForSingleDigitWhenNotThere() throws Exception {
        new File(path, SINGLE_DIGIT_FILENAME1).createNewFile();

        Optional<String> result = NextTsumegoFileFinder.calcNextTsumego(path + "/" + SINGLE_DIGIT_FILENAME1);

        assertThat(result.isPresent()).isEqualTo(false);
    }

    @SmallTest
    public void testShouldFindNextForTwoDigitWhenThere() throws Exception {
        new File(path, TWO_DIGIT_FILENAME1).createNewFile();
        new File(path, TWO_DIGIT_FILENAME2).createNewFile();

        Optional<String> result = NextTsumegoFileFinder.calcNextTsumego(path + "/" + TWO_DIGIT_FILENAME1);

        assertThat(result.get()).isEqualTo(path + "/" + TWO_DIGIT_FILENAME2);
    }


    @SmallTest
    public void testShouldNotFindNextForTwoDigitWhenNotThere() throws Exception {
        new File(path, TWO_DIGIT_FILENAME1).createNewFile();

        Optional<String> result = NextTsumegoFileFinder.calcNextTsumego(path + "/" + TWO_DIGIT_FILENAME1);

        assertThat(result.isPresent()).isEqualTo(false);
    }


    @SmallTest
    public void testShouldFindNextForThreeDigitWhenThere() throws Exception {
        new File(path, THREE_DIGIT_FILENAME1).createNewFile();
        new File(path, THREE_DIGIT_FILENAME2).createNewFile();

        Optional<String> result = NextTsumegoFileFinder.calcNextTsumego(path + "/" + THREE_DIGIT_FILENAME1);

        assertThat(result.get()).isEqualTo(path + "/" + THREE_DIGIT_FILENAME2);
    }


    @SmallTest
    public void testShouldNotFindNextForThreeDigitWhenNotThere() throws Exception {
        new File(path, THREE_DIGIT_FILENAME1).createNewFile();

        Optional<String> result = NextTsumegoFileFinder.calcNextTsumego(path + "/" + THREE_DIGIT_FILENAME1);

        assertThat(result.isPresent()).isEqualTo(false);
    }

    @SmallTest
    public void testFindNextTsumegoAfterGap() throws Exception {
        new File(path, THREE_DIGIT_FILENAME1).createNewFile();
        new File(path, THREE_DIGIT_FILENAME5).createNewFile();

        Optional<String> result = NextTsumegoFileFinder.calcNextTsumego(path + "/" + THREE_DIGIT_FILENAME1);

        assertThat(result.get()).isEqualTo(path + "/" + THREE_DIGIT_FILENAME5);
    }

}