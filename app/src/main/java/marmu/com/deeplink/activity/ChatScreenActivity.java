package marmu.com.deeplink.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import marmu.com.deeplink.R;
import marmu.com.deeplink.adapter.ChatAdapter;
import marmu.com.deeplink.model.ChatModel;
import marmu.com.deeplink.utils.CircleImageView;
import marmu.com.deeplink.utils.CommonUtil;
import marmu.com.deeplink.utils.Constants;
import marmu.com.deeplink.utils.Firebase;
import marmu.com.deeplink.utils.ImageUtils;
import marmu.com.deeplink.utils.Permissions;

import static java.lang.System.currentTimeMillis;
import static java.util.UUID.randomUUID;
import static marmu.com.deeplink.utils.Constants.REQUEST_CAMERA;

@SuppressWarnings({"ConstantConditions", "unchecked", "ResultOfMethodCallIgnored", "deprecation"})
public class ChatScreenActivity extends AppCompatActivity {

    EditText inboxChat;
    CircleImageView sendButton, recordButton;
    ImageButton imageButton;

    static String chatKey;
    static String myKey;
    static String partnerKey;
    static String toolBarName;
    boolean isGroupChat = false;

    //audio variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            myKey = Constants.AUTH.getCurrentUser().getUid();
            partnerKey = bundle.getString(Constants.HIS_KEY);
            toolBarName = bundle.getString(Constants.HIS_NAME);
            chatKey = bundle.getString(Constants.CHAT_KEY);
            isGroupChat = bundle.getBoolean("isGroupChat");

            if (toolBarName != null)
                getSupportActionBar().setTitle(toolBarName);

            inboxChat = (EditText) findViewById(R.id.input_box_chat);
            sendButton = (CircleImageView) findViewById(R.id.send_button);
            recordButton = (CircleImageView) findViewById(R.id.record_button);
            imageButton = (ImageButton) findViewById(R.id.camera_button);

