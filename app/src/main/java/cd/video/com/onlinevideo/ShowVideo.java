package cd.video.com.onlinevideo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShowVideo extends YouTubeBaseActivity  implements YouTubePlayer.OnInitializedListener {
    YouTubePlayerView youTubePlayerView;
    private static final int RECOVERY_REQUEST = 1;
    TextView txt_name;
    String key,name;
    ArrayList<getsetclass> getset;
    ArrayList<Playlistgetset> playgetset;
    ListView video_list;
    String strKey,strName,strPage,strPlaylistId,strPlaylistName;
    int intPosition;
    LazyAdapter lazy;
    String[] channelId;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video);
        colorStatusBar();


    }

    @Override
    protected void onResume() {
        super.onResume();
        Init();
    }

    public void Init()
    {
        txt_name=findViewById(R.id.txt_title);
        video_list=findViewById(R.id.video_list);
        AdMobBanner();
        AdMobInterstitial();

        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtubePlay);
        channelId =getResources().getStringArray(R.array.key);
        Intent i=getIntent();
        key=i.getStringExtra("key");
        name=i.getStringExtra("name");

        strKey = i.getStringExtra("Channelkey");
        strName=i.getStringExtra("Channelname");

        strPage = i.getStringExtra("play");
        strPlaylistId =i.getStringExtra("playlistID");
        strPlaylistName = i.getStringExtra("playlistName");

        txt_name.setText(name);

        youTubePlayerView.initialize(YouTubeConfig.getApiKey(), this);
        getset = new ArrayList<>();
        playgetset=new ArrayList<>();
        if (strPage.equals("home"))
        {
            new getYoutubeIDPlay().execute();
        }
        else
        {
            new getYoutubeID().execute();
        }


    }
    private class getYoutubeID extends AsyncTask<String, Integer, Integer> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(ShowVideo.this);
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

            String jsonStr = sh.makeServiceCall("https://www.googleapis.com/youtube/v3/search?part=snippet&channelId="+strKey+"&maxResults=50&key=AIzaSyCKCyYrVLEKR7VR4BFlrC5AhhzYQGRIet4");
            getset = new ArrayList<>();

            if (jsonStr != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    JSONArray jdata = jsonObject.getJSONArray("items");
                    for (int i = 0; i < jdata.length(); i++) {

                        JSONObject c = jdata.getJSONObject(i);

                        JSONObject jId = c.getJSONObject("id");
                        JSONObject jSnippet = c.getJSONObject("snippet");
                        if (jId.optString("videoId").length()==11)
                        {
                            getsetclass obj = new getsetclass();
                            obj.setKey(jId.optString("videoId"));
                            obj.setName(jSnippet.optString("title"));
//                            obj.setPosition(i);
                            getset.add(obj);


                        }


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
                        Toast.makeText(getApplicationContext(),"Please Check Your Internet!",Toast.LENGTH_LONG).show();
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

            lazy = new LazyAdapter(ShowVideo.this, getset);

            video_list.setAdapter(lazy);

            video_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    lazy.notifyDataSetChanged();
                    Log.d("CheckPosition35",""+position);

                    Intent i =new Intent(ShowVideo.this,ShowVideo.class);
                    i.putExtra("key",""+getset.get(position).getKey());
                    i.putExtra("name",""+getset.get(position).getName());
                    i.putExtra("Channelkey",""+strKey);
                    i.putExtra("Channelname",""+strName);
                    i.putExtra("play",strPage);
//                    i.putExtra("position",""+position);
                    startActivity(i);
                    if (getResources().getString(R.string.adsShow).equals("yes")) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        }
                    }
                }
            });

        }
    }

    private class getYoutubeIDPlay extends AsyncTask<String, Integer, Integer> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(ShowVideo.this);
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

            String jsonStr = sh.makeServiceCall("https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults="+getResources().getString(R.string.maxResults)+"&playlistId="+strPlaylistId+"&key="+getResources().getString(R.string.api));
            Log.d("CheckUrl",""+"https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults="+getResources().getString(R.string.maxResults)+"&playlistId="+strPlaylistId+"&key="+getResources().getString(R.string.api));
