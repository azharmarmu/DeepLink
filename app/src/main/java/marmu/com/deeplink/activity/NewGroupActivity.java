package marmu.com.deeplink.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import marmu.com.deeplink.R;
import marmu.com.deeplink.adapter.NewGroupAdapter;
import marmu.com.deeplink.adapter.SelectedMemberAdapter;
import marmu.com.deeplink.interfaces.SelectedMembersListener;
import marmu.com.deeplink.model.ContactsModel;
import marmu.com.deeplink.utils.Constants;
import marmu.com.deeplink.utils.DialogUtils;
import marmu.com.deeplink.utils.Firebase;
import marmu.com.deeplink.utils.Permissions;
import marmu.com.deeplink.utils.Users;

import static java.lang.System.currentTimeMillis;
import static java.util.UUID.randomUUID;

@SuppressWarnings("unchecked")
public class NewGroupActivity extends AppCompatActivity implements SelectedMembersListener, Serializable {
    static List<ContactsModel> selectedMembers = new ArrayList<>();

    public List<String> allPhoneNumber = new ArrayList<>();
    public List<String> allKey = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        if (Permissions.CONTACTS(NewGroupActivity.this)) {
            selectedMembers.clear();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Select Contact");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }

            getUsers();

        } else {
            finish();
        }
    }

    public void getUsers() {
        HashMap<String, Object> users = Users.users;
        if (users != null && users.size() > 0) {
            for (String key : users.keySet()) {
                HashMap<String, Object> user = (HashMap<String, Object>) users.get(key);
                if (user.containsKey(Constants.PHONE_NUMBER)) {
                    if (!Constants.MY_PHONE_NUMBER.equalsIgnoreCase(user.get(Constants.PHONE_NUMBER).toString())) {
                        String phNumber = String.valueOf(user.get(Constants.PHONE_NUMBER));
                        phNumber = phNumber.replace(" ", "");
                        if (phNumber.contains("+91")) {
                            phNumber = phNumber.substring(3);
                        }

                        allPhoneNumber.add(phNumber);
                        allKey.add(key);
                    }
                }
            }

            ContactAsyncTask asyncTask = new ContactAsyncTask();
            asyncTask.execute();
        }
    }

    private class ContactAsyncTask extends AsyncTask<Void, Void, List<ContactsModel>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<ContactsModel> doInBackground(Void... params) {
            List<ContactsModel> contactsList = new ArrayList<>();

            Cursor c = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    null, null, null);
            if (c != null) {
                while (c.moveToNext()) {

                    String contactName = c
                            .getString(c
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phNumber = c
                            .getString(c
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    phNumber = phNumber.replace(" ", "");
                    if (phNumber.contains("+91")) {
                        phNumber = phNumber.substring(3);
                    }
                    if (allPhoneNumber.contains(phNumber)) {
                        int index = allPhoneNumber.indexOf(phNumber);
                        boolean phoneNumberExists = false;
                        for (int i = 0; i < contactsList.size(); i++) {
                            ContactsModel model = contactsList.get(i);
                            if (model.getPhone().equalsIgnoreCase(phNumber)) {
                                phoneNumberExists = true;
                                break;
                            }
                        }
                        if (!phoneNumberExists) {
                            contactsList.add(new ContactsModel(allKey.get(index), contactName, phNumber));
                        }
                    }
                }
            }
            if (c != null) {
                c.close();
            }

            return contactsList;
        }

        @Override
        protected void onPostExecute(List<ContactsModel> contactsList) {
            super.onPostExecute(contactsList);
            //DialogUtils.dismissProgressDialog();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setSubtitle(contactsList.size() + " contacts");
            }
            populateMemberView(contactsList);
        }
    }

    private void populateMemberView(List<ContactsModel> contactsList) {
        NewGroupAdapter adapter = new NewGroupAdapter(NewGroupActivity.this, contactsList);
        adapter.onMemberSelectedListener(NewGroupActivity.this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rcv_members);
        recyclerView.removeAllViews();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(NewGroupActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onSelectedMember(ContactsModel member) {
        if (!selectedMembers.contains(member)) {
            selectedMembers.add(member);
            populateSelectedMemberView();
        } else {
            DialogUtils.appToastShort(NewGroupActivity.this, "Already added");
        }
    }

    @Override
    public void onRemovedMember(ContactsModel member) {
        selectedMembers.remove(member);
        populateSelectedMemberView();
    }

    private void populateSelectedMemberView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rcv_selected_members);
        if (selectedMembers.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            SelectedMemberAdapter adapter = new SelectedMemberAdapter(NewGroupActivity.this, selectedMembers);
            adapter.onMemberSelectedListener(NewGroupActivity.this);
            recyclerView.removeAllViews();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(NewGroupActivity.this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
        } else {
            recyclerView.setVisibility(View.GONE);
        }
    }

    @SuppressLint("InflateParams")
    public void createGroup(View view) {
        if (selectedMembers.size() > 1) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(NewGroupActivity.this);
            LayoutInflater inflater = (LayoutInflater) NewGroupActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View dialogView = inflater.inflate(R.layout.dialog_name, null);
            dialogBuilder.setView(dialogView);

            final EditText etName = dialogView.findViewById(R.id.et_my_name);
            etName.setHint("Group Name");

            dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                @SuppressWarnings("ConstantConditions")
                public void onClick(DialogInterface dialog, int whichButton) {
                    String groupName = etName.getText().toString();
                    if (!groupName.isEmpty()) {
                        String chatKey = Firebase.messageDBRef.push().getKey();

                        HashMap<String, Object> botDetails = new HashMap<>();
                        botDetails.put(Constants.MESSAGE, "You created group " + groupName);
                        botDetails.put(Constants.DATE, currentTimeMillis());
                        botDetails.put(Constants.TYPE, Constants.BOT);

                        //Bot message
                        Firebase.messageDBRef.child(chatKey).child(randomUUID().toString()).updateChildren(botDetails);

                        HashMap<String, Object> myChatDetails = new HashMap<>();
                        myChatDetails.put("name", groupName);
                        myChatDetails.put("no_members", selectedMembers.size());
                        myChatDetails.put("admin", Constants.AUTH.getCurrentUser().getUid());
                        myChatDetails.put("isGroupChat", true);
                        myChatDetails.put(Constants.DATE, currentTimeMillis());

                        //updating selected members chatList
                        for (int i = 0; i < selectedMembers.size(); i++) {
                            String key = selectedMembers.get(i).getKey();
                            Firebase.userListDBRef.child(key).child(Constants.MY_CHAT)
                                    .child(chatKey).updateChildren(myChatDetails);
                        }
                        //updating my chatList
                        Firebase.userListDBRef.child(Constants.AUTH.getCurrentUser().getUid())
                                .child(Constants.MY_CHAT)
                                .child(chatKey).updateChildren(myChatDetails);

                        Intent chatActivity = new Intent(NewGroupActivity.this, ChatScreenActivity.class);
                        chatActivity.putExtra("isGroupChat", true);
                        chatActivity.putExtra(Constants.CHAT_KEY, chatKey);
                        chatActivity.putExtra(Constants.HIS_NAME, groupName);
                        startActivity(chatActivity);

                        finish();
                    }
                }
            });
            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                }
            });
            AlertDialog b = dialogBuilder.create();
            b.show();

        } else {
            DialogUtils.appToastShort(NewGroupActivity.this, "Minimum 3 members need to be added for group chat");
        }
    }

    @Override
    public void onBackPressed() {
        selectedMembers.clear();
        super.onBackPressed();
    }
}
