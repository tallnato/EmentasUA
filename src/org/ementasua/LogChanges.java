package org.ementasua;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class LogChanges extends Activity {
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.logchanges);
      
      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putBoolean("log_show",true);
      editor.commit();
   }
}
