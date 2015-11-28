package com.wingify.ashishgoel.wingifysample.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wingify.ashishgoel.wingifysample.R;
import com.wingify.ashishgoel.wingifysample.extras.AppConstants;
import com.wingify.ashishgoel.wingifysample.utils.ImageRequestManager;

import java.util.List;

import twitter4j.QueryResult;
import twitter4j.Status;

/**
 * Created by Ashish Goel on 11/28/2015.
 */
public class HomeActivityRecylerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements AppConstants {

    Context context;
    List<Status> mData;
    boolean isMoreAllowed;

    public HomeActivityRecylerViewAdapter(Context context, List<Status> mData, boolean moreAllowed) {
        this.context = context;
        this.mData = mData;
        this.isMoreAllowed = moreAllowed;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == VIEWTYPE_LOADING) {
            View v = LayoutInflater.from(context).inflate(R.layout.loading_more_layout, viewGroup, false);
            LoadingHolder holder = new LoadingHolder(v);
            return holder;
        } else {
            View v = LayoutInflater.from(context).inflate(R.layout.home_recyler_list_item_layout, viewGroup, false);
            TweetHolder holder = new TweetHolder(v);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int pos) {
        pos = viewHolder.getAdapterPosition();
        if (getItemViewType(pos) == VIEWTYPE_TWEET) {
            TweetHolder holder = (TweetHolder) viewHolder;
            Status status = mData.get(pos);

            holder.name.setText(status.getUser().getName());
            holder.time.setText(status.getCreatedAt() + "");
            holder.tweetText.setText(status.getText());

            if (status.getUser().getBiggerProfileImageURL() != null) {
                ImageRequestManager.get(context).requestImage(context, holder.circularImageView, status.getUser().getBiggerProfileImageURL(), -1);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= mData.size()) {
            return VIEWTYPE_LOADING;
        }
        return VIEWTYPE_TWEET;
    }

    @Override
    public int getItemCount() {
        if (!isMoreAllowed)
            return mData.size();
        return mData.size() + 1;
    }

    public void addData(List<Status> tweets, boolean isMoreAllowed) {
        mData.addAll(tweets);
        this.isMoreAllowed = isMoreAllowed;
        notifyDataSetChanged();
    }

    public void addDataAtBeginningOfList(QueryResult result) {
        mData.addAll(0, result.getTweets());
        notifyDataSetChanged();
    }

    class LoadingHolder extends RecyclerView.ViewHolder {

        public LoadingHolder(View v) {
            super(v);
        }
    }

    class TweetHolder extends RecyclerView.ViewHolder {

        widgets.CircularImageView circularImageView;
        TextView tweetText, time, name;

        public TweetHolder(View v) {
            super(v);
            circularImageView = (widgets.CircularImageView) v.findViewById(R.id.circularimage);
            tweetText = (TextView) v.findViewById(R.id.comment);
            time = (TextView) v.findViewById(R.id.time);
            name = (TextView) v.findViewById(R.id.uploadrname);
        }
    }
}
