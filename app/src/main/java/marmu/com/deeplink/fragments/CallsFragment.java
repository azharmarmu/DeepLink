package marmu.com.deeplink.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import marmu.com.deeplink.R;
import marmu.com.deeplink.adapter.CallListAdapter;
import marmu.com.deeplink.model.ChatListModel;

@SuppressWarnings("unchecked")
public class CallsFragment extends Fragment {

    private List<ChatListModel> chatList = new ArrayList<>();

    public CallsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_common, container, false);
        //prepareChatLists(rootView);
        populateView(rootView);
        return rootView;
    }

    private void prepareChatLists(final View rootView) {


        /*Firebase.chatListDBRef.keepSynced(true);
        Firebase.chatListDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    HashMap<String, Object> chatListMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    changeMapToList(chatListMap);
                    populateView(rootView);
                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", databaseError.getMessage());
            }
        });*/


    }

    private void changeMapToList(HashMap<String, Object> chatListMap) {
        chatList = new ArrayList<>();
        if (chatListMap != null) {
            for (String key : chatListMap.keySet()) {
                HashMap<String, Object> chatListDetails = (HashMap<String, Object>) chatListMap.get(key);
                chatList.add(new ChatListModel(key, chatListDetails));
            }
        }
    }


    private void populateView(View rootView) {
        final List<String> callList = new ArrayList<>();
        callList.add("video");
        for (int i = 0; i < 10; i++) {
            callList.add(i + 1 + "");
        }
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.removeAllViews();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);
        CallListAdapter adapter = new CallListAdapter(getContext(), callList, recyclerView);
        recyclerView.setAdapter(adapter);
    }
}
