package com.example.family;
import android.app.Application;


public class Family extends Application{
	
	
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
