package marmu.com.deeplink.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.List;

import marmu.com.deeplink.R;

/**
 * Created by azharuddin on 3/8/17.
 */

@SuppressWarnings("unchecked")
public class CallListAdapter extends RecyclerView.Adapter<CallListAdapter.MyViewHolder> {
    private Context context;
    private List<String> callList;


    private int firstVisibleItem, lastVisibleItem;

    public CallListAdapter(Context context, final List<String> callList, RecyclerView recyclerView) {
        this.context = context;
        this.callList = callList;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();


            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                            firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                            int initialItem = 0;
                            if (firstVisibleItem <= 10) initialItem = 0;
                            else if (firstVisibleItem > 10) initialItem = firstVisibleItem - 10;

                            for (int visibleItem = initialItem; visibleItem <= lastVisibleItem; visibleItem++) {
                                if (callList.get(visibleItem).equalsIgnoreCase("video")) {
                                    setVideo();
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                        }
                    });
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_call,
                        parent, false);
        return new CallListAdapter.MyViewHolder(itemView);
    }

    private VideoView videoLayout;

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.videoPlayerView.setVisibility(View.GONE);
        holder.textView.setVisibility(View.GONE);
        if (callList.get(position).equalsIgnoreCase("video")) {
            videoLayout = holder.videoPlayerView;
        } else {
            holder.textView.setVisibility(View.VISIBLE);
            holder.textView.setText(callList.get(position));
        }
    }

    int seekPosition = 0;

    private void setVideo() {
        videoLayout.setVisibility(View.VISIBLE);
        String path = "android.resource://" + context.getPackageName() + "/" + R.raw.search_doctors;
        videoLayout.setVideoURI(Uri.parse(path));

        videoLayout.seekTo(seekPosition);
        videoLayout.start();
        videoLayout.setZOrderOnTop(true);
        videoLayout.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
                seekPosition = mediaPlayer.getCurrentPosition();
            }
        });
    }

    @Override
    public int getItemCount() {
        return callList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        VideoView videoPlayerView;
        TextView textView;

        MyViewHolder(View itemView) {
            super(itemView);
            videoPlayerView = itemView.findViewById(R.id.video_player);
            textView = itemView.findViewById(R.id.text_view);
        }
    }
}
