package com.fandg.santashuffle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.ListView;


public class Family extends Application{
	
	private static ArrayList<String> names = new ArrayList<String>();
	private static ArrayList<String> recipients = new ArrayList<String>();
	
	public static ArrayList<Integer> chosenIndicies = new ArrayList<Integer>();
	public static String _APP_VERSION = "";
	public static ArrayList<Integer> getChosenIndicies() {
		return chosenIndicies;
	}



	public static int getCurrentSelection() {
		return _currentSelection;
	}

	public static void setCurrentSelection(int currentSelection) {
		_currentSelection = currentSelection;
	}

	public static ArrayList<String> getNames() {
		return names;
	}

	public static ArrayList<String> getRecipients() {
		return recipients;
	}

	private static int _currentSelection = 0;
	private static boolean isPaired = false;
	private static boolean isHideGifters = false;
	

	public static boolean isHideGifters() {
		return isHideGifters;
	}

	public static void setHideGifters(boolean isHideGifters) {
		Family.isHideGifters = isHideGifters;
	}

	public static boolean isPaired() {
		return isPaired;
	}

	public static void setPaired(boolean isPaired) {
		Family.isPaired = isPaired;
	}
	

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Context context = getApplicationContext(); // or activity.getApplicationContext()
		PackageManager packageManager = context.getPackageManager();
		String packageName = context.getPackageName();

	
		// Obtains the app version based in the manifest file.
		try {
		    _APP_VERSION = packageManager.getPackageInfo(packageName, 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {

		}
	}

}
