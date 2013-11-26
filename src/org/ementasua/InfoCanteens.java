package org.ementasua;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class InfoCanteens extends Activity {
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.infocanteens);
      
      String canteen = getIntent().getStringExtra("canteens");
      setTitle(canteen);

      TextView tv = (TextView) findViewById(R.id.info_content);
      if(canteen.equals("Santiago")){
          tv.setText(R.string.info_Santiago);
      }else if(canteen.equals("Crasto")){
    	  tv.setText(R.string.info_Crasto);
      }else if(canteen.equals("SnackBar")){
    	  tv.setText(R.string.info_SnackBar);
      }
   }
}
