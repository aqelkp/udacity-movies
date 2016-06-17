package in.aqel.movies.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import in.aqel.movies.Objects.Video;
import in.aqel.movies.R;

/**
 * Created by Ahammad on 03/06/16.
 */
public class VideosAdapter extends BaseAdapter {

    Context context;
    List<Video> videos = new ArrayList<>();

    public VideosAdapter(Context context, List<Video> videos) {
        this.context = context;
        this.videos = videos;
    }

    @Override
    public int getCount() {
        return videos.size();
    }

    @Override
    public Object getItem(int position) {
        return videos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);

        return (view);
    }
}
