package org.ementasua;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Prefs extends PreferenceActivity{
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.layout.settings);
      setContentView(R.layout.settings_layout);
      Button b = (Button) findViewById(R.id.button_save);
      b.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			finish();
		}
	});
   }
}