//      getset = new ArrayList<>();
            if (jsonStr != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    JSONArray jdata = jsonObject.getJSONArray("items");
                    for (int i = 0; i < jdata.length(); i++) {

                        JSONObject c = jdata.getJSONObject(i);
                        Playlistgetset obj = new Playlistgetset();
//

                        JSONObject jSnippet = c.getJSONObject("snippet");
                        obj.setTitle(jSnippet.getString("title"));
                        obj.setPlaylistId(jSnippet.getString("playlistId"));

                        JSONObject jThumbnails= jSnippet.getJSONObject("thumbnails");
                        JSONObject jHigh= jThumbnails.getJSONObject("high");
                        obj.setImage(jHigh.getString("url"));

                        JSONObject jResourceId =jSnippet.getJSONObject("resourceId");
                        obj.setId(jResourceId.getString("videoId"));

                        playgetset.add(obj);


                    }
//

                } catch (final JSONException e) {

                    e.printStackTrace();

                }
            }else {
                    // Log.e(TAG, "Couldn't get json from server.");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Please Check Your Internet!", Toast.LENGTH_LONG).show();
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
            LazyAdapterPlay lazy = new LazyAdapterPlay(ShowVideo.this, playgetset);

            video_list.setAdapter(lazy);
            video_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent i =new Intent(ShowVideo.this,ShowVideo.class);


                    i.putExtra("key",""+playgetset.get(position).getId());
                    i.putExtra("name",""+playgetset.get(position).getTitle());
                    i.putExtra("playlistID",""+playgetset.get(position).getPlaylistId());
                    i.putExtra("playlistName",""+strPlaylistName);

                    i.putExtra("play",strPage);

//                    i.putExtra("position",""+position);
                    startActivity(i);
                    if (getResources().getString(R.string.adsShow).equals("yes")) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        }
                    }
                }
            });

        }
    }

    public class LazyAdapter extends BaseAdapter {

        private Activity activity;
        private ArrayList<getsetclass> data;
        private LayoutInflater inflater = null;


        public LazyAdapter(Activity a, ArrayList<getsetclass> d) {
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
                RelativeLayout rl_img=(RelativeLayout)vi.findViewById(R.id.rl_img);
                TextView txt_first = (TextView) vi.findViewById(R.id.txt_video_name);
                final ProgressBar progressBar= (ProgressBar)vi.findViewById(R.id.progress);

                /*if (data.get(position).getKey().contains(key))
                {
                    data.remove(data.get(intPosition));

                }*/
                    progressBar.setVisibility(View.VISIBLE);
                    txt_first.setText("" + data.get(position).getName());
                    Picasso.with(activity)
                            .load("http://img.youtube.com/vi/" + data.get(position).getKey() + "/0.jpg")
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
    public class LazyAdapterPlay extends BaseAdapter {

        private Activity activity;
        private ArrayList<Playlistgetset> data;
        private LayoutInflater inflater = null;


        public LazyAdapterPlay(Activity a, ArrayList<Playlistgetset> d) {
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
    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int minutes = seconds / 60;
        int hours = minutes / 60;

        return (hours == 0 ? "" : hours) + String.format("%02d:%02d", minutes % 60, seconds % 60);
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);

        if (!wasRestored) {
            player.cueVideo(key); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format("Check", errorReason.toString());

        }
    }
    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {

        @Override
        public void onBuffering(boolean arg0) {
            Log.d("Check","onBuffering");

        }

        @Override
        public void onPaused() {
            Log.d("Check","onPaused");
        }

        @Override
        public void onPlaying() {
            Log.d("Check","onPlaying");

        }

        @Override
        public void onSeekTo(int arg0) {
            Log.d("Check","onSeekTo");

        }

        @Override
        public void onStopped() {
            Log.d("Check","onStopped");

        }

    };

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {

        @Override
        public void onAdStarted() {
            Log.d("Check","onAdStarted");
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason arg0) {
            Log.d("Check","onError");

        }

        @Override
        public void onLoaded(String arg0) {
            Log.d("Check","onLoaded");

        }

        @Override
        public void onLoading() {
            Log.d("Check","onLoading");
        }

        @Override
        public void onVideoEnded() {
            Log.d("Check","onVideoEnded");
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }

        }

        @Override
        public void onVideoStarted() {
            Log.d("Check","onVideoStarted");

        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(YouTubeConfig.getApiKey(), this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubePlayerView;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (strPage.equals("playlist"))
        {
            Intent i = new Intent(ShowVideo.this, Playlist.class);
            i.putExtra("key", strKey);
            i.putExtra("name", strName);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
//            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        else if (strPage.equals("home"))
        {
            Intent i = new Intent(ShowVideo.this, PlaylistVideo.class);
            i.putExtra("key",""+strPlaylistId);
            i.putExtra("name",""+strPlaylistName);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
//            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        }

    }

    public void colorStatusBar()
    {

        Window window = ShowVideo.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor( ShowVideo.this,R.color.colorPrimary));
        }

    }

    public void AdMobBanner()
    {
        if (getResources().getString(R.string.adsShow).equals("yes"))
        {
            mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    mAdView.setVisibility(View.VISIBLE);
                    Log.d("Banner","onAdLoaded");
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.
                    Log.d("Banner","onAdFailedToLoad");
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.
                }

                @Override
                public void onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when when the user is about to return
                    // to the app after tapping on an ad.
                }
            });
        }
    }
    public void AdMobInterstitial()
    {
        if (getResources().getString(R.string.adsShow).equals("yes")) {
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ID));
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.

                    Log.d("InterstitialAd","onAdLoaded");
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.
                    Log.d("InterstitialAd",""+errorCode);
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when the ad is displayed.
                }

                @Override
                public void onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when when the interstitial ad is closed.
                }
            });
        }
    }
}
