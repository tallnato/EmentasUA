package org.ementasua;

import utilclass.EmentasGetter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Start extends Activity{
	private EmentasTask et;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		
		EmentasGetter.setDirPath(getFilesDir());
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		et = new EmentasTask();
		et.execute();
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		et.cancel(true);
	}

	public class EmentasTask extends AsyncTask<Void, Integer, Boolean> {
		TextView tv = (TextView) findViewById(R.id.start_status);

		protected void onProgressUpdate(Integer... s) {
			tv.setText(s[0]);
		}

		protected void onPostExecute(Boolean b) {
			if(b){
				tv.setText(R.string.start_finnished);
				startActivity(new Intent(getApplicationContext(), EmentasUA.class));
				finish();
			}else{
				tv.setText(R.string.start_error);
				ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar1);
				pb.setIndeterminate(false);
				pb.setProgress(pb.getMax());
				
				AlertDialog.Builder builder = new AlertDialog.Builder(Start.this);
				builder.setCancelable(true);
				builder.setMessage(R.string.start_error_msg);
				builder.setTitle(R.string.start_error);
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				builder.setPositiveButton(R.string.start_error_wireless, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
					}
				});
				builder.setNegativeButton(R.string.start_error_cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				builder.setNeutralButton(R.string.start_error_retry, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						new EmentasTask().execute();
					}
				});
				builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						finish();
					}
				});

				builder.show();
			}
		}
		@Override
		protected void onPreExecute() {
			tv.setText(R.string.start_downloading);
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			try { 
				EmentasGetter.start(this);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		
		public void updateProgress(Integer text){
			publishProgress(text);
		}
	}
}
