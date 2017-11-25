package marmu.com.deeplink.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import marmu.com.deeplink.R;
import marmu.com.deeplink.activity.ChatScreenActivity;
import marmu.com.deeplink.model.ContactsModel;
import marmu.com.deeplink.utils.Constants;
import marmu.com.deeplink.utils.ImageUtils;
import marmu.com.deeplink.utils.Users;

/**
 * Created by azharuddin on 3/8/17.
 */

@SuppressWarnings("unchecked")
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {
    private Context context;
    private List<ContactsModel> contactList;

    public ContactAdapter(Context context, List<ContactsModel> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_common,
                        parent, false);
        return new ContactAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.contactTime.setVisibility(View.GONE);

        ContactsModel model = contactList.get(position);

        final String userKey = String.valueOf(model.getKey());
        HashMap<String, Object> user = (HashMap<String, Object>) Users.users.get(userKey);

        final String userName = String.valueOf(model.getName());

        ImageUtils.loadImageToViewByURL(context, holder.contactPic,
                Uri.parse(String.valueOf(user.get(Constants.IMG_URL))));
        holder.contactName.setText(userName);
        holder.contactStatus.setText("Coming soon...");

        holder.contactHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatActivity = new Intent(context, ChatScreenActivity.class);
                chatActivity.putExtra(Constants.HIS_KEY, userKey);
                chatActivity.putExtra(Constants.HIS_NAME, userName);
                chatActivity.putExtra("isGroupChat", false);
                context.startActivity(chatActivity);
                ((Activity) context).finish();
            }
        });

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout contactHolder;

        TextView contactName, contactStatus, contactTime;
        ImageView contactPic;

        MyViewHolder(View itemView) {
            super(itemView);

            contactHolder = itemView.findViewById(R.id.chat_holder);

            contactName = itemView.findViewById(R.id.tv_chat_name);
            contactStatus = itemView.findViewById(R.id.tv_chat_last_msg);
            contactTime = itemView.findViewById(R.id.tv_chat_time);

            contactPic = itemView.findViewById(R.id.iv_profile_pic);
        }
    }
}
