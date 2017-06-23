package com.tzidis.android.newsapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.R.attr.data;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    // Store a member variable for the newsArticles
    public List<NewsArticle> mNewsArticles;
    // Store the context for easy access
    private Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImage;
        public TextView mTitle;
        public TextView mSection;
        public TextView mDate;
        public RelativeLayout mLayout;


        public ViewHolder(View v) {
            super(v);

            mImage = (ImageView) v.findViewById(R.id.info_image);
            mTitle = (TextView) v.findViewById(R.id.info_title);
            mSection = (TextView) v.findViewById(R.id.info_section);
            mDate = (TextView) v.findViewById(R.id.info_date);
            mLayout = (RelativeLayout) v.findViewById(R.id.news_layout);
        }
    }

    // Provide a suitable constructor
    public NewsAdapter (Activity context, List<NewsArticle> newsArticles) {
        mNewsArticles = newsArticles;
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View newsView = inflater.inflate(R.layout.news_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(newsView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // Get the data model based on position
        final NewsArticle currentNewsArticle = mNewsArticles.get(position);

        //Change the Date format
        String currentDate = currentNewsArticle.getFirstPublicationDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss'Z'");
        Date newDate = null;
        try {
            newDate = simpleDateFormat.parse(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        simpleDateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy");
        String formattedDate = simpleDateFormat.format(newDate);

        // replace the contents of the view with that element

        holder.mTitle.setText(currentNewsArticle.getHeadline());
        holder.mSection.setText(currentNewsArticle.getSectionName());
        holder.mDate.setText(mContext.getString(R.string.published_on) + formattedDate);
        Glide.with(holder.itemView.getContext())
                .load(currentNewsArticle.getThumbnail())
                .into(holder.mImage);

        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(currentNewsArticle.getWebUrl()));
                mContext.startActivity(intent);
            }
        });
    }


    public void addAll(List<NewsArticle> newsArticles) {
        mNewsArticles.clear();
        mNewsArticles.addAll(newsArticles);
        notifyDataSetChanged();
    }

    public void clear(){
        mNewsArticles.clear();
        notifyDataSetChanged();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        return mNewsArticles.size();
    }
}