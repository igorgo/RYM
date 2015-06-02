package home.go.rym;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import home.go.rym.utils.Constants;


public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS);
        addPreferencesFromResource(R.xml.preferences);
        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.activity_settings_toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference instanceof EditTextPreference) {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            String stringValue = newValue.toString();
            if (preference.getKey().equals(Constants.PREF_PASSWORD)) {
                EditText edit = ((EditTextPreference) preference).getEditText();
                String sPref = edit.getTransformationMethod().getTransformation(stringValue, edit).toString();
                preference.setSummary(sPref);
            } else {
                preference.setSummary(stringValue);
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindPreferenceSummaryToValue(findPreference(Constants.PREF_USERNAME));
        bindPreferenceSummaryToValue(findPreference(Constants.PREF_PASSWORD));
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);
        // Trigger the listener immediately with the preference's
        // current value.
        if (preference instanceof EditTextPreference)
            onPreferenceChange(preference,
                    prefs.getString(preference.getKey(), ""));
        if (preference instanceof CheckBoxPreference)
            onPreferenceChange(preference,
                    prefs.getBoolean(preference.getKey(), true));

    }
}
