package com.fandg.santashuffle;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;


/**
 * 
 * Activity for the splash screen.
 * 
 * This activity has not history - after it it finishes it is destroyed and will
 * only be created again after a crash or initial start
 * 
 * For this class to work you will need the supporting layout.xml or create your own.
 * 
 * @author Arden
 * 
 */
public class SplashScreen extends Activity {
	protected int _splashTime = 5000;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		setContentView(R.layout.splashscreen);
		
		 Thread background = new Thread() {
	            public void run() {
	                 
	                try {
	                    // Thread will sleep for 5 seconds
	                    sleep(_splashTime);
	                     
	                    // After 5 seconds redirect to another intent
	                    Intent i=new Intent(getBaseContext(),Main.class);
	                    startActivity(i);
	                     
	                    //Remove activity
	                    finish();
	                     
	                } catch (Exception e) {
	                 
	                }
	            }
	        };
	         
	        // start thread
	        background.start();
	}
	
	@Override
	public void finish() {
		super.finish();
	}

	@Override
	public void onBackPressed() {
		this.finish();
		super.onBackPressed();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		TextView version = (TextView) findViewById(R.id.versionnumber);
		version.setText(Family._APP_VERSION);
	}
}
