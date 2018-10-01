package com.bjohnson.newssearch.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.bjohnson.newssearch.models.Article;
import com.bjohnson.newssearch.R;

public class ArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        Article article = (Article) getIntent().getSerializableExtra("article");

        WebView webView = findViewById(R.id.wvArticle);

        webView.loadUrl(article.getWebUrl());
    }
}
