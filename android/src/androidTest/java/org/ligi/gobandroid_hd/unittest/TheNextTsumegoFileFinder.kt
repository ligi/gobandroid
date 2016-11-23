package org.ligi.gobandroid_hd.unittest

import android.content.Context
import android.test.AndroidTestCase
import android.test.suitebuilder.annotation.SmallTest
import org.assertj.core.api.Assertions.assertThat
import org.ligi.gobandroid_hd.ui.tsumego.NextTsumegoFileFinder
import java.io.File

class TheNextTsumegoFileFinder : AndroidTestCase() {

    private val path by lazy { context.getDir("testDir", Context.MODE_PRIVATE) }

    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()

        path!!.mkdirs()
    }

    @Throws(Exception::class)
    override fun tearDown() {
        super.tearDown()
        path.deleteRecursively()
    }

    @SmallTest
    @Throws(Exception::class)
    fun testShouldNotExplodeIfNoNumberIsInFilename() {
        val result = NextTsumegoFileFinder.calcNextTsumego("FOO")

        assertThat(result).isNull()
    }

    @SmallTest
    @Throws(Exception::class)
    fun testShouldFindNextForSingleDigitWhenThere() {
        File(path, SINGLE_DIGIT_FILENAME1).createNewFile()
        File(path, SINGLE_DIGIT_FILENAME2).createNewFile()

        val result = NextTsumegoFileFinder.calcNextTsumego(path.toString() + "/" + SINGLE_DIGIT_FILENAME1)

        assertThat(result).isEqualTo(path.toString() + "/" + SINGLE_DIGIT_FILENAME2)
    }


    @SmallTest
    @Throws(Exception::class)
    fun testShouldNotFindNextForSingleDigitWhenNotThere() {
        File(path, SINGLE_DIGIT_FILENAME1).createNewFile()

        val result = NextTsumegoFileFinder.calcNextTsumego(path.toString() + "/" + SINGLE_DIGIT_FILENAME1)

        assertThat(result).isNull()
    }

    @SmallTest
    @Throws(Exception::class)
    fun testShouldFindNextForTwoDigitWhenThere() {
        File(path, TWO_DIGIT_FILENAME1).createNewFile()
        File(path, TWO_DIGIT_FILENAME2).createNewFile()

        val result = NextTsumegoFileFinder.calcNextTsumego(path.toString() + "/" + TWO_DIGIT_FILENAME1)

        assertThat(result).isEqualTo(path.toString() + "/" + TWO_DIGIT_FILENAME2)
    }


    @SmallTest
    @Throws(Exception::class)
    fun testShouldNotFindNextForTwoDigitWhenNotThere() {
        File(path, TWO_DIGIT_FILENAME1).createNewFile()

        val result = NextTsumegoFileFinder.calcNextTsumego(path.toString() + "/" + TWO_DIGIT_FILENAME1)

        assertThat(result).isNull()
    }


    @SmallTest
    @Throws(Exception::class)
    fun testShouldFindNextForThreeDigitWhenThere() {
        File(path, THREE_DIGIT_FILENAME1).createNewFile()
        File(path, THREE_DIGIT_FILENAME2).createNewFile()

        val result = NextTsumegoFileFinder.calcNextTsumego(path.toString() + "/" + THREE_DIGIT_FILENAME1)

        assertThat(result).isEqualTo(path.toString() + "/" + THREE_DIGIT_FILENAME2)
    }


    @SmallTest
    @Throws(Exception::class)
    fun testShouldNotFindNextForThreeDigitWhenNotThere() {
        File(path, THREE_DIGIT_FILENAME1).createNewFile()

        val result = NextTsumegoFileFinder.calcNextTsumego(path.toString() + "/" + THREE_DIGIT_FILENAME1)

        assertThat(result).isNull()
    }

    @SmallTest
    @Throws(Exception::class)
    fun testFindNextTsumegoAfterGap() {
        File(path, THREE_DIGIT_FILENAME1).createNewFile()
        File(path, THREE_DIGIT_FILENAME5).createNewFile()

        val result = NextTsumegoFileFinder.calcNextTsumego(path.toString() + "/" + THREE_DIGIT_FILENAME1)

        assertThat(result).isEqualTo(path.toString() + "/" + THREE_DIGIT_FILENAME5)
    }

    companion object {
        private val SINGLE_DIGIT_FILENAME1 = "foo1.sgf"
        private val SINGLE_DIGIT_FILENAME2 = "foo2.sgf"
        private val TWO_DIGIT_FILENAME1 = "foo01.sgf"
        private val TWO_DIGIT_FILENAME2 = "foo02.sgf"
        private val THREE_DIGIT_FILENAME1 = "foo201.sgf"
        private val THREE_DIGIT_FILENAME2 = "foo202.sgf"
        private val THREE_DIGIT_FILENAME5 = "foo205.sgf"
    }

}