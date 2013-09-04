package com.nraboy.spyfi;

import android.os.*;
import com.actionbarsherlock.app.*;
import com.actionbarsherlock.view.*;
import android.content.*;
import android.preference.*;
import android.preference.Preference.*;
import android.net.Uri;
import android.preference.PreferenceManager;
import com.google.analytics.tracking.android.EasyTracker;

public class SettingsActivity extends SherlockPreferenceActivity {

    private Preference appVersion;
    private Preference appRate;
    private Preference appSupport;
    private Preference appTwitter;
    private String appVersionStr;
    private SharedPreferences settings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        appVersionStr = getResources().getString(R.string.dialog_message_version) + " " + getResources().getString(R.string.app_version);
        appVersion = (Preference) findPreference("pref_key_app_version");
        appRate = (Preference) findPreference("pref_key_app_rate");
        appSupport = (Preference) findPreference("pref_key_support_contact");
        appTwitter = (Preference) findPreference("pref_key_app_twitter");
        appRate.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.app_store)));
                startActivity(browserIntent);
                return true;
            }
        });
        appSupport.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.app_support)));
                startActivity(browserIntent);
                return true;
            }
        });
        appTwitter.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.app_twitter)));
                startActivity(browserIntent);
                return true;
            }
        });
        appVersion.setTitle(appVersionStr);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(settings.getBoolean("pref_key_analytics", true)) {
            EasyTracker.getInstance().activityStart(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(settings.getBoolean("pref_key_analytics", true)) {
            EasyTracker.getInstance().activityStop(this);
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:             
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
}
