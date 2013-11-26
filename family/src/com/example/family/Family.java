package com.example.family;
import java.util.ArrayList;

import android.app.Application;
import android.widget.ListView;


public class Family extends Application{
	
	private static ArrayList<String> names = new ArrayList<String>();
	private static ArrayList<String> recipients = new ArrayList<String>();

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
	



}
