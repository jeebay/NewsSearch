package com.bjohnson.newssearch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bjohnson.newssearch.models.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArticleArrayAdapter extends ArrayAdapter<Article> {
    public ArticleArrayAdapter(Context context, List<Article> articles) {
        super(context, android.R.layout.simple_list_item_1, articles);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // get data item for position
        Article article = this.getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_article_result, parent, false);
        }

        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        tvTitle.setText(article.getHeadline());

        TextView tvSnippet = (TextView) convertView.findViewById(R.id.tvSnippet);
        tvSnippet.setText(article.getSnippet());

        String thumbnail = article.getThumbnail();

        ImageView imageView = (ImageView) convertView.findViewById(R.id.ivImage);
        imageView.setImageResource(0);
        if (!TextUtils.isEmpty(thumbnail)) {
            Picasso.get()
                    .load(thumbnail)
                    .centerCrop()
                    .fit()
                    .into(imageView);
        }

        return convertView;

    }
}
