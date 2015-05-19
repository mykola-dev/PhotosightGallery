package ds.photosight.ui.widget

import android.content.Context
import android.preference.EditTextPreference
import android.preference.Preference
import android.util.AttributeSet

public class SummaryEditTextPreference(context: Context, attrs: AttributeSet) : EditTextPreference(context, attrs) {

    init {
        setOnPreferenceChangeListener({ p, o ->
            p.setSummary(getText())
            true
        })
    }

    override fun getSummary(): CharSequence {
        return getText()
    }
}
