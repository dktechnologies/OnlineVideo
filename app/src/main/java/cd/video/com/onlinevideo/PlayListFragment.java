package cd.video.com.onlinevideo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.google.android.gms.internal.zzahn.runOnUiThread;

public class PlayListFragment extends Fragment {
    ListView video_list;
    ArrayList<Playlistgetset> getset;
    private String[] channelId;
    SharedPreferences sPsave;
    public static final String PREFS_NAME = "list_show";

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    public PlayListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.playlist, container, false);
        sPsave= getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        video_list=(ListView) rootView.findViewById(R.id.video_list);
        channelId =getActivity().getResources().getStringArray(R.array.key);

        getset = new ArrayList<>();
        new getYoutubeID().execute();

        return rootView;
    }

    private class getYoutubeID extends AsyncTask<String, Integer, Integer> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub
            getset.clear();
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response


//                String jsonStr = sh.makeServiceCall("https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=" + channelId[j] + "&maxResults="+getResources().getString(R.string.maxResults)+"&key="+getResources().getString(R.string.api));
                String jsonStr = sh.makeServiceCall("https://www.googleapis.com/youtube/v3/playlists?part=snippet&maxResults="+getResources().getString(R.string.maxResults)+"&channelId=UChUY0Ydv0J5WR4geWL_gnEg&key=AIzaSyCKCyYrVLEKR7VR4BFlrC5AhhzYQGRIet4");
                Log.d("CheckUrl",""+"https://www.googleapis.com/youtube/v3/playlists?part=snippet&maxResults="+getResources().getString(R.string.maxResults)+"&channelId=UChUY0Ydv0J5WR4geWL_gnEg&key=AIzaSyCKCyYrVLEKR7VR4BFlrC5AhhzYQGRIet4");
//      getset = new ArrayList<>();
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonStr);
                        JSONArray jdata = jsonObject.getJSONArray("items");
                        for (int i = 0; i < jdata.length(); i++) {

                            JSONObject c = jdata.getJSONObject(i);
                            Playlistgetset obj = new Playlistgetset();
                            obj.setId(c.getString("id"));

                            JSONObject jSnippet = c.getJSONObject("snippet");
                            obj.setTitle(jSnippet.getString("title"));



                            JSONObject jThumbnails= jSnippet.getJSONObject("thumbnails");

                            JSONObject jHigh= jThumbnails.getJSONObject("high");

                            obj.setImage(jHigh.getString("url"));

                            getset.add(obj);




                        }
//

                    } catch (final JSONException e) {

                        e.printStackTrace();

                    }
                } else {
                    // Log.e(TAG, "Couldn't get json from server.");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Please Check Your Internet!", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            return null;
        }


        @Override
        protected void onPostExecute(Integer result) {

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            LazyAdapter lazy = new LazyAdapter(getActivity(), getset);

            video_list.setAdapter(lazy);
            video_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                SharedPreferences.Editor editor = sPsave.edit();
                editor.putString("list", "playlist");
                editor.commit();

                    Intent i =new Intent(getActivity(),PlaylistVideo.class);
                    i.putExtra("key",""+getset.get(position).getId());
                    i.putExtra("name",""+getset.get(position).getTitle());
                    startActivity(i);

                }
            });
        }
    }

    public class LazyAdapter extends BaseAdapter {

        private Activity activity;
        private ArrayList<Playlistgetset> data;
        private LayoutInflater inflater = null;


        public LazyAdapter(Activity a, ArrayList<Playlistgetset> d) {
            activity = a;
            data = d;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View vi = convertView;

            if (convertView == null) {
                vi = inflater.inflate(R.layout.video_cell, null);
            }



            try {

                ImageView img =(ImageView)vi.findViewById(R.id.video_img);
                final ProgressBar progressBar= (ProgressBar)vi.findViewById(R.id.progress);
                progressBar.setVisibility(View.VISIBLE);
                TextView txt_first = (TextView) vi.findViewById(R.id.txt_video_name);
                txt_first.setText("" +data.get(position).getTitle());
                Picasso.with(activity)
                        .load( data.get(position).getImage())
                        .into(img, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {

                            }
                        });
            } catch (StringIndexOutOfBoundsException e) {
                // TODO: handle exception
                e.printStackTrace();
            }catch (NullPointerException e) {
                // TODO: handle exception
            }



            return vi;
        }
    }
}