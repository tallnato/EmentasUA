package org.ementasua;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class About extends Activity {
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.about);
      
      ImageView fb = (ImageView) findViewById(R.id.iv_fb);
      fb.setOnClickListener(new OnClickListener() {
		public void onClick(View arg0) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/EmentasUA"));
			startActivity(intent);
		}
      });
      
      ImageView market = (ImageView) findViewById(R.id.iv_market);
      market.setOnClickListener(new OnClickListener() {
		public void onClick(View arg0) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=org.ementasua"));
			startActivity(intent);
		}
      });
      
      ImageView api = (ImageView) findViewById(R.id.iv_api);
      api.setOnClickListener(new OnClickListener() {
		public void onClick(View arg0) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://api.web.ua.pt/"));
			startActivity(intent);
		}
      });
   }
}
