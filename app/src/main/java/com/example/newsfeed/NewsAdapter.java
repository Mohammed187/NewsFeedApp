package com.example.newsfeed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(Context context, List<News> news) {
        super(context, 0, news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View newsItem = convertView;

        if (newsItem == null) {
            newsItem = LayoutInflater.from(getContext()).inflate(R.layout.news_item, parent, false);
        }

        News news =getItem(position);

        TextView title = newsItem.findViewById(R.id.news_title);
        title.setText(news.getTitle());

        TextView date = newsItem.findViewById(R.id.news_date);
        date.setText(news.getDate());

        ImageView thumbnail = newsItem.findViewById(R.id.news_thumbnail);
        Picasso.get().load(news.getThumbnail())
                .fit()
                .centerInside()
                .into(thumbnail);

        return newsItem;
    }

}
