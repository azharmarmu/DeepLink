package marmu.com.deeplink.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
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
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    FrameLayout progressBarHolder;

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

        progressBarHolder = rootView.findViewById(R.id.progressBarHolder);
        enableProgressBar();


        String mykey = Constants.AUTH.getCurrentUser().getUid();
        Query chatListQuery = Firebase.userListDBRef.child(mykey).child(Constants.MY_CHAT).orderByChild(Constants.DATE);
        chatListQuery.keepSynced(true);
        chatListQuery.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatList = new ArrayList<>();
                TextView noView = rootView.findViewById(R.id.tv_no_chat_history);
                if (dataSnapshot.getValue() != null) {
                    noView.setVisibility(View.GONE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        HashMap<String, Object> chatListMap = (HashMap<String, Object>) snapshot.getValue();
                        chatList.add(new ChatListModel(snapshot.getKey(), chatListMap));
                    }
                    populateView(rootView);
                } else {
                    noView.setVisibility(View.VISIBLE);
                }
                disableProgressBar();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                disableProgressBar();
                Log.e("Error", databaseError.getMessage());
            }
        });
    }

    private void enableProgressBar() {
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);
    }

    private void disableProgressBar() {
        outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        progressBarHolder.setAnimation(outAnimation);
        progressBarHolder.setVisibility(View.GONE);
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
