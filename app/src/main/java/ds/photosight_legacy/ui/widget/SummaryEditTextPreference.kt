package ds.photosight_legacy.ui.widget

import android.content.Context
import android.preference.EditTextPreference
import android.util.AttributeSet

class SummaryEditTextPreference(context: Context, attrs: AttributeSet) : EditTextPreference(context, attrs) {

    init {
        setOnPreferenceChangeListener { p, o ->
            p.summary = text
            true
        }
    }

    override fun getSummary(): CharSequence = text
}
