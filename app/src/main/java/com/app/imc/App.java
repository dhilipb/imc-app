package com.app.imc;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.TypedValue;

public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
    
	public static AssetManager getAssetManager() {
    	return App.getContext().getAssets();
    }
	
	public static float dpTopx(int dp) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, App.getContext().getResources().getDisplayMetrics());
	}

}
