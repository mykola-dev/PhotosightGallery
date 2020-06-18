/*
 * Copyright (c) 2016. Deviant Studio
 */

package ds.photosight.core

import android.content.Context
import com.chibatching.kotpref.KotprefModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Prefs @Inject constructor(@ApplicationContext context: Context) : KotprefModel(context) {
    var sampleData by stringPref()
}
