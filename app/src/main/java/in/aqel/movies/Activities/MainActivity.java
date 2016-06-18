package in.aqel.movies.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.aqel.movies.Adapters.MoviesAdapter;
import in.aqel.movies.Fragments.MovieDetailFragment;
import in.aqel.movies.Objects.Movie;
import in.aqel.movies.R;
import in.aqel.movies.Utils.AppConstants;

public class MainActivity extends AppCompatActivity {

    boolean mTwoPane;
    Context context;
    List<Movie> movies = new ArrayList<>();
    ProgressDialog progressDialog;
    RecyclerView recycler;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    String mode = "popular";

    private static String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null)
            mode = savedInstanceState.getString("mode");

        Log.d(LOG_TAG, "OnCreate " + mode);
        context = MainActivity.this;

        recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setLayoutManager(new GridLayoutManager(this, 2));
        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }

        fetchMovieList();
    }

    private void fetchMovieList() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Fetching movie list");
        progressDialog.show();

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://api.themoviedb.org/3/movie/" + mode + "?api_key=" + AppConstants.API_KEY;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();
                        Gson gson = new Gson();
                        try {
                            JSONArray json = new JSONObject(response).getJSONArray("results");
                            movies = gson.fromJson( json.toString(), new TypeToken<List<Movie>>(){}.getType());
                            adapter = new MoviesAdapter(context, movies);
                            recycler.setAdapter(adapter);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId()) {
            case R.id.menu_popular:
                    mode = "popular";
                    fetchMovieList();
                break;
            case R.id.menu_rating:
                mode = "top_rated";
                fetchMovieList();
                break;
            case R.id.menu_order_fav:
                showFavourites();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showFavourites() {
        SharedPreferences preferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        String listString = preferences.getString(MovieDetailActivity.PREF_FAVOURITES, "");

        if (listString.isEmpty() || listString == null){
            movies = new ArrayList<>();
        } else movies = new Gson().fromJson(listString, new TypeToken<ArrayList<Movie>>(){}.getType());
        if (movies.size() < 1){
            Toast.makeText(context, "You haven't added any movie as favourite", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        adapter = new MoviesAdapter(context, movies);
        recycler.setAdapter(adapter);

    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString("mode", mode);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mode = savedInstanceState.getString("mode");
        Log.d(LOG_TAG, "Restore state " + mode);

    }

    public void openDetails(Movie movie){

        if (mTwoPane){

            Bundle arguments = new Bundle();
            arguments.putString(MovieDetailActivity.EXTRA_MOVIE, new Gson().toJson(movie));
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();

        } else {

            Intent intent = new Intent(context, MovieDetailActivity.class);
            intent.putExtra(MovieDetailActivity.EXTRA_MOVIE, new Gson().toJson(movie));
            context.startActivity(intent);

        }
    }


}
