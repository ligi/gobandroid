package org.ligi.gobandroid_hd

import org.ligi.trulesk.AppReplacingRunnerBase

class AppReplacingRunner : AppReplacingRunnerBase() {

    override fun testAppClass() = TestApp::class.java

}
