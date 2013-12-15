package com.fandg.santashuffle;

import java.util.ArrayList;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;

public class Family extends Application {
    
    public static String             _APP_VERSION      = "";
    private static int               _currentSelection = 0;
    
    public static ArrayList<Integer> chosenIndicies    = new ArrayList<Integer>();
    private static boolean           isHideGifters     = false;
    
    private static boolean           isPaired          = false;
    private static ArrayList<String> names             = new ArrayList<String>();
    private static ArrayList<String> recipients        = new ArrayList<String>();
    
    public static ArrayList<Integer> getChosenIndicies() {
        return Family.chosenIndicies;
    }
    
    public static int getCurrentSelection() {
        return Family._currentSelection;
    }
    
    public static ArrayList<String> getNames() {
        return Family.names;
    }
    
    public static ArrayList<String> getRecipients() {
        return Family.recipients;
    }
    
    public static boolean isHideGifters() {
        return Family.isHideGifters;
    }
    
    public static boolean isPaired() {
        return Family.isPaired;
    }
    
    public static boolean isChosen() {
        return !Family.isPaired;
    }
    
    public static void setChosen(boolean isChosen) {
        Family.isPaired = !isChosen;
    }
    
    public static void setCurrentSelection(int currentSelection) {
        Family._currentSelection = currentSelection;
    }
    
    public static void setHideGifters(boolean isHideGifters) {
        Family.isHideGifters = isHideGifters;
    }
    
    public static void setPaired(boolean isPaired) {
        Family.isPaired = isPaired;
    }
    
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Context context = this.getApplicationContext(); // or
        // activity.getApplicationContext()
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();
        
        // Obtains the app version based in the manifest file.
        try {
            Family._APP_VERSION = packageManager.getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            
        }
    }
    
}
