package com.example.family;
import android.app.Application;


public class Family extends Application{
	
	
	private static boolean isPaired = false;

	public static boolean isPaired() {
		return isPaired;
	}

	public static void setPaired(boolean isPaired) {
		Family.isPaired = isPaired;
	}
	



}
