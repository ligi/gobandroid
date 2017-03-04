package org.ligi.gobandroid_hd

import com.chibatching.kotpref.KotprefModel

object CloudPrefs : KotprefModel() {
    var userWantsPlayConnection: Boolean by booleanPref(default = false)
}