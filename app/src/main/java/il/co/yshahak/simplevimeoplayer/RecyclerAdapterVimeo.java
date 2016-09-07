package il.co.yshahak.simplevimeoplayer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vimeo.networking.model.Video;
import com.vimeo.networking.model.VideoList;

import java.util.Locale;

import static il.co.yshahak.simplevimeoplayer.VimeoPlayerActivity.EXTRA_LINK;

/**
 * Created by B.E.L on 01/09/2016.
 */

public class RecyclerAdapterVimeo extends RecyclerView.Adapter<RecyclerAdapterVimeo.ViewHolder> {

    private VideoList videoList;
    private String contentPlaceHolder;

    public RecyclerAdapterVimeo(Context context, VideoList videoList) {
        this.videoList = videoList;
        contentPlaceHolder = context.getString(R.string.vimeo_video_placeholder);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView thumbnail;
        TextView textViewContent, textViewLabel, videoLength;

        public ViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            textViewContent = (TextView) itemView.findViewById(R.id.text_content);
            textViewLabel = (TextView)itemView.findViewById(R.id.text_label);
            videoLength = (TextView)itemView.findViewById(R.id.video_duration);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String html = (String) itemView.getTag();
            if (html != null) {
                Intent intent = new Intent(itemView.getContext(), VimeoPlayerActivity.class);
                intent.putExtra(EXTRA_LINK, html);
                itemView.getContext().startActivity(intent);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_video_vimeo, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Video video = videoList.data.get(position); // just an example of getting the first video
        String html = video.embed != null ? video.embed.html : null;
        holder.itemView.setTag(html);
        String url = video.pictures.pictureForWidth(150).link;
        Picasso.with(holder.itemView.getContext())
                .load(url)
                .fit()
                .into(holder.thumbnail);
        holder.textViewLabel.setText(video.name);
        holder.textViewContent.setText(String.format(contentPlaceHolder, video.playCount(), video.likeCount()));

        String length = String.format(Locale.getDefault(), "%02d:%02d",
                ((video.duration ) % 3600) / 60, video.duration  % 60);
        holder.videoLength.setText(length);
    }

    @Override
    public int getItemCount() {
        if (videoList == null){
            return 0;
        }
        return videoList.data.size();
    }




    void setVideoList(VideoList videoList) {
        this.videoList = videoList;
        notifyDataSetChanged();
    }

}
