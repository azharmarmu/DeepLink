package marmu.com.deeplink.activity;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import marmu.com.deeplink.R;
import marmu.com.deeplink.adapter.NewGroupAdapter;
import marmu.com.deeplink.adapter.SelectedMemberAdapter;
import marmu.com.deeplink.interfaces.SelectedMembersListener;
import marmu.com.deeplink.model.ContactsModel;
import marmu.com.deeplink.utils.Constants;
import marmu.com.deeplink.utils.Permissions;
import marmu.com.deeplink.utils.Users;

@SuppressWarnings("unchecked")
public class NewGroupActivity extends AppCompatActivity implements SelectedMembersListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        if (Permissions.CONTACTS(NewGroupActivity.this)) {

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

    public List<String> allPhoneNumber = new ArrayList<>();
    public List<String> allKey = new ArrayList<>();

    public void getUsers() {
        HashMap<String, Object> users = Users.users;
        if (users != null && users.size() > 0) {
            for (String key : users.keySet()) {
                HashMap<String, Object> user = (HashMap<String, Object>) users.get(key);
                if (user.containsKey(Constants.PHONE_NUMBER)) {
                    if (!Constants.MY_PHONE_NUMBER.equalsIgnoreCase(user.get(Constants.PHONE_NUMBER).toString())) {
                        allPhoneNumber.add(String.valueOf(user.get(Constants.PHONE_NUMBER)));
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

    static List<ContactsModel> selectedMembers = new ArrayList<>();

    @Override
    public void onSelectedMember(ContactsModel member) {
        selectedMembers.add(member);
        populateSelectedMemberView();
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

    public void createGroup(View view) {
        if (selectedMembers.size() > 0) {

        }
    }

    @Override
    public void onBackPressed() {
        selectedMembers.clear();
        super.onBackPressed();
    }
}
