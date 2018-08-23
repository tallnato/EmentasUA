package org.ementasua;

import utilclass.EmentasGetter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class ConnectivityReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
        
        if(checkWiFi3G(context)){
	        try { 
				EmentasGetter.start(null);
			} catch (Exception e) {
				e.printStackTrace();
			} 
        }
    }
	private boolean checkWiFi3G(Context context)
	{
		ConnectivityManager connec = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return(connec.getActiveNetworkInfo() != null && connec.getActiveNetworkInfo().isAvailable() && connec
				.getActiveNetworkInfo().isConnected());
	}
}