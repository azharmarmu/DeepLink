package marmu.com.deeplink;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.database.FirebaseDatabase;

import marmu.com.deeplink.utils.FontsOverride;

/**
 * Created by azharuddin on 1/8/17.
 */

@SuppressWarnings("deprecation")
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/Asap-Regular.otf");
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/Asap-Regular.otf");
    }
}
