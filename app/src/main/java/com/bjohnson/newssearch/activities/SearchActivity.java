package com.bjohnson.newssearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;

import com.bjohnson.newssearch.ArticleArrayAdapter;
import com.bjohnson.newssearch.EndlessScrollListener;
import com.bjohnson.newssearch.R;
import com.bjohnson.newssearch.models.Article;
import com.bjohnson.newssearch.models.Filters;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {
    EditText etQuery;
    GridView gvResults;
    Button btnSearch;
    ImageButton btnFilter;
    ArrayList<Article> articles;
    ArticleArrayAdapter articleAdapter;
    Filters filters;
    int totalSearchResults = 0;
    int searchPage = 0;

    private final int FILTER_REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupViews();
    }

    public void setupViews() {
        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnFilter = (ImageButton) findViewById(R.id.btnFilter);

        // create new filters object
        filters = new Filters();

        // set up articles and item listener
        articles = new ArrayList<>();
        articleAdapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(articleAdapter);
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                Article article = articles.get(position);
                i.putExtra("article", article);
                startActivity(i);
            }
        });
        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                searchPage = page;
                doSearch(false);
                return totalItemsCount <= totalSearchResults;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == FILTER_REQUEST_CODE) {
            // Extract name value from result extras
            filters = (Filters) data.getSerializableExtra("filters");
            searchPage = 0;
            doSearch(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    static String formatNewsDesk(ArrayList<String> newsDesks) {
        ArrayList<String> formmattedDesks = new ArrayList<String>();

        for (int i = 0; i < newsDesks.size(); i++) {
            formmattedDesks.add(String.format("\"%s\"",  newsDesks.get(i)));
        }

        return "news_desk:(" + TextUtils.join(" ", formmattedDesks) + ")";
    }

    private RequestParams makeSearchQuery(String query) {
        RequestParams params = new RequestParams();
        params.put("api-key", "227c750bb7714fc39ef1559ef1bd8329");
        params.put("page", searchPage);
        params.put("q", query);
        params.put("sort", filters.getSortOrder());

        if (!TextUtils.isEmpty(filters.getBeginDate())) {
            params.put("begin_date", filters.getBeginDate());
        }

        if (!filters.getNewsDesk().isEmpty()) {
            params.put("fq", formatNewsDesk(filters.getNewsDesk()));
        }

        return params;
    }

    public void onArticleSearch(View view) {
        searchPage = 0;
        doSearch(true);
    }

    public void doSearch(final boolean clear) {
        String query = etQuery.getText().toString();
        AsyncHttpClient client = new AsyncHttpClient();

        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = makeSearchQuery(query);

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJSONResults = null;

                try {
                    articleJSONResults = response.getJSONObject("response").getJSONArray("docs");
                    totalSearchResults = response.getJSONObject("response").getJSONObject("meta").getInt("hits");
                    if (clear) articleAdapter.clear();
                    articleAdapter.addAll(Article.fromJSONArray(articleJSONResults));
                    Log.d("DEBUG", articles.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onFilter(View view) {
        Intent i = new Intent(this, FilterActivity.class);

        // pass current filters value to FilterActivity
        i.putExtra("filters", filters);

        startActivityForResult(i, FILTER_REQUEST_CODE);
    }
}
