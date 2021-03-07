package org.ligi.gobandroid_hd.unittest

import android.content.Context
import androidx.test.InstrumentationRegistry
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.ligi.gobandroid_hd.ui.tsumego.NextTsumegoFileFinder
import java.io.File

class TheNextTsumegoFileFinder {

    private val SINGLE_DIGIT_FILENAME1 = "foo1.sgf"
    private val SINGLE_DIGIT_FILENAME2 = "foo2.sgf"
    private val TWO_DIGIT_FILENAME1 = "foo01.sgf"
    private val TWO_DIGIT_FILENAME2 = "foo02.sgf"
    private val THREE_DIGIT_FILENAME1 = "foo201.sgf"
    private val THREE_DIGIT_FILENAME2 = "foo202.sgf"
    private val THREE_DIGIT_FILENAME5 = "foo205.sgf"

    private val path by lazy { InstrumentationRegistry.getTargetContext().getDir("testDir", Context.MODE_PRIVATE) }

    @Before
    fun setUp() {
        path!!.mkdirs()
    }

    @After
    fun tearDown() {
        path.deleteRecursively()
    }

    @Test
    fun testShouldNotExplodeIfNoNumberIsInFilename() {
        val result = NextTsumegoFileFinder.calcNextTsumego("FOO")

        assertThat(result).isNull()
    }

    @Test
    fun testShouldFindNextForSingleDigitWhenThere() {
        File(path, SINGLE_DIGIT_FILENAME1).createNewFile()
        File(path, SINGLE_DIGIT_FILENAME2).createNewFile()

        val result = NextTsumegoFileFinder.calcNextTsumego(path.toString() + "/" + SINGLE_DIGIT_FILENAME1)

        assertThat(result).isEqualTo(path.toString() + "/" + SINGLE_DIGIT_FILENAME2)
    }


    @Test
    fun testShouldNotFindNextForSingleDigitWhenNotThere() {
        File(path, SINGLE_DIGIT_FILENAME1).createNewFile()

        val result = NextTsumegoFileFinder.calcNextTsumego(path.toString() + "/" + SINGLE_DIGIT_FILENAME1)

        assertThat(result).isNull()
    }

    @Test
    fun testShouldFindNextForTwoDigitWhenThere() {
        File(path, TWO_DIGIT_FILENAME1).createNewFile()
        File(path, TWO_DIGIT_FILENAME2).createNewFile()

        val result = NextTsumegoFileFinder.calcNextTsumego(path.toString() + "/" + TWO_DIGIT_FILENAME1)

        assertThat(result).isEqualTo(path.toString() + "/" + TWO_DIGIT_FILENAME2)
    }


    @Test
    fun testShouldNotFindNextForTwoDigitWhenNotThere() {
        File(path, TWO_DIGIT_FILENAME1).createNewFile()

        val result = NextTsumegoFileFinder.calcNextTsumego(path.toString() + "/" + TWO_DIGIT_FILENAME1)

        assertThat(result).isNull()
    }


    @Test
    fun testShouldFindNextForThreeDigitWhenThere() {
        File(path, THREE_DIGIT_FILENAME1).createNewFile()
        File(path, THREE_DIGIT_FILENAME2).createNewFile()

        val result = NextTsumegoFileFinder.calcNextTsumego(path.toString() + "/" + THREE_DIGIT_FILENAME1)

        assertThat(result).isEqualTo(path.toString() + "/" + THREE_DIGIT_FILENAME2)
    }


    @Test
    fun testShouldNotFindNextForThreeDigitWhenNotThere() {
        File(path, THREE_DIGIT_FILENAME1).createNewFile()

        val result = NextTsumegoFileFinder.calcNextTsumego(path.toString() + "/" + THREE_DIGIT_FILENAME1)

        assertThat(result).isNull()
    }

    @Test
    fun testFindNextTsumegoAfterGap() {
        File(path, THREE_DIGIT_FILENAME1).createNewFile()
        File(path, THREE_DIGIT_FILENAME5).createNewFile()

        val result = NextTsumegoFileFinder.calcNextTsumego(path.toString() + "/" + THREE_DIGIT_FILENAME1)

        assertThat(result).isEqualTo(path.toString() + "/" + THREE_DIGIT_FILENAME5)
    }

}