package in.aqel.movies.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.aqel.movies.Objects.Movie;
import in.aqel.movies.Objects.Review;
import in.aqel.movies.Objects.Video;
import in.aqel.movies.R;
import in.aqel.movies.Utils.AppConstants;

public class MovieDetailActivity extends AppCompatActivity {

    boolean isFav;
    Context context;
    Gson gson = new Gson();
    ImageView ivPoster;
    LinearLayout layout;
    List<Video> videos = new ArrayList<>();
    List<Review> reviews = new ArrayList<>();
    Menu menu;
    Movie movie;
    ProgressDialog progressDialog;
    TextView tvTitle, tvOverview, tvReleaseDate, tvRating;

    public static String EXTRA_MOVIE = "movieString";
    public static String LOG_TAG = "MovieDetailActivity";
    public static String PREF_FAVOURITES = "favourites";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        context = MovieDetailActivity.this;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        movie = gson.fromJson(getIntent().getExtras().getString(EXTRA_MOVIE), Movie.class);

        ivPoster = (ImageView) findViewById(R.id.ivPoster);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvOverview = (TextView) findViewById(R.id.tvOverview);
        tvReleaseDate = (TextView) findViewById(R.id.tvReleaseDate);
        tvRating = (TextView) findViewById(R.id.tvRating);
        layout = (LinearLayout) findViewById(R.id.layout);

        tvTitle.setText(movie.getOriginal_title());
        tvOverview.setText(movie.getOverview());
        tvReleaseDate.setText(movie.getRelease_date());
        tvRating.setText(movie.getVote_average() + "/10");

        String imageUrl = "http://image.tmdb.org/t/p/w185/" + movie.getPoster_path();
        Picasso.with(this).load(imageUrl).into(ivPoster);

        fetchMovieVideos();

        isFav = getFavPref();
        Log.d(LOG_TAG, "onCreate called");

    }


    private void fetchMovieVideos() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Fetching movie trailers");
        progressDialog.show();

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://api.themoviedb.org/3/movie/" + movie.getId() + "/videos?api_key=" + AppConstants.API_KEY;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();
                        fetchMovieReviews();

                        Log.d(LOG_TAG, response);
                        Gson gson = new Gson();
                        try {


                            JSONArray json = new JSONObject(response).getJSONArray("results");
                            videos = gson.fromJson( json.toString(), new TypeToken<List<Video>>(){}.getType());
                            for (final Video video : videos){

                                View view = LayoutInflater.from(context).inflate(R.layout.item_video, layout, false);
                                TextView tvName = (TextView) view.findViewById(R.id.tvName);
                                tvName.setText(video.getName());
                                view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        openYoutube(video.getKey());
                                    }
                                });
                                layout.addView(view);

                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void fetchMovieReviews() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Fetching movie reviews");
        progressDialog.show();

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://api.themoviedb.org/3/movie/" + movie.getId() + "/reviews?api_key=" + AppConstants.API_KEY;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();
                        Log.d(LOG_TAG, response);
                        Gson gson = new Gson();
                        try {


                            JSONArray json = new JSONObject(response).getJSONArray("results");
                            reviews = gson.fromJson( json.toString(), new TypeToken<List<Review>>(){}.getType());
                            for (final Review review : reviews){

                                View view = LayoutInflater.from(context).inflate(R.layout.item_review,
                                        layout, false);
                                TextView tvAuthor = (TextView) view.findViewById(R.id.tvAuthor);
                                TextView tvContent = (TextView) view.findViewById(R.id.tvContent);

                                tvAuthor.setText(review.getAuthor());
                                tvContent.setText(review.getContent());

                                layout.addView(view);
                                Log.d(LOG_TAG, review.getAuthor() );

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    private void openYoutube(String key) {
        String url = "https://www.youtube.com/watch?v=" + key;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;

            case R.id.menu_fav:

                if (isFav){
                    isFav = false;
                    item.setIcon(android.R.drawable.btn_star_big_off);
                } else {
                    isFav = true;
                    item.setIcon(android.R.drawable.btn_star_big_on);
                }

                saveFavPref();

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void saveFavPref() {

        SharedPreferences preferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        String listString = preferences.getString(PREF_FAVOURITES, "");

        ArrayList<Movie> favourites;
        if (listString.isEmpty()) favourites = new ArrayList<>();
        else favourites = gson.fromJson(listString, new TypeToken<ArrayList<Movie>>(){}.getType());

        if (isFav){

            favourites.add(movie);

            for (Movie movie : favourites) Log.d(LOG_TAG, movie.getOriginal_title());

        } else {

            favourites.remove(movie);
            for (int i =0; i< favourites.size(); i++){
                if (favourites.get(i).getId().equals(this.movie.getId()))
                    favourites.remove(i);
            }

            for (Movie movie : favourites) Log.d(LOG_TAG, movie.getOriginal_title());


        }

        listString = gson.toJson(favourites);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_FAVOURITES, listString);
        editor.commit();

    }

    private boolean getFavPref() {

        SharedPreferences preferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        String listString = preferences.getString(PREF_FAVOURITES, "");

        ArrayList<Movie> favourites;
        if (listString.isEmpty()) favourites = new ArrayList<>();
        else favourites = gson.fromJson(listString, new TypeToken<ArrayList<Movie>>(){}.getType());

        for (int i =0; i< favourites.size(); i++){
            if (favourites.get(i).getId().equals(this.movie.getId()))
                return true;
        }

        return false;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.d(LOG_TAG, "onCreateOtionsMen called");

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_detail, menu);

        this.menu = menu;
        if (isFav) menu.findItem(R.id.menu_fav).setIcon(android.R.drawable.btn_star_big_on);
        else menu.findItem(R.id.menu_fav).setIcon(android.R.drawable.btn_star_big_off);
        return true;
    }
}
