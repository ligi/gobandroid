package org.ligi.gobandroid_hd.test_helper_functions

import android.content.Context
import org.assertj.core.api.Fail.fail
import java.io.IOException
import java.io.StringWriter

fun readAssetHowItShouldBe(context: Context, fileName: String): String? {
    try {
        val assets = context.assets
        val inputStream = assets.open(fileName)
        return inputStream.reader().readText()
    } catch (e: IOException) {
        fail("could not read test asset $fileName $e")
        return null
    }
}

fun readAsset(context: Context, fileName: String): String? {

        val assets = context.assets
        val inputStream = assets.open(fileName)
        val foo = StringWriter()
        inputStream.reader().copyTo(foo)
        return foo.toString()


}
