package marmu.com.deeplink.adapter;

import android.content.Context;
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
import marmu.com.deeplink.interfaces.SelectedMembersListener;
import marmu.com.deeplink.model.ContactsModel;
import marmu.com.deeplink.utils.Constants;
import marmu.com.deeplink.utils.ImageUtils;
import marmu.com.deeplink.utils.Users;

/**
 * Created by azharuddin on 17/10/17.
 */

@SuppressWarnings("unchecked")
public class NewGroupAdapter extends RecyclerView.Adapter<NewGroupAdapter.MyViewHolder> {
    private Context context;
    private List<ContactsModel> contactList;

    private SelectedMembersListener memberListener;

    public void onMemberSelectedListener(SelectedMembersListener memberListener) {
        this.memberListener = memberListener;
    }

    public NewGroupAdapter(Context context, List<ContactsModel> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_common,
                        parent, false);
        return new NewGroupAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.contactTime.setVisibility(View.GONE);

        final ContactsModel model = contactList.get(position);

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
                memberListener.onSelectedMember(model);
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
