package org.ligi.gobandroid_hd

import android.app.Application
import android.support.v7.app.AppCompatDelegate
import com.chibatching.kotpref.Kotpref
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import org.ligi.gobandroid_hd.model.GameProvider
import org.ligi.gobandroid_hd.ui.GoPrefs
import org.ligi.gobandroid_hd.ui.GobandroidTracker
import org.ligi.gobandroid_hd.ui.GobandroidTrackerResolver
import org.ligi.gobandroid_hd.ui.application.GoAndroidEnvironment
import org.ligi.gobandroid_hd.ui.application.GobandroidSettingsTransition
import org.ligi.gobandroid_hd.util.TsumegoCleaner
import org.ligi.tracedroid.TraceDroid
import org.ligi.tracedroid.logging.Log

/**
 * the central Application-Context
 */
open class App : Application() {

    override fun onCreate() {
        super.onCreate()

        env = GoAndroidEnvironment(this@App)
        kodein = Kodein {
            bind<InteractionScope>() with singleton { InteractionScope() }
            bind<GoAndroidEnvironment>() with singleton { env }
            bind<GameProvider>() with singleton { GameProvider(instance()) }
            bind<App>() with singleton { this@App }
        }

        GobandroidSettingsTransition(this).transition()

        TsumegoCleaner(env).clean()

        tracker.init(this)

        TraceDroid.init(this)
        Log.setTAG("gobandroid")

        CloudHooks.onApplicationCreation(this)

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        AppCompatDelegate.setDefaultNightMode(GoPrefs.getThemeInt())
    }

    open val isTesting = false

    companion object {

        lateinit var kodein: Kodein
        lateinit var env: GoAndroidEnvironment

        val tracker: GobandroidTracker
            get() = GobandroidTrackerResolver.getTracker()

    }
}
