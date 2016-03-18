package com.andy.LuFM.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * 处理sharedpreferences
 *
 * @author Andy.Wang
 */
public class PrefKit {
	/**
	 * 得到默认sp
	 *
	 * @param context
	 * @return
	 */
	public static SharedPreferences getSharedPreferences(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	/**
	 * getInt
	 *
	 * @param context
	 * @param key
	 * @param def
	 * @return
	 */
	public static int getInt(Context context, String key, int def) {
		SharedPreferences sharedPreferences = getSharedPreferences(context);
		return sharedPreferences.getInt(key, def);
	}

	public static void writeInt(Context activity, String key, int value) {
		SharedPreferences sp = getSharedPreferences(activity);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static boolean getBoolean(Context context, String key, boolean def) {
		SharedPreferences sharedPreferences = getSharedPreferences(context);
		return sharedPreferences.getBoolean(key, def);
	}

	public static void writeBoolean(Context context, String key, boolean value) {
		SharedPreferences sp = getSharedPreferences(context);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
}
