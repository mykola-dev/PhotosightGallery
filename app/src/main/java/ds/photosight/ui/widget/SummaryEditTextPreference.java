package ds.photosight.ui.widget;

import android.content.Context;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.util.AttributeSet;

public class SummaryEditTextPreference extends EditTextPreference {

	public SummaryEditTextPreference(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		init();
	}


	private void init() {

		setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference p, Object o) {
				p.setSummary(getText());
				return true;
			}
		});
	}


	@Override
	public CharSequence getSummary() {
		return getText();
	}
}
