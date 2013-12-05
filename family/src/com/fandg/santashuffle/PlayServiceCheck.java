package com.fandg.santashuffle;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * 
 */
public class PlayServiceCheck extends Activity {

	static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
	static final String PLAYSERVICES_PACKAGE = "com.google.android.gms";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playservicecheck);
	}


	public void clickYes(View view) {
		try {
			// Tries the market first
    	    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+ PLAYSERVICES_PACKAGE)));
    	} catch (android.content.ActivityNotFoundException anfe) {
    		//If cant install from the market try the url
    		 startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+ PLAYSERVICES_PACKAGE)));
    	}
		finish();
		System.exit(0);
	}
	

	public void clickCancel(View view) {
		finish();
		System.exit(0);
	}
	

	public static boolean checkPlayServicesStatus(Activity activity) {
		  int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
		  if (status != ConnectionResult.SUCCESS) {
		    if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
		    	return true;
		    } else {
		      Toast.makeText(activity, "This device is not supported.",
		          Toast.LENGTH_LONG).show();
		      return false;
		    }
		  }
		  else{
			  return false;
		  }
		} 
}
