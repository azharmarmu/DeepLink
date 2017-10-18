package marmu.com.deeplink.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import marmu.com.deeplink.R;
import marmu.com.deeplink.activity.ImagePreview;
import marmu.com.deeplink.model.ChatModel;
import marmu.com.deeplink.utils.CommonUtil;
import marmu.com.deeplink.utils.Constants;
import marmu.com.deeplink.utils.Permissions;

/**
 * Created by azharuddin on 3/8/17.
 */

@SuppressWarnings("unchecked")
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<ChatModel> chatModel;
    private String myKey;

    public ChatAdapter(Context context, List<ChatModel> chatModel, String myKey) {
        this.context = context;
        this.chatModel = chatModel;
        this.myKey = myKey;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case 1:
                viewHolder = new MessageSenderViewHolder(inflater.inflate(R.layout.chat_cell_sender_message, parent, false));
                break;
            case 2:
                viewHolder = new MessageReceiverViewHolder(inflater.inflate(R.layout.chat_cell_receiver_message, parent, false));
                break;
            case 3:
                viewHolder = new ImageSenderViewHolder(inflater.inflate(R.layout.chat_cell_sender_image, parent, false));
                break;
            case 4:
                viewHolder = new ImageReceiverViewHolder(inflater.inflate(R.layout.chat_cell_receiver_image, parent, false));
                break;
            default:
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        ChatModel chats = chatModel.get(position);
        HashMap<String, Object> chatList = (HashMap<String, Object>) chats.getChatListMap();

        String from = String.valueOf(chatList.get(Constants.FROM));
        String type = String.valueOf(chatList.get(Constants.TYPE));
        if (type.equalsIgnoreCase(Constants.MESSAGE)) {
            if (from.equalsIgnoreCase(myKey)) {
                senderMessage((MessageSenderViewHolder) holder, chatList);
            } else {
                receiverMessage((MessageReceiverViewHolder) holder, chatList);
            }
        } else if (type.equalsIgnoreCase(Constants.IMAGE)) {
            if (from.equalsIgnoreCase(myKey)) {
                senderImage((ImageSenderViewHolder) holder, chatList);
            } else {
                receiverImage((ImageReceiverViewHolder) holder, chatList);
            }
        }/* else if (type.equalsIgnoreCase(Constants.AUDIO)) {
            if (from.equalsIgnoreCase(myKey)) {
                //senderAudio((ImageSenderViewHolder) holder, chatList, position);
            } else {
                //receiverAudio((ImageReceiverViewHolder) holder, chatList, position);
            }
        }*/
    }

    private void senderMessage(MessageSenderViewHolder holder, HashMap<String, Object> chatList) {
        holder.senderMsg.setText(String.valueOf(chatList.get(Constants.MESSAGE)));
        holder.senderMsgTime.setText("" + CommonUtil.getDate((Long) chatList.get(Constants.DATE), "h:mm a"));
        if (String.valueOf(chatList.get(Constants.MSG_STATUS)).equalsIgnoreCase(Constants.SENT)) {
            Picasso.with(context).load(R.drawable.ic_done_sent)
                    .placeholder(R.drawable.ic_done_sent)
                    .into(holder.senderMsgStatus);
        } else if (String.valueOf(chatList.get(Constants.MSG_STATUS)).equalsIgnoreCase(Constants.DELIVERED)) {
            Picasso.with(context).load(R.drawable.ic_done_delivered)
                    .placeholder(R.drawable.ic_done_delivered)
                    .into(holder.senderMsgStatus);
        } else if (String.valueOf(chatList.get(Constants.MSG_STATUS)).equalsIgnoreCase(Constants.READ)) {
            Picasso.with(context).load(R.drawable.ic_done_read)
                    .placeholder(R.drawable.ic_done_read)
                    .into(holder.senderMsgStatus);
        }
    }

    private void receiverMessage(MessageReceiverViewHolder holder, HashMap<String, Object> chatList) {
        holder.receiverMsg.setText(String.valueOf(chatList.get(Constants.MESSAGE)));
        holder.receiverMsgTime.setText("" + CommonUtil.getDate((Long) chatList.get(Constants.DATE), "h:mm a"));
    }

    private void senderImage(final ImageSenderViewHolder holder, final HashMap<String, Object> chatList) {
        if (Permissions.EXTERNAL_STORAGE(context)) {
            final String pathName = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/MyChat/Chat Images/" + chatList.get(Constants.DATE) + ".jpg";
            if (new File(pathName).exists()) {
                Bitmap bmp = BitmapFactory.decodeFile(pathName);
                holder.senderImg.setImageBitmap(bmp);
            } else {
                if (chatList.containsKey(Constants.URL)) {
                    Picasso.with(context).load(String.valueOf(chatList.get(Constants.URL)))
                            .placeholder(R.drawable.people)
                            .into(holder.senderImg);
                    MyTaskParams params = new MyTaskParams(String.valueOf(chatList.get(Constants.URL)), (Long) chatList.get(Constants.DATE));
                    new getBitmapFromURL().execute(params);
                }
            }

            holder.senderImgTime.setText("" + CommonUtil.getDate((Long) chatList.get(Constants.DATE), "h:mm a"));
            if (String.valueOf(chatList.get(Constants.MSG_STATUS)).equalsIgnoreCase(Constants.SENT)) {
                Picasso.with(context).load(R.drawable.ic_done_sent)
                        .placeholder(R.drawable.ic_done_sent)
                        .into(holder.senderImgStatus);
            } else if (String.valueOf(chatList.get(Constants.MSG_STATUS)).equalsIgnoreCase(Constants.DELIVERED)) {
                Picasso.with(context).load(R.drawable.ic_done_delivered)
                        .placeholder(R.drawable.ic_done_delivered)
                        .into(holder.senderImgStatus);
            } else if (String.valueOf(chatList.get(Constants.MSG_STATUS)).equalsIgnoreCase(Constants.READ)) {
                Picasso.with(context).load(R.drawable.ic_done_read)
                        .placeholder(R.drawable.ic_done_read)
                        .into(holder.senderImgStatus);
            }

            holder.senderImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ImagePreview.class);
                    if (new File(pathName).exists()) {
                        intent.putExtra(Constants.IMG_URL, pathName);
                        intent.putExtra(Constants.TYPE, Constants.BITMAP);
                    } else {
                        if (chatList.containsKey(Constants.URL)) {
                            intent.putExtra(Constants.IMG_URL, String.valueOf(chatList.get(Constants.URL)));
                        }
                    }

                    context.startActivity(intent);
                }
            });
        }
    }

    private void receiverImage(ImageReceiverViewHolder holder, HashMap<String, Object> chatList) {
        if (Permissions.EXTERNAL_STORAGE(context)) {
            final String imgUrl = String.valueOf(chatList.get(Constants.URL));
            Picasso.with(context).load(imgUrl)
                    .placeholder(R.drawable.people)
                    .into(holder.receiverImg);

            holder.receiverImgTime.setText("" + CommonUtil.getDate((Long) chatList.get(Constants.DATE), "h:mm a"));
            holder.receiverImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ImagePreview.class);
                    intent.putExtra(Constants.IMG_URL, imgUrl);
                    context.startActivity(intent);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return chatModel.size();
    }

    private class MessageSenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMsg, senderMsgTime;
        ImageView senderMsgStatus;

        private MessageSenderViewHolder(View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.message_sender);
            senderMsgTime = itemView.findViewById(R.id.message_sender_time);
            senderMsgStatus = itemView.findViewById(R.id.message_sender_status);
        }
    }

    private class MessageReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView receiverMsg, receiverMsgTime;

        private MessageReceiverViewHolder(View itemView) {
            super(itemView);
            receiverMsg = itemView.findViewById(R.id.message_receiver);
            receiverMsgTime = itemView.findViewById(R.id.message_receiver_time);
        }

    }

    private class ImageSenderViewHolder extends RecyclerView.ViewHolder {
        ImageView senderImg, senderImgStatus;
        TextView senderImgTime;

        private ImageSenderViewHolder(View itemView) {
            super(itemView);
            senderImg = itemView.findViewById(R.id.image_sender);
            senderImgTime = itemView.findViewById(R.id.image_sender_time);
            senderImgStatus = itemView.findViewById(R.id.image_sender_status);
        }
    }

    private class ImageReceiverViewHolder extends RecyclerView.ViewHolder {
        ImageView receiverImg;
        TextView receiverImgTime;

        private ImageReceiverViewHolder(View itemView) {
            super(itemView);
            receiverImg = itemView.findViewById(R.id.image_sender);
            receiverImgTime = itemView.findViewById(R.id.image_receiver_time);
        }
    }

    @Override
    public int getItemViewType(int position) {

        ChatModel chats = chatModel.get(position);
        HashMap<String, Object> chatList = (HashMap<String, Object>) chats.getChatListMap();

        try {
            String from = String.valueOf(chatList.get(Constants.FROM));
            String type = String.valueOf(chatList.get(Constants.TYPE));

            if (type.equalsIgnoreCase(Constants.MESSAGE)) {
                if (from.equalsIgnoreCase(myKey)) {
                    return 1;
                } else {
                    return 2;
                }
            } else if (type.equalsIgnoreCase(Constants.IMAGE)) {
                if (from.equalsIgnoreCase(myKey)) {
                    return 3;
                } else {
                    return 4;
                }
            } else if (type.equalsIgnoreCase(Constants.AUDIO)) {
                if (from.equalsIgnoreCase(myKey)) {
                    return 5;
                } else {
                    return 6;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    private class getBitmapFromURL extends AsyncTask<MyTaskParams, Void, Void> {

        @Override
        protected Void doInBackground(MyTaskParams... params) {

            try {
                URL url = new URL(params[0].url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                CommonUtil.storeIntoDisk(bitmap, params[0].time);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class MyTaskParams {
        String url;
        long time;

        MyTaskParams(String url, long time) {
            this.url = url;
            this.time = time;
        }
    }

}
