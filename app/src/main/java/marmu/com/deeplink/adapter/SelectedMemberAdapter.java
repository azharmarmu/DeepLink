package marmu.com.deeplink.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
public class SelectedMemberAdapter extends RecyclerView.Adapter<SelectedMemberAdapter.MyViewHolder> {
    private Context context;
    private List<ContactsModel> selectedMembers;

    private SelectedMembersListener memberListener;

    public void onMemberSelectedListener(SelectedMembersListener memberListener) {
        this.memberListener = memberListener;
    }

    public SelectedMemberAdapter(Context context, List<ContactsModel> selectedMembers) {
        this.context = context;
        this.selectedMembers = selectedMembers;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_selected_members,
                        parent, false);
        return new SelectedMemberAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ContactsModel model = selectedMembers.get(position);

        final String userKey = String.valueOf(model.getKey());
        HashMap<String, Object> user = (HashMap<String, Object>) Users.users.get(userKey);

        ImageUtils.loadImageToViewByURL(context, holder.contactPic,
                Uri.parse(String.valueOf(user.get(Constants.IMG_URL))));

        final String userName = String.valueOf(model.getName());

        holder.contactName.setText(userName);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                memberListener.onRemovedMember(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return selectedMembers.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView contactName;
        ImageView contactPic;

        MyViewHolder(View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.tv_member_name);
            contactPic = itemView.findViewById(R.id.iv_member_pic);
        }
    }
}
