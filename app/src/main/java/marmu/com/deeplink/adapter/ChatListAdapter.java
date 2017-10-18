package marmu.com.deeplink.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import marmu.com.deeplink.R;
import marmu.com.deeplink.activity.ChatScreenActivity;
import marmu.com.deeplink.model.ChatListModel;
import marmu.com.deeplink.utils.CircleImageView;
import marmu.com.deeplink.utils.Constants;
import marmu.com.deeplink.utils.ImageUtils;
import marmu.com.deeplink.utils.Users;

/**
 * Created by azharuddin on 3/8/17.
 */

@SuppressWarnings("unchecked")
public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> {
    private Context context;
    private List<ChatListModel> chatList;

    public ChatListAdapter(Context context, List<ChatListModel> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_common,
                        parent, false);
        return new ChatListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ChatListModel list = chatList.get(position);
        HashMap<String, Object> listMap = (HashMap<String, Object>) list.getChatListMap();
        if (listMap.containsKey("key")) {
            final String userKey = String.valueOf(listMap.get("key"));

            HashMap<String, Object> user = (HashMap<String, Object>) Users.users.get(userKey);

            String name;
            if (user.get(Constants.MY_NAME) != null) {
                name = String.valueOf(user.get(Constants.MY_NAME));
            } else {
                name = String.valueOf(user.get(Constants.PHONE_NUMBER));
            }
            final String userName = name;

            ImageUtils.loadImageToViewByURL(context, holder.profilePic,
                    Uri.parse(String.valueOf(user.get(Constants.IMG_URL))));
            holder.chatName.setText(name);
            holder.chatLastMsg.setText("Coming soon...");
            holder.chatTime.setText("Coming soon...");

            holder.chatHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent chatActivity = new Intent(context, ChatScreenActivity.class);
                    chatActivity.putExtra(Constants.HIS_KEY, userKey);
                    chatActivity.putExtra(Constants.HIS_NAME, userName);
                    chatActivity.putExtra(Constants.CHAT_KEY, list.getKey());
                    chatActivity.putExtra("isGroupChat", false);
                    context.startActivity(chatActivity);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout chatHolder;
        CircleImageView profilePic;
        TextView chatName, chatLastMsg, chatTime;

        MyViewHolder(View itemView) {
            super(itemView);
            chatHolder = itemView.findViewById(R.id.chat_holder);
            profilePic = itemView.findViewById(R.id.iv_profile_pic);
            chatName = itemView.findViewById(R.id.tv_chat_name);
            chatLastMsg = itemView.findViewById(R.id.tv_chat_last_msg);
            chatTime = itemView.findViewById(R.id.tv_chat_time);
        }
    }
}
