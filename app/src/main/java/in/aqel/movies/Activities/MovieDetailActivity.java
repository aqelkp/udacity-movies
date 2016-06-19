package in.aqel.movies.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import in.aqel.movies.fragments.MovieDetailFragment;
import in.aqel.movies.objects.Movie;
import in.aqel.movies.objects.Review;
import in.aqel.movies.objects.Video;
import in.aqel.movies.R;

public class MovieDetailActivity extends AppCompatActivity {

    boolean isFav;
    Context context;
    Gson gson = new Gson();
    ImageView ivPoster;
    LinearLayout layout;
    List<Video> videos = new ArrayList<>();
    List<Review> reviews = new ArrayList<>();
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

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(EXTRA_MOVIE, getIntent().getStringExtra(EXTRA_MOVIE));
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

}
