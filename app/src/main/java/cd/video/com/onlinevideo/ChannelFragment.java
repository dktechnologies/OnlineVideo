package cd.video.com.onlinevideo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static android.content.Context.MODE_PRIVATE;

public class ChannelFragment extends Fragment {

    ListView Channel_list;
    private String[] channelName,channelId,channelImage;
    SharedPreferences sPsave;
    public static final String PREFS_NAME = "list_show";

    public ChannelFragment() {
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
        View rootView = inflater.inflate(R.layout.channel, container, false);
        sPsave= getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Channel_list = rootView.findViewById(R.id.Channel_list);
        channelId = getActivity().getResources().getStringArray(R.array.key);
        channelName = getActivity().getResources().getStringArray(R.array.name);
        channelImage= getActivity().getResources().getStringArray(R.array.image);
        MyAdapter adapter = new MyAdapter(getActivity(),channelName);
        Channel_list.setAdapter(adapter);
        Channel_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = sPsave.edit();
                editor.putString("list", "channel");
                editor.commit();
                Intent i = new Intent(getActivity(),Playlist.class);
                i.putExtra("key",channelId[position]);
                i.putExtra("name",channelName[position]);
                startActivity(i);

            }
        });
        return rootView;
    }

    public class MyAdapter extends BaseAdapter {


        private Context context_;
        private String[] items;

        public MyAdapter(Context context, String[] items) {
            this.context_ = context;
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return items[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater mInflater = (LayoutInflater)
                        context_.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

                convertView = mInflater.inflate(R.layout.video_cell, null);
            }

            ImageView img =(ImageView)convertView.findViewById(R.id.video_img);
            final ProgressBar progressBar= (ProgressBar)convertView.findViewById(R.id.progress);
            TextView txtFirstWord=(TextView)convertView.findViewById(R.id.txtFirstWord);
            txtFirstWord.setVisibility(View.GONE);

            TextView txt_first = (TextView) convertView.findViewById(R.id.txt_video_name);
            txt_first.setText("" +items[position]);
            progressBar.setVisibility(View.VISIBLE);
            Picasso.with(getActivity())
                    .load(channelImage[position])
                    .into(img, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {

                        }
                    });
            return convertView;
        }
    }

}