            // checking already we have chatted
            if (chatKey != null) {
                getChatHistory();
            } else {
                getChatListOfMine();
            }
            //audio chat or text chat
            listenForTypeofChat();

            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!TextUtils.isEmpty(inboxChat.getText())) {
                        sendMessage(inboxChat.getText().toString());
                        inboxChat.setText("");
                    }
                }
            });

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Permissions.EXTERNAL_STORAGE(ChatScreenActivity.this)) {
                        cameraIntent();
                    }
                }
            });

            recordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ChatScreenActivity.this, "Coming soon...", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getChatListOfMine() {
        Firebase.userListDBRef.child(myKey).child(Constants.MY_CHAT).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    HashMap<String, Object> myChats = (HashMap<String, Object>) dataSnapshot.getValue();
                    try {
                        for (String key : myChats.keySet()) {
                            HashMap<String, Object> chatDetails = (HashMap<String, Object>) myChats.get(key);
                            if (chatDetails.containsKey("key") && !(boolean) chatDetails.get("isGroupChat")) {
                                if (partnerKey.equalsIgnoreCase(chatDetails.get("key").toString())) {
                                    chatKey = key;
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("Exception", e.getMessage());
                    }
                }
                if (chatKey == null) {
                    chatKey = Firebase.messageDBRef.push().getKey();
                }
                getChatHistory();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", databaseError.getMessage());
            }
        });
    }


    List<ChatModel> chatHistory = new ArrayList<>();

    private void getChatHistory() {
        Query chatQuery = Firebase.messageDBRef.child(chatKey).orderByChild(Constants.DATE);
        chatQuery.keepSynced(true);
        chatQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    chatHistory = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        HashMap<String, Object> history = (HashMap<String, Object>) snapshot.getValue();
                        chatHistory.add(new ChatModel(snapshot.getKey(), history));
                    }
                    populateView();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", databaseError.getMessage());
            }
        });
    }

    private void populateView() {
        RecyclerView.Adapter adapter = new ChatAdapter(ChatScreenActivity.this, chatHistory, myKey);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.removeAllViews();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ChatScreenActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(chatHistory.size() - 1);
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                recyclerView.scrollToPosition(chatHistory.size() - 1);
            }
        });
    }

    Uri imageUri;

    private void cameraIntent() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                try {
                    Bitmap thumbnails = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), imageUri);
                    String uuid = UUID.randomUUID().toString();
                    long time = System.currentTimeMillis();
                    CommonUtil.storeIntoDisk(thumbnails, time);
                    sendImageDetails(uuid, time);
                    Firebase.storeInFirebase(ImageUtils.getImageUri(ChatScreenActivity.this, thumbnails),
                            Constants.CHAT_FILE,
                            chatKey,
                            null,
                            uuid);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }
            }
        }
    }

    //text message
    private void sendMessage(String message) {

        HashMap<String, Object> myChatDetails = new HashMap<>();
        myChatDetails.put("isGroupChat", isGroupChat);
        myChatDetails.put("name", toolBarName);
        myChatDetails.put(Constants.DATE, currentTimeMillis());
        if (isGroupChat) {

        } else {
            myChatDetails.put("key", partnerKey);
            Firebase.userListDBRef.child(myKey).child(Constants.MY_CHAT).
                    child(chatKey).updateChildren(myChatDetails);

            myChatDetails.put("key", myKey);
            Firebase.userListDBRef.child(partnerKey).child(Constants.MY_CHAT).
                    child(chatKey).updateChildren(myChatDetails);
        }


        HashMap<String, Object> messageMap = new HashMap<>();
        messageMap.put(Constants.MESSAGE, message);
        messageMap.put(Constants.FROM, myKey);
        messageMap.put(Constants.TO, partnerKey);
        messageMap.put(Constants.MSG_STATUS, Constants.SENT);
        messageMap.put(Constants.TYPE, Constants.MESSAGE);
        messageMap.put(Constants.DATE, currentTimeMillis());

        Firebase.messageDBRef
                .child(chatKey)
                .child(randomUUID().toString())
                .updateChildren(messageMap);
    }

    //Image message
    private void sendImageDetails(String uuid, long time) {

        HashMap<String, Object> myChatDetails = new HashMap<>();
        myChatDetails.put("isGroupChat", isGroupChat);
        myChatDetails.put("name", toolBarName);
        myChatDetails.put(Constants.DATE, currentTimeMillis());
        myChatDetails.put("key", partnerKey);
        Firebase.userListDBRef.child(myKey).child(Constants.MY_CHAT).
                child(chatKey).updateChildren(myChatDetails);

        myChatDetails.put("key", myKey);
        Firebase.userListDBRef.child(partnerKey).child(Constants.MY_CHAT).
                child(chatKey).updateChildren(myChatDetails);

        HashMap<String, Object> messageMap = new HashMap<>();
        messageMap.put(Constants.FROM, myKey);
        messageMap.put(Constants.TO, partnerKey);
        messageMap.put(Constants.MSG_STATUS, Constants.SENT);
        messageMap.put(Constants.TYPE, Constants.IMAGE);
        messageMap.put(Constants.DATE, time);

        Firebase.messageDBRef
                .child(chatKey)
                .child(uuid)
                .updateChildren(messageMap);
    }

    //update image url in chat
    public void updateImageURL(String url, String fileName, String uuid) {
        Firebase.messageDBRef
                .child(fileName)
                .child(uuid)
                .child(Constants.URL)
                .setValue(url);
    }

    private void listenForTypeofChat() {
        inboxChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (inboxChat.getText().toString().length() > 0) {
                    if (recordButton.getVisibility() != View.GONE) {
                        sendButton.setVisibility(View.VISIBLE);
                        recordButton.setVisibility(View.GONE);
                    }
                } else {
                    if (sendButton.getVisibility() != View.GONE) {
                        sendButton.setVisibility(View.GONE);
                        recordButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
}
