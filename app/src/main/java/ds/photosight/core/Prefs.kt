/*
 * Copyright (c) 2016. Deviant Studio
 */

package ds.photosight.core

import android.content.Context
import com.chibatching.kotpref.KotprefModel

class Prefs(context: Context) : KotprefModel(context) {
    var sampleData by stringPref()
}
