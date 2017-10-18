package marmu.com.deeplink.firebase;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import marmu.com.deeplink.utils.Constants;

/**
 * Created by azharuddin on 8/8/17.
 */

public class Invite {

    public String buildLongDynamicLink() {
        return FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.google.co.in/"))
                .setDynamicLinkDomain(Constants.DYNAMIC_LINK_DOMAIN)
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                // Open links with com.example.ios on iOS
                .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                .buildDynamicLink().getUri().toString();
    }

    public void inviteLink(final Activity activity, final String TAG) {
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(buildLongDynamicLink()))
                .buildShortDynamicLink()
                .addOnCompleteListener(activity, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            Log.e(TAG, shortLink.toString());
                            Log.e(TAG, flowchartLink.toString());
                            Intent intent = new Intent();
                            String msg = "My App : " + shortLink.toString();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT, msg);
                            intent.setType("text/plain");
                            activity.startActivity(intent);

                        }
                    }
                });
    }
}
