package marmu.com.deeplink.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.appinvite.FirebaseAppInvite;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.single.PermissionListener;

import marmu.com.deeplink.R;
import marmu.com.deeplink.adapter.ViewPagerAdapter;
import marmu.com.deeplink.firebase.Invite;
import marmu.com.deeplink.fragments.CallsFragment;
import marmu.com.deeplink.fragments.ChatFragment;
import marmu.com.deeplink.fragments.StatusFragment;
import marmu.com.deeplink.utils.Constants;
import marmu.com.deeplink.utils.DialogUtils;
import marmu.com.deeplink.utils.Users;

@SuppressWarnings("unchecked")
public class MainActivity extends AppCompatActivity
        implements PermissionListener, PermissionRequestErrorListener {
    private static final String TAG = MainActivity.class.getClass().getSimpleName();
    ImageButton fabMain;
    public static int tab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        receiveDeepLink();
        remoteConfig();
        fabMain = findViewById(R.id.ib_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Let'sChat");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        isUserExists();
    }

    private void receiveDeepLink() {

        // Check for App Invite invitations and launch deep-link activity if possible.
        // Requires that an Activity is registered in AndroidManifest.xml to handle
        // deep-link URLs.
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData data) {
                        if (data == null) {
                            Log.d(TAG, "getInvitation: no data");
                            return;
                        }

                        // Get the deep link
                        Uri deepLink = data.getLink();

                        // Extract invite
                        FirebaseAppInvite invite = FirebaseAppInvite.getInvitation(data);
                        if (invite != null) {
                            String invitationId = invite.getInvitationId();
                            DialogUtils.appToastShort(MainActivity.this, invitationId);
                        }

                        // Handle the deep link
                        // [START_EXCLUDE]
                        Log.d(TAG, "deepLink:" + deepLink);
                        if (deepLink != null) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setPackage(getPackageName());
                            intent.setData(deepLink);

                            //startActivity(intent);
                        }
                        // [END_EXCLUDE]
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getDynamicLink:onFailure", e);
                    }
                });
    }

    private void remoteConfig() {
        final FirebaseRemoteConfig mRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings remoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .build();
        mRemoteConfig.setConfigSettings(remoteConfigSettings);
        mRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        // cache expiration in seconds
        long cacheExpiration = 3600;

        //expire the cache immediately for development mode.
        if (mRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        // fetch
        mRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // task successful. Activate the fetched data
                            mRemoteConfig.activateFetched();

                            int color = Color.parseColor(mRemoteConfig.getString(Constants.BACK_GROUND));

                            //update views?
                            CoordinatorLayout mainCoordinator = (CoordinatorLayout) findViewById(R.id.coordinator_main);
                            mainCoordinator.setBackgroundColor(color);
                        } else {
                            DialogUtils.appToastShort(MainActivity.this, "Issue in Remote Config");
                        }
                    }
                });
    }

    private void isUserExists() {
        try {
            if (Constants.AUTH.getCurrentUser() != null) {
                Log.e(TAG, "onAuthStateChanged ---> signed_in : " + Constants.AUTH.getCurrentUser());

                //Tab View-Pager
                ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
                setupViewPager(viewPager);

                TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                tabLayout.setupWithViewPager(viewPager);
                Users.getAllUsers();
            } else {
                indexMethod();
            }
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
            indexMethod();
        }
    }

    private void indexMethod() {
        Log.v(TAG, "onAuthStateChanged ---> signed_out");
        Intent indexActivity = new Intent(MainActivity.this, IndexActivity.class);
        indexActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(indexActivity);
        finish();
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChatFragment(), Constants.CHATS);
        adapter.addFragment(new StatusFragment(), Constants.STATUS);
        adapter.addFragment(new CallsFragment(), Constants.CALLS);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tab = position;
                switch (position) {
                    case 0:
                        fabMain.setImageDrawable(ContextCompat
                                .getDrawable(MainActivity.this, R.drawable.ic_person));
                        break;
                    case 1:
                        fabMain.setImageDrawable(ContextCompat
                                .getDrawable(MainActivity.this, R.drawable.ic_status));
                        break;
                    case 2:
                        fabMain.setImageDrawable(ContextCompat
                                .getDrawable(MainActivity.this, R.drawable.ic_local_phone));
                        break;

                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void fabClick(View view) {
        switch (tab) {
            case 0:
                Dexter.withActivity(this)
                        .withPermission(
                                Manifest.permission.READ_CONTACTS)
                        .withListener(this)
                        .check();
                break;
            case 1:
                //startActivity(new Intent(MainActivity.this, ContactActivity.class));
                break;
            case 2:
                //startActivity(new Intent(MainActivity.this, ContactActivity.class));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_search:
                break;
            case R.id.action_new_group:
                startActivity(new Intent(MainActivity.this, NewGroupActivity.class));
                break;
            case R.id.action_invite:
                new Invite().inviteLink(MainActivity.this, TAG);
                break;
            case R.id.action_settings:
                startActivity(new Intent(getApplicationContext(), Settings.class));
                break;
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onPermissionGranted(PermissionGrantedResponse response) {
        startActivity(new Intent(MainActivity.this, ContactActivity.class));
    }

    @Override
    public void onPermissionDenied(PermissionDeniedResponse response) {
        DialogUtils.appSnakeBar(MainActivity.this,
                "Contact permission is required");
    }

    @Override
    public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
                                                   PermissionToken token) {
        token.continuePermissionRequest();
    }

    @Override
    public void onError(DexterError error) {
        Log.e("Dexter", "There was an error: " + error.toString());
    }
}
