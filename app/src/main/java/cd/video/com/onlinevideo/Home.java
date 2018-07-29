package cd.video.com.onlinevideo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cd.video.com.onlinevideo.R;

public class Home extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private TabLayout tabLayout;
    static ViewPager viewPager;
    ImageView btn_menu;
    public static final String PREFS_NAME = "list_show";
    SharedPreferences sPsave;
    private final static String TAG = Home.class.getSimpleName();
    String showList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Init();


    }
    public void Init()
    {


        sPsave= getSharedPreferences(PREFS_NAME, MODE_PRIVATE);


        drawerLayout = findViewById(R.id.drawer_layout);
        btn_menu = findViewById(R.id.btn_menu);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        FragmentDrawer drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        getSupportActionBar();

        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.START);

            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        showList= sPsave.getString("list", "");
      /*  if (showList.equals("channel"))
        {
            Channel_list.setVisibility(View.VISIBLE);
            video_list.setVisibility(View.GONE);
        }
        else
        {
            Channel_list.setVisibility(View.GONE);
            video_list.setVisibility(View.VISIBLE);
        }*/
        adapter.addFragment(new PlayListFragment(), "Playlist");
        adapter.addFragment(new ChannelFragment(), "Channel");

        viewPager.setAdapter(adapter);
        Intent i = getIntent();
//        String position1 = i.getStringExtra("position1");
//        2131296328

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        Log.e("check","Back");
        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        builder.setMessage(getString(R.string.logout_message))
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finishAffinity();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

}
