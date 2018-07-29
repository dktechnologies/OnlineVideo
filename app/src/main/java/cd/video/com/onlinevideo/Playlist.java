package cd.video.com.onlinevideo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import co.ceryle.radiorealbutton.RadioRealButton;

public class Playlist extends AppCompatActivity {
    ArrayList<getsetclass> getset;
    ListView video_list;
    TextView txtTitle;
    String strKey,strName;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        colorStatusBar();
        Init();
    }
    public void Init() {
        video_list = findViewById(R.id.video_list);
        txtTitle = findViewById(R.id.txtTitle);

        AdMobBanner();
        AdMobInterstitial();

        Intent i=getIntent();
        strKey = i.getStringExtra("key");
        strName = i.getStringExtra("name");
        txtTitle.setText(strName);
        getset = new ArrayList<>();
        new getYoutubeID().execute();

    }
    private class getYoutubeID extends AsyncTask<String, Integer, Integer> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(Playlist.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub

            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response

            String jsonStr = sh.makeServiceCall("https://www.googleapis.com/youtube/v3/search?part=snippet&channelId="+strKey+"&maxResults="+getResources().getString(R.string.maxResults)+"&key="+getResources().getString(R.string.api));
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
                            Log.d("Checkgeteset123",""+getset.size());


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

            LazyAdapter lazy = new LazyAdapter(Playlist.this, getset);

            video_list.setAdapter(lazy);
            video_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("CheckPosition2",""+getset.get(position).getPosition());

                    Intent i =new Intent(Playlist.this,ShowVideo.class);
                    i.putExtra("key",""+getset.get(position).getKey());
                    i.putExtra("name",""+getset.get(position).getName());
                    i.putExtra("Channelkey",""+strKey);
                    i.putExtra("Channelname",""+strName);
                    i.putExtra("play","playlist");
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
                final ProgressBar progressBar= (ProgressBar)vi.findViewById(R.id.progress);
                progressBar.setVisibility(View.VISIBLE);
                TextView txt_first = (TextView) vi.findViewById(R.id.txt_video_name);
                txt_first.setText("" +data.get(position).getName());
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();

           /* Intent i = new Intent(Playlist.this, Home.class);
            startActivity(i);*/
//            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


    }

    public void colorStatusBar()
    {

        Window window = Playlist.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor( Playlist.this,R.color.colorPrimary));
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
