package marmu.com.deeplink.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import marmu.com.deeplink.R;
import marmu.com.deeplink.adapter.ChatListAdapter;
import marmu.com.deeplink.model.ChatListModel;
import marmu.com.deeplink.utils.Constants;
import marmu.com.deeplink.utils.Firebase;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class ChatFragment extends Fragment {

    private List<ChatListModel> chatList;

    public ChatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_common, container, false);
        prepareChatLists(rootView);
        return rootView;
    }

    private void prepareChatLists(final View rootView) {

        final RelativeLayout commonLayout = rootView.findViewById(R.id.common_layout);

        chatList = new ArrayList<>();
        String mykey = Constants.AUTH.getCurrentUser().getUid();
        Firebase.userListDBRef.child(mykey).child(Constants.MY_CHAT).keepSynced(true);
        Firebase.userListDBRef.child(mykey).child(Constants.MY_CHAT).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    HashMap<String, Object> chatListMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    for (String key : chatListMap.keySet()) {
                        chatList.add(new ChatListModel(key, chatListMap.get(key)));
                    }
                    populateView(rootView);
                } else {
                    RelativeLayout.LayoutParams params = new RelativeLayout
                            .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                    TextView noView = new TextView(getActivity());
                    noView.setLayoutParams(params);

                    noView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
                    noView.setPadding(16, 16, 16, 16);
                    noView.setText("No Chat History");
                    noView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    noView.setTypeface(null, Typeface.BOLD);
                    noView.setGravity(Gravity.CENTER);
                    commonLayout.addView(noView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", databaseError.getMessage());
            }
        });


    }

    private void populateView(View rootView) {
        RecyclerView.Adapter adapter = new ChatListAdapter(getContext(), chatList);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.removeAllViews();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }
}
