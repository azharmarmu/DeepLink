package marmu.com.deeplink.utils;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by azharuddin on 29/8/17.
 */

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class Users {

    public static HashMap<String, Object> users;

    public static void getAllUsers() {
        Firebase.userListDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    users = (HashMap<String, Object>) dataSnapshot.getValue();
                    if (users.containsKey(Constants.AUTH.getCurrentUser().getUid())) {
                        HashMap<String, Object> me = (HashMap<String, Object>) users.get(Constants.AUTH.getCurrentUser().getUid());
                        Constants.MY_PHONE_NUMBER = String.valueOf(me.get(Constants.PHONE_NUMBER));
                    }
                } else {
                    users = new HashMap<>();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", databaseError.getMessage());
            }
        });
    }
}
