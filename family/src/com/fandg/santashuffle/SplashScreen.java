package com.fandg.santashuffle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

/**
 * splash screen
 * 
 * @author Arden
 * 
 */
public class SplashScreen extends Activity {
    protected int     _splashTime  = 5000;
    protected boolean screenTapped = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		if(PlayServiceCheck.checkPlayServicesStatus(this)){
			Intent intent = new Intent(this, PlayServiceCheck.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			finish();
			return;
		}
        setContentView(R.layout.splashscreen);
        
        final Thread background = new Thread() {
            public void run() {
                
                try {
                    
                    screenTapped = false;
                    // Thread will sleep for 5 seconds
                    for (int i = 0; i < 100; i++) {
                        if (screenTapped == true) {
                            break;
                        }
                        sleep(_splashTime / 100);
                    }
                    
                    // After 5 seconds redirect to another intent
                    Intent i = new Intent(getBaseContext(), Main.class);
                    startActivity(i);
                    
                    // Remove activity
                    finish();
                    
                } catch (Exception e) {
                    
                }
            }
        };
        
        // start thread
        background.start();
        
        // Close the splash screen by tapping the screen
        View splashView = findViewById(R.id.splashscreen);
        splashView.setOnTouchListener(new OnTouchListener() {
            
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                screenTapped = true;
                
                return true;
            }
            
        });
        
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
