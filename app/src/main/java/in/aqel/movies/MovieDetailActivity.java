package in.aqel.movies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    Gson gson = new Gson();
    ImageView ivPoster;
    Movie movie;
    TextView tvTitle, tvOverview, tvReleaseDate, tvRating;

    public static String EXTRA_MOVIE = "movieString";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        movie = gson.fromJson(getIntent().getExtras().getString(EXTRA_MOVIE), Movie.class);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvOverview = (TextView) findViewById(R.id.tvOverview);
        tvReleaseDate = (TextView) findViewById(R.id.tvReleaseDate);
        tvRating = (TextView) findViewById(R.id.tvRating);
        ivPoster = (ImageView) findViewById(R.id.ivPoster);

        tvTitle.setText(movie.getOriginal_title());
        tvOverview.setText(movie.getOverview());
        tvReleaseDate.setText(movie.getRelease_date());
        tvRating.setText(movie.getVote_average() + "/10");

        String imageUrl = "http://image.tmdb.org/t/p/w185/" + movie.getPoster_path();
        Picasso.with(this).load(imageUrl).into(ivPoster);

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
