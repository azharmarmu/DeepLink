package marmu.com.deeplink.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by azharuddin on 17/08/17.
 */

public class SharePref {

    private static int MODE = 0;

    public static String getToken(Context mContext) {
        SharedPreferences preferences = mContext.getSharedPreferences(Constants.LOGIN, MODE);
        return preferences.getString(Constants.TOKEN, "");
    }

    public static void setToken(Context mContext, String token) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(Constants.LOGIN, MODE).edit();
        editor.putString(Constants.TOKEN, token);
        editor.apply();
    }

}
