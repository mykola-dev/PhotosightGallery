package ds.photosight.core

import android.content.Context
import com.chibatching.kotpref.KotprefModel

class Prefs(context: Context) : KotprefModel(context) {
    var appVersion by intPref()
}
